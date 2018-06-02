package com.qunhe.util.nest.util;

import com.qunhe.util.nest.Nest;
import com.qunhe.util.nest.data.NestPath;
import com.qunhe.util.nest.data.NfpKey;
import com.qunhe.util.nest.data.Result;
import com.qunhe.util.nest.data.Segment;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlacementworkerTest {


    @Test
    public void MapTest() throws Exception{
        Map<String, NfpKey> nfpCache = new HashMap<String, NfpKey>();
        String key = "123";
        if(nfpCache.containsKey(key)){
            System.out.println("Here");
        }
        else{
            System.out.println("Hello" );
        }
    }


    @Test
    public void placePaths() throws Exception {

        NestPath binPolygon = new NestPath();
        binPolygon.add( new Segment(290,290));
        binPolygon.add(new Segment( 0,290));
        binPolygon.add(new Segment( 0 , 0));
        binPolygon.add(new Segment( 290 , 0 ));

        List<NestPath> placelist = new ArrayList<NestPath>();
        NestPath nestPath1 = new NestPath();
        nestPath1.add(new Segment((double)273.2070398,(double)346.164));
        nestPath1.add(new Segment(275,350));
        nestPath1.add(new Segment(275,420));
        nestPath1.add(new Segment(273.836,423.2070));
        nestPath1.add(new Segment(270,425));
        nestPath1.add(new Segment(10,425));
        nestPath1.add(6.7929602 , 423.836);
        nestPath1.add(5,420);
        nestPath1.add(5, 350);
        nestPath1.add(6.164 ,346.7929602 );
        nestPath1.add(10,345);
        nestPath1.add(270,345);
        nestPath1.setSource(1);
        nestPath1.setId(1);
        nestPath1.setRotation(270);

        NestPath nestPath2 = new NestPath();
        nestPath2.add(503.2070398,6.164);
        nestPath2.add(504.9208818,  9.1140416);
        nestPath2.add(504.3707864 , 12.4282147);
        nestPath2.add( 454.3707864 , 102.4282147);
        nestPath2.add( 451.7957911 , 104.6663834);
        nestPath2.add( 448.3846755 , 104.7318841);
        nestPath2.add( 445.6292136 , 102.4282147);
        nestPath2.add( 395.629213 ,  12.4282147);
        nestPath2.add( 395.0892565 , 9.0594691);
        nestPath2.add(396.8357416 , 6.1286348);
        nestPath2.add(400,5);
        nestPath2.add(500,5);
        nestPath2.setSource(0);
        nestPath2.setId(0);
        nestPath2.setRotation(270);
        placelist.add(nestPath1);
        placelist.add(nestPath2);
        List<Integer> integers = new ArrayList<Integer>();
        integers.add(1);
        integers.add(0);
        List<Integer> rotations = new ArrayList<Integer>();
        rotations.add(270);rotations.add(270);
        Config config = new Config();
        Map<String, List<NestPath>> nfpCache = new HashMap<String, List<NestPath>>();
        Placementworker placementworker = new Placementworker(binPolygon,config,nfpCache);
        Result result = placementworker.placePaths(placelist);
        System.out.println("area = "+ result.area +" , fitness = "+result.fitness);

    }

}
