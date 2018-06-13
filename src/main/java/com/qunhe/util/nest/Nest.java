package com.qunhe.util.nest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.qunhe.util.nest.algorithm.GeneticAlgorithm;
import com.qunhe.util.nest.algorithm.Individual;
import com.qunhe.util.nest.data.*;
import com.qunhe.util.nest.data.Vector;
import com.qunhe.util.nest.util.*;

import java.util.*;


/**
 * @author yisa
 */
public class Nest {
    private  NestPath binPath;
    private  List<NestPath> parts;
    private Config config;
    int loopCount ;
    private GeneticAlgorithm GA = null ;
    private Map<String,List<NestPath>> nfpCache;
    private static Gson gson = new GsonBuilder().create();
    private int launchcount =0;

    /**
     *  创建一个新的Nest对象
     * @param binPath   底板多边形
     * @param parts     板件多边形列表
     * @param config    参数设置
     * @param count     迭代计算次数
     */
    public Nest(NestPath binPath, List<NestPath> parts, Config config, int count) {
        this.binPath = binPath;
        this.parts = parts;
        this.config = config;
        this.loopCount = count;
        nfpCache = new HashMap<String, List<NestPath>>();
    }

    /**
     *  开始进行Nest计算
     * @return
     */
    public  List<List<Placement>> startNest(){

        List<NestPath> tree = CommonUtil.BuildTree(parts , config.CURVE_TOLERANCE);

        CommonUtil.offsetTree(tree , 0.5 * config.SPACING);
        binPath.config = config;
        for(NestPath nestPath: parts){
            nestPath.config = config;
        }
        NestPath binPolygon = NestPath.cleanNestPath(binPath);
        Bound binBound = GeometryUtil.getPolygonBounds(binPolygon);
        if(config.SPACING > 0 ){
            List<NestPath> offsetBin = CommonUtil.polygonOffset(binPolygon , -0.5 * config.SPACING);
            if(offsetBin.size() == 1 ){
                binPolygon = offsetBin.get(0);
            }
        }
        binPolygon.setId(-1);

        List<Integer> integers = checkIfCanBePlaced(binPolygon,tree);
        List<NestPath> safeTree = new ArrayList<NestPath>();
        for(Integer i : integers){
            safeTree.add(tree.get(i));
        }
        tree = safeTree;

        double xbinmax = binPolygon.get(0).x;
        double xbinmin = binPolygon.get(0).x;
        double ybinmax = binPolygon.get(0).y;
        double ybinmin = binPolygon.get(0).y;

        for(int i = 1 ; i<binPolygon.size(); i ++){
            if(binPolygon.get(i).x > xbinmax ){
                xbinmax = binPolygon.get(i).x;
            }
            else if (binPolygon.get(i).x < xbinmin ){
                xbinmin = binPolygon.get(i) .x;
            }

            if(binPolygon.get(i).y > ybinmax ){
                ybinmax = binPolygon.get(i).y;
            }
            else if (binPolygon.get(i). y <ybinmin ){
                ybinmin = binPolygon.get(i).y;
            }
        }
        for(int i=0; i<binPolygon.size(); i++){
            binPolygon.get(i).x -= xbinmin;
            binPolygon.get(i).y -= ybinmin;
        }


        double binPolygonWidth = xbinmax - xbinmin;
        double binPolygonHeight = ybinmax - ybinmin;

        if(GeometryUtil.polygonArea(binPolygon) > 0 ){
            binPolygon.reverse();
        }
        /**
         * 确保为逆时针
         */
        for(int i = 0 ; i< tree.size(); i ++){
            Segment start = tree.get(i).get(0);
            Segment end = tree.get(i).get(tree.get(i).size()-1);
            if(start == end || GeometryUtil.almostEqual(start.x , end.x) && GeometryUtil.almostEqual(start.y , end.y)){
                tree.get(i).pop();
            }
            if(GeometryUtil.polygonArea(tree.get(i)) > 0 ){
                tree.get(i).reverse();
            }
        }

        launchcount = 0;
        Result best = null;
        for(int i = 0; i<loopCount;i++ ){

            Result result = launchWorkers(tree , binPolygon , config);

            if(i == 0 ){
                best = result;
            }
            else{
                if(best.fitness > result.fitness){
                    best = result;
                }
            }
        }
        double sumarea = 0;
        double totalarea = 0;
        for(int i = 0; i < best.placements.size();i++){
            totalarea += Math.abs(GeometryUtil.polygonArea(binPolygon));
            for(int j = 0 ; j< best.placements.get(i).size() ; j ++){
                sumarea += Math.abs(GeometryUtil.polygonArea(tree.get(best.placements.get(i).get(j).id)));
            }
        }
        double rate = (sumarea/totalarea)*100;
        List<List<Placement>> appliedPlacement = applyPlacement(best,tree);
        return appliedPlacement;
    }

