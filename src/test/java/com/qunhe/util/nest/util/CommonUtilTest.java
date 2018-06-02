package com.qunhe.util.nest.util;

import com.qunhe.util.nest.data.NestPath;
import com.qunhe.util.nest.data.Segment;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class CommonUtilTest {
    @Test
    public void toTreeTest() throws Exception{

        NestPath nestPath = new NestPath();
        nestPath.add(new Segment(-5,-5));
        nestPath.add(new Segment(-5,15));
        nestPath.add(new Segment(20, 15));
        nestPath.add(new Segment(20,-5));

        NestPath nestPath1 = new NestPath();
        nestPath1.add(new Segment(0,0));
        nestPath1.add(new Segment(0,5));
        nestPath1.add(new Segment(5, 0));


        NestPath nestPath2 = new NestPath();
        nestPath2.add(new Segment(10,0));
        nestPath2.add(new Segment(10,5));
        nestPath2.add(new Segment(15, 0));

        List<NestPath> list = new ArrayList<NestPath>();
        list.add(nestPath);list.add(nestPath1);list.add(nestPath2);
        int id = CommonUtil.toTree(list,0);
        System.out.println("id = "+id);
        nestPath.toString();
    }

    @Test
    public void offsetTreeTest() throws Exception{
        NestPath nestPath = new NestPath();
        nestPath.add(new Segment(50,50));
        nestPath.add(new Segment(100,50));
        nestPath.add(new Segment(100,100));
        nestPath.add(new Segment(50,100));
        List<NestPath> nestPaths = new ArrayList<NestPath>();
        nestPaths.add(nestPath);
        CommonUtil.offsetTree(nestPaths,0.5 * nestPath.config.SPACING);
        nestPaths.get(0).toString();
    }

    @Test
    public void polygonOffsetTest() throws Exception{
        Config config = new Config();
        Segment i1 = new Segment(20,320);
        Segment i2 = new Segment(20,370);
        Segment i3 = new Segment(70,370);
        Segment i4 = new Segment(70,320);
        NestPath in = new NestPath(config);
        in.add(i1);in.add(i2);in.add(i3);in.add(i4);

        List<NestPath> result = CommonUtil.polygonOffset(in,0.5*config.SPACING);
        for(Segment s :result.get(0).getSegments()){
            System.out.println(s);
        }
    }

    @Test
    public void toTreeTest2() throws Exception{

        Segment s1 = new Segment(0,0);
        Segment s2 = new Segment(0,200);
        Segment s3 = new Segment(200,200);
        Segment s4 = new Segment(200,0);
        NestPath out = new NestPath();
        out.add(s1);out.add(s2);out.add(s3);out.add(s4);

        Segment t1 = new Segment(50,50);
        Segment t2 = new Segment(50,100);
        Segment t3 = new Segment(100,100);
        Segment t4 = new Segment(100,50);
        NestPath in = new NestPath();
        in.add(t1);in.add(t2);in.add(t3);in.add(t4);

        List<NestPath> list = new ArrayList<NestPath>();
        list.add(out);
        list.add(in);
        int id = CommonUtil.toTree(list,0);
        System.out.println(list.get(0));
    }


    @Test
    public void FloatTest(){
        Float f1 = 14f;
        Float f2 = 12f;
        System.out.println(Float.compare(f1,f2));
    }

}
