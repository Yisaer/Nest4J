package com.qunhe.util.nest.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qunhe.util.nest.data.*;
import com.qunhe.util.nest.util.coor.ClipperCoor;
import de.lighti.clipper.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author yisa
 */
public class Placementworker {
    public NestPath binPolygon;
    public Config config;
    public Map<String, List<NestPath>> nfpCache ;
    private static Gson gson = new GsonBuilder().create();

    /**
     *
     * @param binPolygon    底板参数
     * @param config    设置
     * @param nfpCache  nfp列表
     */
    public Placementworker(NestPath binPolygon, Config config, Map<String, List<NestPath>> nfpCache) {
        this.binPolygon = binPolygon;
        this.config = config;
        this.nfpCache = nfpCache;
    }

    /**
     * 根据板件列表与旋转角列表，通过nfp,计算板件在底板上的位置，并返回这个种群的fitness
     * @param paths
     * @return
     */
    public Result placePaths(List<NestPath> paths){
        List<NestPath> rotated = new ArrayList<NestPath>();
        for(int i = 0 ; i<paths.size() ; i++){
            NestPath r = GeometryUtil.rotatePolygon2Polygon(paths.get(i) , paths.get(i).getRotation());
            r.setRotation(paths.get(i).getRotation());
            r.setSource(paths.get(i).getSource());
            r.setId( paths.get(i).getId());
            rotated.add(r);
        }
        paths = rotated;

        List<List<Vector>>allplacements = new ArrayList<List<Vector>>();
        double fitness = 0 ;
        double binarea = Math.abs(GeometryUtil.polygonArea(this.binPolygon));
        String key = null;
        List<NestPath> nfp = null;

        while(paths.size() > 0 ){

            List<NestPath> placed = new ArrayList<NestPath>();
            List<Vector> placements = new ArrayList<Vector>();

            fitness += 1;
            double minwidth = Double.MAX_VALUE;
            for(int i = 0; i <paths.size() ; i ++){

                NestPath path = paths.get(i);

                //inner NFP
                key = gson.toJson(new NfpKey(-1 , path.getId() , true , 0 , path.getRotation()));

                if(!nfpCache.containsKey(key)){
                    continue;
                }

                List<NestPath> binNfp = nfpCache.get(key);



                // ensure exists
                boolean error = false;
                for(int j = 0 ; j < placed.size();j++){
                    key = gson.toJson(new NfpKey(placed.get(j).getId() , path.getId() , false , placed.get(j).getRotation() , path.getRotation()));
                    if(nfpCache.containsKey(key)) nfp = nfpCache.get(key);
                    else{
                        error = true;
                        break;
                    }
                }
                if(error ) {
                    continue;
                }


                Vector position = null;
                if(placed.size() == 0){
                    // first placement , put it on the lefth
                    for(int j = 0 ; j< binNfp.size(); j ++){
                        for(int k = 0 ; k< binNfp.get(j).size() ; k ++){
                            if (position == null || binNfp.get(j).get(k).x - path.get(0).x < position.x) {
                                position = new Vector(
                                        binNfp.get(j).get(k).x - path.get(0).x,
                                        binNfp.get(j).get(k).y - path.get(0).y,
                                        path.getId(),
                                        path.getRotation()
                                );
                            }
                        }
                    }
                    placements.add(position);
                    placed.add(path);
                    continue;
                }

                Paths clipperBinNfp = new Paths();

                for(int j = 0; j <binNfp.size(); j ++){
                    NestPath binNfpj = binNfp.get(j);
                    clipperBinNfp.add(scaleUp2ClipperCoordinates(binNfpj));
                }
                DefaultClipper clipper = new DefaultClipper();
                Paths combinedNfp = new Paths();


                for(int j = 0 ; j<placed.size(); j ++){
                    key = gson.toJson(new NfpKey(placed.get(j).getId() , path.getId() , false , placed.get(j).getRotation() ,path.getRotation()));
                    nfp = nfpCache.get(key);
                    if(nfp == null ){
                        continue;
                    }

                    for(int k = 0 ; k <nfp.size();k++){
                        Path clone = scaleUp2ClipperCoordinates(nfp.get(k));
                        for(int m = 0 ; m< clone.size() ; m++){
                            long clx = clone.get(m).getX();
                            long cly = clone.get(m).getY();
                            clone.get(m).setX(clx + (long)(placements.get(j).x * Config.CLIIPER_SCALE));
                            clone.get(m).setY(cly + (long)(placements.get(j).y * Config.CLIIPER_SCALE));
                        }
                        clone = clone.cleanPolygon(0.0001 * Config.CLIIPER_SCALE);
                        double area = Math.abs(clone.area());
                        if( clone.size() > 2 && area > 0.1 * Config.CLIIPER_SCALE * Config.CLIIPER_SCALE){
                            clipper.addPath(clone , Clipper.PolyType.SUBJECT , true);
                        }
                    }
                }
                if( !clipper.execute(Clipper.ClipType.UNION , combinedNfp , Clipper.PolyFillType.NON_ZERO , Clipper.PolyFillType.NON_ZERO)){
                    continue;
                }

                //difference with bin polygon
                Paths finalNfp = new Paths();
                clipper = new DefaultClipper();

                clipper.addPaths( combinedNfp , Clipper.PolyType.CLIP , true);
                clipper.addPaths(clipperBinNfp , Clipper.PolyType.SUBJECT , true);
                if( !clipper.execute(Clipper.ClipType.DIFFERENCE , finalNfp , Clipper.PolyFillType.NON_ZERO , Clipper.PolyFillType.NON_ZERO)){
                    continue;
                }

                finalNfp = finalNfp.cleanPolygons(0.0001 * Config.CLIIPER_SCALE);
                for(int j  =0 ; j< finalNfp.size() ; j ++){
                    double area = Math.abs(finalNfp.get(j).area());
                    if( finalNfp.get(j).size() < 3 || area < 0.1 * Config.CLIIPER_SCALE * Config.CLIIPER_SCALE){
                        finalNfp.remove(j);
                        j--;
                    }
                }

                if( finalNfp == null || finalNfp.size() == 0 ){
                    continue;
                }

                List<NestPath> f = new ArrayList<NestPath>();
                for(int j = 0 ; j<finalNfp.size() ; j ++){
                    f.add( toNestCoordinates(finalNfp.get(j)));
                }

                List<NestPath> finalNfpf = f;
                double minarea = Double.MIN_VALUE;
                double minX = Double.MAX_VALUE;
                NestPath nf = null;
                double area = Double.MIN_VALUE;
                Vector shifvector = null;
                for(int j = 0 ; j < finalNfpf.size() ; j ++){
                    nf = finalNfpf.get(j);
                    if(Math.abs( GeometryUtil.polygonArea(nf)) < 2 ){
                        continue;
                    }
                    for(int k = 0 ; k< nf.size() ; k ++){
                        NestPath allpoints = new NestPath();
                        for(int m = 0; m <placed.size();m++){
                            for(int n = 0 ; n < placed.get(m).size();n++){
                                allpoints.add(new Segment(  placed.get(m).get(n).x + placements.get(m).x  ,
                                                            placed.get(m).get(n).y +placements.get(m).y));
                            }
                        }
                        shifvector = new Vector(
                                nf.get(k).x - path.get(0).x,
                                nf.get(k).y - path.get(0).y,
                                path.getId(),
                                path.getRotation(),
                                combinedNfp
                        );
                        for(int m = 0 ;m < path.size();m++){
                            allpoints.add( new Segment(path.get(m).x + shifvector.x , path.get(m).y + shifvector.y));
                        }
                        Bound rectBounds = GeometryUtil.getPolygonBounds(allpoints);

                        area = rectBounds.getWidth() * 2 + rectBounds.getHeight();
                        if(minarea == Double.MIN_VALUE
                                || area < minarea
                                || ( GeometryUtil.almostEqual(minarea,area)
                                && (minX == Double.MIN_VALUE || shifvector.x <minX))){
                            minarea =area;
                            minwidth = rectBounds.getWidth();
                            position = shifvector;
                            minX = shifvector.x;
                        }
                    }
                }
                if(position!=null){

                    placed.add(path);
                    placements.add(position);
                }
            }
            if(minwidth != Double.MAX_VALUE){
                fitness += minwidth/binarea;
            }



            for(int i = 0 ; i< placed.size() ; i++){
                int index = paths.indexOf(placed.get(i));
                if( index >=  0 ){
                    paths.remove(index);
                }
            }

            if(placements != null && placements.size() >0 ){
                allplacements.add(placements);
            }
            else{
                break; // something went wrong
            }

        }
        // there were paths that couldn't be placed
        fitness += 2* paths.size();
        return new Result(allplacements , fitness , paths , binarea);
    }


    /**
     *  坐标转换，与clipper库交互必须坐标转换
     * @param polygon
     * @return
     */
    public static Path scaleUp2ClipperCoordinates(NestPath polygon){
        Path p = new Path();
        for(Segment s : polygon.getSegments()){
            ClipperCoor cc = CommonUtil.toClipperCoor(s.x , s.y);
            p.add(new Point.LongPoint(cc.getX() , cc.getY()));
        }
        return p;
    }

    public static NestPath toNestCoordinates(Path polygon){
        NestPath clone = new NestPath();
        for(int i = 0 ; i< polygon.size() ; i ++){
            Segment s = new Segment((double)polygon.get(i).getX()/Config.CLIIPER_SCALE , (double)polygon.get(i).getY()/Config.CLIIPER_SCALE);
            clone.add(s);
        }
        return clone ;
    }

}