    /**
     *  一次迭代计算
     * @param tree  底板
     * @param binPolygon    板件列表
     * @param config    设置
     * @return
     */
    public Result launchWorkers(List<NestPath> tree ,NestPath binPolygon ,Config config ){
        launchcount++;
        if(GA == null ){

            List<NestPath> adam = new ArrayList<NestPath>();
            for(NestPath nestPath : tree ){
                NestPath clone  = new NestPath(nestPath);
                adam.add(clone);
            }
            for(NestPath nestPath: adam){
                nestPath.area = GeometryUtil.polygonArea(nestPath);
            }
            Collections.sort(adam);
            GA = new GeneticAlgorithm(adam , binPolygon  , config);
        }

        Individual individual = null;
        for(int i = 0 ; i <GA.population.size(); i ++){
            if( GA.population.get(i).getFitness() <  0 ){
                individual = GA.population.get(i);
                break;
            }
        }
//        if(individual == null ){
//            GA.generation();
//            individual = GA.population.get(1);
//        }
        if(launchcount > 1 && individual == null ){
            GA.generation();
            individual = GA.population.get(1);
        }
        
        // 以上为GA

        List<NestPath> placelist = individual.getPlacement();
        List<Integer> rotations = individual.getRotation();

        List<Integer> ids = new ArrayList<Integer>();
        for(int i = 0 ; i < placelist.size(); i ++){
            ids.add(placelist.get(i).getId());
            placelist.get(i).setRotation(rotations.get(i));
        }
        List<NfpPair> nfpPairs = new ArrayList<NfpPair>();
        NfpKey key = null;
        /**
         * 如果在nfpCache里没找到nfpKey 则添加进nfpPairs
         */
        for(int i = 0 ; i< placelist.size();i++){
            NestPath part = placelist.get(i);
            key = new NfpKey(binPolygon .getId() , part.getId() , true , 0 , part.getRotation());
            nfpPairs.add(new NfpPair(binPolygon,part,key));
            for(int j = 0 ; j< i ; j ++){
                NestPath placed = placelist.get(j);
                NfpKey keyed = new NfpKey(placed.getId() , part.getId() , false , rotations.get(j), rotations.get(i));
                nfpPairs.add( new NfpPair(placed , part , keyed));
            }
        }


        /**
         * 第一次nfpCache为空 ，nfpCache存的是nfpKey所对应的两个polygon所形成的Nfp( List<NestPath> )
         */
        List<ParallelData> generatedNfp = new ArrayList<ParallelData>();
        for(NfpPair nfpPair :nfpPairs){
            ParallelData data = NfpUtil.nfpGenerator(nfpPair,config);
            generatedNfp.add(data);
        }
        for(int i = 0 ; i<generatedNfp.size() ; i++){
            ParallelData Nfp = generatedNfp.get(i);
            //TODO remove gson & generate a new key algorithm
            String tkey = gson.toJson(Nfp.getKey());
            nfpCache.put(tkey , Nfp.value);
        }


        Placementworker worker = new Placementworker(binPolygon,config,nfpCache);
        List<NestPath> placeListSlice = new ArrayList<NestPath>();



        for(int i = 0; i< placelist.size() ; i++){
            placeListSlice.add( new NestPath(placelist.get(i)));
        }
        List<List<NestPath>> data = new ArrayList<List<NestPath>>();
        data.add(placeListSlice);
        List<Result> placements = new ArrayList<Result>();
        for(int i = 0 ;i <data.size() ; i++){
            Result result = worker.placePaths(data.get(i));
            placements.add(result);
        }
        if(placements.size() == 0){
            return null;
        }
        individual.fitness = placements.get(0).fitness;
        Result bestResult = placements.get(0);
        for(int i = 1; i <placements.size() ; i++){
            if(placements.get(i).fitness < bestResult.fitness){
                bestResult = placements.get(i);
            }
        }
        return bestResult;
    }

