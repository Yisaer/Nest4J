package com.qunhe.util.nest.algorithm;

import com.qunhe.util.nest.data.NestPath;
import com.qunhe.util.nest.data.Segment;
import com.qunhe.util.nest.util.CommonUtil;
import com.qunhe.util.nest.util.Config;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GeneticAlgorithmTest {

    @Test
    public void ListTest() throws Exception{
        List<Segment> s1 = new ArrayList<Segment>();
        s1.add(new Segment(1,2));
        List<Segment> s2 = new ArrayList<Segment>();
        s2.add(s1.get(0));
        s1.get(0).setX(5);
        System.out.println(s1.get(0).getX());
        System.out.println(s2.get(0).getX());

    }

    @Test
    public void swapTest() throws  Exception{
        List<Segment> s1 = new ArrayList<Segment>();
        s1.add(new Segment(1,2));
        s1.add(new Segment(3,4));
        Collections.swap(s1 , 0,1);
        for(Segment s : s1 ){
            System.out.println(s.getX()+" , "+ s.getY());
        }
    }

    @Test
    public void sortTest() throws Exception{
        Individual i1 = new Individual();
        i1.fitness = 2;
        Individual i2 = new Individual();
        i2.fitness = 1;
        List<Individual> individuals = new ArrayList<Individual>();
        individuals.add(i1);individuals.add(i2);
        Collections.sort(individuals);
        System.out.println(individuals.get(0).fitness);
    }

    @Test
    public void indexOfTest() throws Exception{
        Segment s1 = new Segment(1,2);
        Segment s2 = new Segment(3,4);
        List<Segment> segments = new ArrayList<Segment>();
        segments.add(s1);segments.add(s2);
        List<Segment> segments1 = new ArrayList<Segment>();
        for(Segment s : segments ){
            segments1.add(new Segment(s));
        }
        System.out.println(segments1.indexOf(s1));
    }

    @Test
    public void GATest() throws Exception{
        Config  config = new Config();
        Segment s1 = new Segment(0,0);
        Segment s2 = new Segment(0,200);
        Segment s3 = new Segment(200,200);
        Segment s4 = new Segment(200,0);
        NestPath bin = new NestPath(config);
        bin.setRotation(0);
        bin.add(s1);bin.add(s2);bin.add(s3);bin.add(s4);
        Segment t1 = new Segment(0,300);
        Segment t2 = new Segment(0,400);
        Segment t3 = new Segment(100,400);
        Segment t4 = new Segment(100,300);
        NestPath poly1 = new NestPath(config);
        poly1.setRotation(4);
        poly1.add(t1);poly1.add(t2);poly1.add(t3);poly1.add(t4);
        Segment q1 = new Segment(400,0);
        Segment q2 = new Segment(420,0);
        Segment q3 = new Segment(420,50);
        Segment q4 = new Segment(400,50);
        NestPath poly2 = new NestPath(config);
        poly2.setRotation(2);
        poly2.add(q1);poly2.add(q2);poly2.add(q3);poly2.add(q4);

        Segment i1 = new Segment(20,320);
        Segment i2 = new Segment(20,370);
        Segment i3 = new Segment(70,370);
        Segment i4 = new Segment(70,320);
        NestPath in = new NestPath(config);
        in.add(i1); in.add(i2); in.add(i3); in.add(i4);


        List<NestPath> parts = new ArrayList<NestPath>();
        parts.add(in);parts.add(poly2); parts.add(poly1);

        List<NestPath> tree = CommonUtil.BuildTree(parts,config.CURVE_TOLERANCE);
        CommonUtil.offsetTree(tree , 0.5 * config.SPACING);

        List<NestPath> adam = new ArrayList<NestPath>();
        for(NestPath np : tree ){
            NestPath clone = new NestPath(np);
            adam.add(clone);
        }

        Collections.sort(adam);
        GeneticAlgorithm GA = new GeneticAlgorithm(adam,bin , config);


        Individual individual = null;
        for(int i =0 ;i< GA.config.POPULATION_SIZE;i++){
            if( GA.population.get(i).getFitness() < 0 ){
                individual = GA.population.get(i);
                break;
            }
        }
        if( individual == null ){
            GA.generation();
            individual = GA.population.get(1);
        }



//        System.out.println(GA.binBounds);
//        System.out.println("size = " +GA.population.size());
//        for(int i = 0; i<GA.population.size() ;i ++){
//            System.out.println("=============================================");
//            System.out.println(GA.population.get(i));
//        }

    }


}
