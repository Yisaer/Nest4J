package com.qunhe.util.nest.algorithm;


import com.qunhe.util.nest.data.NestPath;
import com.qunhe.util.nest.data.Segment;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;


public class IndividualTest {

    @Test
    public void equalsTest() throws Exception {
        Individual individual = new Individual();

        NestPath nestPath = new NestPath();
        nestPath.add(new Segment(0,0));
        nestPath.add(new Segment(10,10));
        nestPath.add(new Segment(20, 0 ));

        NestPath nestPath1 = new NestPath();
        nestPath1.add(new Segment(5,5));
        nestPath1.add(new Segment(15,15));
        nestPath1.add(new Segment(25, 5 ));


        NestPath nestPath2 = new NestPath();
        nestPath2.add(new Segment(4,4));
        nestPath2.add(new Segment(14,14));
        nestPath2.add(new Segment(24, 4));

        individual.getPlacement().add(nestPath);
        individual.getPlacement().add(nestPath1);
        individual.getPlacement().add(nestPath2);

        individual.getRotation().add(10);
        individual.getRotation().add(20);
        individual.getRotation().add(30);
        Individual clone = new Individual(individual);
        System.out.println(clone.equals(individual)+","+individual.equals(clone));

    }

    @Test
    public void IndividualCloneTest() throws Exception{
        Individual individual = new Individual();

        NestPath nestPath = new NestPath();
        nestPath.add(new Segment(0,0));
        nestPath.add(new Segment(10,10));
        nestPath.add(new Segment(20, 0 ));

        NestPath nestPath1 = new NestPath();
        nestPath1.add(new Segment(5,5));
        nestPath1.add(new Segment(15,15));
        nestPath1.add(new Segment(25, 5 ));


        NestPath nestPath2 = new NestPath();
        nestPath2.add(new Segment(4,4));
        nestPath2.add(new Segment(14,14));
        nestPath2.add(new Segment(24, 4));

        individual.getPlacement().add(nestPath);
        individual.getPlacement().add(nestPath1);
        individual.getPlacement().add(nestPath2);

        individual.getRotation().add(10);
        individual.getRotation().add(20);
        individual.getRotation().add(30);

        System.out.println(individual.toString());

        Individual clone = new Individual(individual);
        System.out.println("==================================================\n" +clone.toString());

    }

    @Test
    public void indexOfTest() throws Exception{
        Individual individual = new Individual();
        NestPath nestPath = new NestPath();
        nestPath.add(new Segment(0,0));
        nestPath.add(new Segment(10,10));
        nestPath.add(new Segment(20, 0 ));
        individual.getPlacement().add(nestPath);
        individual.getRotation().add(10);

        Individual individual2 = new Individual();
        NestPath nestPath2 = new NestPath();
        nestPath2.add(new Segment(20,20));
        nestPath2.add(new Segment(30,30));
        nestPath2.add(new Segment(40, 30 ));
        individual2.getPlacement().add(nestPath2);
        individual2.getRotation().add(20);

        List<Individual> individuals = new ArrayList<Individual>();
        individuals.add(individual);individuals.add(individual2);

        List<Individual> individualList = new ArrayList<Individual>();
        for(Individual i :individuals) {
            Individual clone = new Individual(i);
            individualList.add(clone);
        }
        int index = individualList.indexOf(individual2);
        System.out.println(index);

    }

    @Test
    public void removeTest() throws Exception{
        Individual individual = new Individual();
        NestPath nestPath = new NestPath();
        nestPath.add(new Segment(0,0));
        nestPath.add(new Segment(10,10));
        nestPath.add(new Segment(20, 0 ));
        individual.getPlacement().add(nestPath);
        individual.getRotation().add(10);

        Individual individual2 = new Individual();
        NestPath nestPath2 = new NestPath();
        nestPath2.add(new Segment(20,20));
        nestPath2.add(new Segment(30,30));
        nestPath2.add(new Segment(40, 30 ));
        individual2.getPlacement().add(nestPath2);
        individual2.getRotation().add(20);


        Individual individual3 = new Individual();
        NestPath nestPath3 = new NestPath();
        nestPath3.add(new Segment(50,50));
        nestPath3.add(new Segment(90,50));
        nestPath3.add(new Segment(100, 60 ));
        individual3.getPlacement().add(nestPath3);
        individual3.getRotation().add(30);

        List<Individual> individuals = new ArrayList<Individual>();
        individuals.add(individual);individuals.add(individual2);individuals.add(individual3);

        individuals.remove(1);
        for(int i=0 ;i<individuals.size();i++){
            System.out.println(individuals.get(i).toString());
        }
    }

}