    /**
     *  通过id与bid将translate和rotate绑定到对应板件上
     * @param best
     * @param tree
     * @return
     */
    public static List<List<Placement>> applyPlacement(Result best , List<NestPath> tree){
        List<List<Placement>> applyPlacement = new ArrayList<List<Placement>>();
        for(int i = 0; i<best.placements.size();i++){
            List<Placement> binTranslate = new ArrayList<Placement>();
            for(int j = 0 ; j <best.placements.get(i).size(); j ++){
                Vector v = best.placements.get(i).get(j);
                NestPath nestPath = tree.get(v.id);
                for(NestPath child : nestPath.getChildren()){
                    Placement chPlacement = new Placement(child.bid , new Segment(v.x,v.y) , v.rotation);
                    binTranslate.add(chPlacement);
                }
                Placement placement = new Placement(nestPath.bid , new Segment(v.x,v.y) , v.rotation);
                binTranslate.add(placement);
            }
            applyPlacement.add(binTranslate);
        }
        return applyPlacement;
    }


    /**
     * 在遗传算法中每次突变或者是交配产生出新的种群时，可能会出现板件与旋转角度不适配的结果，需要重新检查并适配。
     * @param binPolygon
     * @param tree
     * @return
     */
    private static List<Integer>  checkIfCanBePlaced(NestPath binPolygon , List<NestPath> tree ){
        List<Integer> CanBePlacdPolygonIndex = new ArrayList<Integer>();
        Bound binBound = GeometryUtil.getPolygonBounds(binPolygon);
        for(int i = 0; i <tree.size() ; i++ ){
            NestPath nestPath = tree.get(i);
            if(nestPath.getRotation() == 0 ){
                Bound bound = GeometryUtil.getPolygonBounds(nestPath);
                if(bound.width < binBound.width && bound.height < binBound.height ){
                    CanBePlacdPolygonIndex.add(i);
                    continue;
                }
            }
            else{
                for(int j = 0 ; j<nestPath.getRotation() ; j ++){
                    Bound rotatedBound = GeometryUtil.rotatePolygon(nestPath , (360/nestPath.getRotation()) * j );
                    if(rotatedBound.width < binBound.width && rotatedBound.height < binBound.height ){
                        CanBePlacdPolygonIndex.add(i);
                        break;
                    }
                }
            }
        }
        return CanBePlacdPolygonIndex;
    }



    public  void add(NestPath np ){
        parts.add(np);
    }

    public  NestPath getBinPath() {
        return binPath;
    }

    public  List<NestPath> getParts() {
        return parts;
    }

    public void setBinPath(NestPath binPath) {
        this.binPath = binPath;
    }

    public void setParts(List<NestPath> parts) {
        this.parts = parts;
    }

    public Config getConfig() {
        return config;
    }

    public void setConfig(Config config) {
        this.config = config;
    }

}
