package com.qunhe.util.nest.util;

import com.qunhe.util.nest.Nest;
import com.qunhe.util.nest.data.Bound;
import com.qunhe.util.nest.data.NestPath;
import com.qunhe.util.nest.data.Result;
import com.qunhe.util.nest.data.Segment;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class GeometryUtilTest {
    @Test
    public void pointInPolygon() throws Exception {
        NestPath nestPath = new NestPath();
        nestPath.add(new Segment(0,0));
        nestPath.add(new Segment(10,10));
        nestPath.add(new Segment(20, 0 ));

        boolean result = GeometryUtil.pointInPolygon(new Segment(100,5) , nestPath);
        if(result == true ){
            System.out.println("SUCCESS");
        }
        else{
            System.out.println(result);
        }
    }


    @Test
    public void polygonAreaTest() throws Exception{
        NestPath nestPath = new NestPath();
        nestPath.add(new Segment(0,0));
        nestPath.add(new Segment(10,10));
        nestPath.add(new Segment(20, 0 ));
        double area = GeometryUtil.polygonArea(nestPath);
        System.out.println("area = " + area);
    }


    @Test
    public void onSegmentTest() throws Exception{
            Segment a = new Segment(1,2);
            Segment b = new Segment(4,5);
            Segment p = new Segment(3,4);
            boolean re1 = GeometryUtil.onSegment(a,b,p);
            System.out.println(re1);
            Segment p2 = new Segment(6,7);
            boolean re2 = GeometryUtil.onSegment(a,b,p2);
            System.out.println(re2);
    }

    @Test
    public void getPolygonBoundsTest() throws Exception{
            NestPath nestPath = new NestPath();
            nestPath.add(new Segment(0,0));
            nestPath.add(new Segment(20,0));
            nestPath.add(new Segment(10,10));
            Bound bound = GeometryUtil.getPolygonBounds(nestPath);
            System.out.println("x = "+ bound.xmin +" , y = "+bound.ymin+" , width = "+ bound.width+" , height = "+bound.height);
    }

    @Test
    public void rotatePolygon2PolygonTest() throws Exception{
        NestPath nestPath = new NestPath();
        nestPath.add(new Segment(0,0));
        nestPath.add(new Segment(20,0));
        nestPath.add(new Segment(10,10));
        NestPath rota = GeometryUtil.rotatePolygon2Polygon(nestPath, 90);
        System.out.println(rota.toString());
    }

    @Test
    public void rotatePolygonTest() throws Exception{
        NestPath nestPath = new NestPath();
        nestPath.add(new Segment(0,0));
        nestPath.add(new Segment(20,0));
        nestPath.add(new Segment(10,10));
        Bound bound = GeometryUtil.rotatePolygon(nestPath,90);
        System.out.println(bound.toString());
    }


    @Test
    public void isRectangleTest() throws Exception{
        NestPath nestPath = new NestPath();
        nestPath.add(new Segment(0,0));
        nestPath.add(new Segment(20,0));
        nestPath.add(new Segment(10,10));
        boolean is1 = GeometryUtil.isRectangle(nestPath,0.001);
        System.out.println(is1);

        NestPath xie = new NestPath();
        xie.add(new Segment(0,1));
        xie.add(new Segment(4,5));
        xie.add(new Segment(5,4));
        xie.add(1,0);
        boolean is2 = GeometryUtil.isRectangle(xie, 0.001);
        System.out.println(is2);
    }

    @Test
    public void rotateTest() throws Exception{
        NestPath r1 = new NestPath();
        r1.add(300,0);
        r1.add(300,100);
        r1.add(600,100);
        r1.add(600,0);
        NestPath c1 = GeometryUtil.rotatePolygon2Polygon(r1,225);
        Bound d1 = GeometryUtil.getPolygonBounds(c1);
        Bound rd1 = GeometryUtil.rotatePolygon(r1,225);
        System.out.println(d1);
        System.out.println(rd1);
    }


    @Test
    public void nofitPoluygonTest() throws Exception{
        NestPath r1 = new NestPath();
        r1.add(603.2070398 , 596.164);
        r1.add(605,600);
        r1.add(605,700);
        r1.add( 603.836 ,703.2070398 );
        r1.add(600,705);
        r1.add(300,705);
        r1.add(296.7929602 , 703.836);
        r1.add(295 , 700);
        r1.add(295 ,600);
        r1.add(296.164, 596.7929602);
        r1.add(300,595);
        r1.add(600,595);

        NestPath t1 = new NestPath();
        t1.add( 1003.2070398 , -3.836);
        t1.add(1005,0);
        t1.add(1005,100);
        t1.add( 1003.836 , 103.2070398);
        t1.add(1000, 105);
        t1.add(900,105);
        t1.add(896.7929602 , 103.836);
        t1.add(895,100);
        t1.add(895, 0 );
        t1.add(896.164 , -3.2070398);
        t1.add(900 , -5);
        t1.add(1000,-5);

        GeometryUtil.noFitPolygon(r1,t1,false  ,true);

    }

    @Test
    public void nofitInTest(){
        NestPath little = new NestPath();
        little.add(900,0);
        little.add(870,20);
        little.add(930,20);

        NestPath inner = new NestPath();
        inner.add(650,50);
        inner.add(650,150);
        inner.add(750,150);
        inner.add(750,50);

        NestPath A = CommonUtil.polygonOffset(inner,- 0.5*10).get(0);

        NestPath B = CommonUtil.polygonOffset(little,0.5*10 ).get(0);

        if(GeometryUtil.polygonArea(A) > 0 ){
            A.reverse();
        }
        if(GeometryUtil.polygonArea(B) > 0 ){
            B.reverse();
        }
        List<NestPath> cfp = GeometryUtil.noFitPolygon(A,B,true,false);
        System.out.println();
    }


    @Test
    public void TestAlmost(){
        double x = -296.794;
        double y = 491.163;
        System.out.println(GeometryUtil.almostEqual(603.207 , 900+x) +" , "+GeometryUtil.almostEqual(596.164,105+y));
    }

    @Test
    public void intersectTest(){
        NestPath r1 = new NestPath();
        r1.add(603.2070398 , 596.164);
        r1.add(605,600);
        r1.add(605,700);
        r1.add( 603.836 ,703.2070398 );
        r1.add(600,705);
        r1.add(300,705);
        r1.add(296.7929602 , 703.836);
        r1.add(295 , 700);
        r1.add(295 ,600);
        r1.add(296.164, 596.7929602);
        r1.add(300,595);
        r1.add(600,595);

        NestPath t1 = new NestPath();
        t1.add( 1003.2070398 , -3.836);
        t1.add(1005,0);
        t1.add(1005,100);
        t1.add( 1003.836 , 103.2070398);
        t1.add(1000, 105);
        t1.add(900,105);
        t1.add(896.7929602 , 103.836);
        t1.add(895,100);
        t1.add(895, 0 );
        t1.add(896.164 , -3.2070398);
        t1.add(900 , -5);
        t1.add(1000,-5);

        NestPath r2 = new NestPath();
        r1.add(300,600);
        r1.add(300,700);
        r1.add(600,700);
        r1.add(600,600);
        r1.bid = 4;
        r1.setRotation(8);


        NestPath t2 = new NestPath();
        t1.add(900,0);
        t1.add(900,100);
        t1.add(1000,100);
        t1.add(1000,0);

        System.out.println(GeometryUtil.intersect(r2,t2));
    }

    @Test
    public void LoopTest(){

        NestPath r1 = new NestPath();
        r1.add(-1000,5);
        r1.add(-1002.688,4.216);
        r1.add(-1004,2.11);
        r1.add( -1005.0,0.0);
        r1.add(-1005.0 , -99.999);
        r1.add(-1004.216,-102.687);
        r1.add(-1002.11 , -104.532);
        r1.add(-1000.0 , -104.999);
        r1.add(-900.0 , 104.999);
        r1.add(-897.312 , -104.215);
        r1.add(-895.467 , -102.109);
        r1.add(-895.0 , -99.999);
        r1.add(-895.0 , 0.0);
        r1.add(-895.784 , 2.688);
        r1.add(-894.89 , 4.533);
        r1.add(-900.0 , 5.0);


    }


    @Test
    public void polygonSlideDistanceTest(){
        NestPath data1 = new NestPath();
        data1.add(0,500);
        data1.add(0,650);
        data1.add(100,650);
        data1.add(100,500);
        data1.setRotation(0);
        data1.bid = 0 ;
        NestPath data2 = new NestPath();
        data2.add(360,0);
        data2.add(450,100);
        data2.add(560,0);
        data2.setRotation(0);
        data2.bid=1;


        data1 = CommonUtil.polygonOffset(data1,5).get(0);
        data2 = CommonUtil.polygonOffset(data2, 5).get(0);
        if(GeometryUtil.polygonArea(data1) > 0 ){
            data1.reverse();
        }
        if(GeometryUtil.polygonArea(data2) > 0 ){
            data2.reverse();
        }
        data1 = GeometryUtil.rotatePolygon2Polygon(data1 , 180);

        data2 = GeometryUtil.rotatePolygon2Polygon(data2, 270);

//        Segment vector  = new Segment( 1.7929999999999997  , 3.8360000000000127);
//        data1.offsetX  =  -9.998999999999999; data1.offsetY = 290;
        data2.offsetX = 0; data2.offsetY = 0;

        //x = 1.935 y = 3.9499999999999886
        Segment vector = new Segment(1.935,3.9499999999999886);
        data1.offsetX = -9.998999999999999 ; data1.offsetY = 290;
        GeometryUtil.polygonSlideDistance(data2 ,data1 , vector ,true);

    }


    @Test
    public void LineIntersect(){
        //                b1 = x = 677.207, y = 146.844 b2  = x = 675.492, y = 143.894 a1 = x = 745.0, y = 145.0 a2 x = 655.0, y = 145.0
        Segment b1 = new Segment(677.207 , 146.844);
        Segment b2 = new Segment(675.492 , 143.894);
        Segment a1 = new Segment(745.0,145.0);
        Segment a2 = new Segment(655.0,145.0);

        Segment p = GeometryUtil.lineIntersect(b1, b2, a1, a2 ,null);
        System.out.println(p);
    }


    @Test
    public void linearizeTest(){
//        <path d="M80 80
//        A 45 45, 0, 0, 0, 125 125
//        L 125 80 Z" fill="green"/>
        NestPath nestPath = GeometryUtil.linearize(new Segment(80,80) , new Segment(125,125) , 45,45 , 0,0,0,2);
        System.out.println(nestPath);
    }

}
