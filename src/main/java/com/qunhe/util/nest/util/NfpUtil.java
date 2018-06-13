package com.qunhe.util.nest.util;

import com.qunhe.util.nest.data.*;
import java.util.List;

/**
 * @author yisa
 */
public class NfpUtil {

    /**
     * 获取一对多边形，并生成nfp
     * @param pair
     * @param config
     * @return
     */
    public static ParallelData nfpGenerator(NfpPair pair , Config config){
        boolean searchEdges = config.isCONCAVE();
        boolean useHoles = config.isUSE_HOLE();

        NestPath A = GeometryUtil.rotatePolygon2Polygon(pair.getA() , pair.getKey().getArotation());
        NestPath B = GeometryUtil.rotatePolygon2Polygon(pair.getB() , pair.getKey().getBrotation());

        List<NestPath> nfp ;
        if(pair.getKey().isInside()){
            if(GeometryUtil.isRectangle(A,0.001)){
                nfp = GeometryUtil.noFitPolygonRectangle(A,B);
                if(nfp == null ){

                }
            }
            else{
                nfp = GeometryUtil.noFitPolygon(A,B,true,searchEdges);
            }
            if(nfp != null && nfp.size() > 0){
                for(int i = 0 ; i<nfp.size() ; i ++){
                    if(GeometryUtil.polygonArea(nfp.get(i)) > 0 ){
                        nfp.get(i).reverse();
                    }
                }
            }
            else{
                //Warning on null inner NFP
            }
        }
        else{
            int count = 0 ;
            if(searchEdges){

                // NFP Generator TODO  double scale contorl
                nfp = GeometryUtil.noFitPolygon(A,B,false ,searchEdges);
                if(nfp  == null ){

                }
            }
            else{

                nfp = GeometryUtil.minkowskiDifference(A,B);
            }
            // sanity check
            if( nfp == null || nfp.size() == 0 ){

                return null;
            }
            for(int i = 0; i<nfp.size() ; i++){
                if(!searchEdges || i == 0){
                    if(Math.abs(GeometryUtil.polygonArea(nfp.get(i))) < Math.abs(GeometryUtil.polygonArea(A))){
                        nfp.remove(i);

                        return null;
                    }
                }
            }
            if(nfp.size() == 0 ){

                return null;
            }

            for(int i=0; i<nfp.size(); i++){
                if(GeometryUtil.polygonArea(nfp.get(i)) > 0){
                    nfp.get(i).reverse();
                }

                if(i > 0){
                    if(GeometryUtil.pointInPolygon(nfp.get(i).get(0), nfp.get(0))){
                        if(GeometryUtil.polygonArea(nfp.get(i)) < 0){
                            nfp.get(i).reverse();
                        }
                    }
                }
            }

            if(useHoles && A.getChildren().size() >0 ){
                Bound Bbounds = GeometryUtil.getPolygonBounds(B);
                for(int i = 0 ; i <A.getChildren().size() ; i++){
                    Bound Abounds = GeometryUtil.getPolygonBounds(A.getChildren().get(i));

                    if(Abounds.width > Bbounds.width && Abounds.height > Bbounds.height){

                        List<NestPath> cnfp = GeometryUtil.noFitPolygon(A.getChildren().get(i),B,true,searchEdges);
                        // ensure all interior NFPs have the same winding direction

                        if(cnfp != null && cnfp.size() > 0){

                            for(int j=0; j<cnfp.size(); j++){
                                if(GeometryUtil.polygonArea(cnfp.get(j)) < 0){
                                    cnfp.get(j).reverse();
                                }
                                nfp.add(cnfp.get(j));
                            }
                        }

                    }
                }
            }
        }
        if(nfp ==null ){

        }
        return new ParallelData(pair.getKey() , nfp);
    }
}
