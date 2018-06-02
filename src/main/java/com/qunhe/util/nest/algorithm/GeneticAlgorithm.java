package com.qunhe.util.nest.algorithm;

import com.qunhe.util.nest.data.Bound;
import com.qunhe.util.nest.data.NestPath;
import com.qunhe.util.nest.util.Config;
import com.qunhe.util.nest.util.GeometryUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * @author yisa
 */
public class GeneticAlgorithm {

    public List<NestPath> adam;
    public NestPath bin;
    public Bound binBounds;
    public List<Integer> angles ;
    public List<Individual> population;
    public Config config;

    public GeneticAlgorithm(List<NestPath> adam, NestPath bin, Config config) {
        this.adam = adam;
        this.bin = bin;
        this.config = config;
        this.binBounds = GeometryUtil.getPolygonBounds(bin);
        population = new ArrayList<Individual>();
        init();
    }



    public void generation(){
        List<Individual> newpopulation = new ArrayList<Individual>();
        Collections.sort(population);

        newpopulation.add(population.get(0));
        while(newpopulation.size() < config.POPULATION_SIZE){
            Individual male = randomWeightedIndividual(null);
            Individual female = randomWeightedIndividual(male);
            List<Individual> children = mate(male,female);
            newpopulation.add(mutate(children.get(0)));
            if(newpopulation.size() < population.size() ){
                newpopulation.add(mutate(children.get(1)));
            }
        }
        population = newpopulation;
    }

    public List<Individual> mate(Individual male , Individual female){
        List<Individual> children = new ArrayList<Individual>();

        long cutpoint = Math.round(Math.min(Math.max(Math.random(), 0.1), 0.9)*(male.placement.size()-1));

        List<NestPath> gene1 = new ArrayList<NestPath>();
        List<Integer> rot1 = new ArrayList<Integer>();
        List<NestPath> gene2 = new ArrayList<NestPath>();
        List<Integer> rot2 = new ArrayList<Integer>();

        for(int i = 0; i <cutpoint;i ++){
            gene1.add(new NestPath(male.placement.get(i)));
            rot1.add(male.getRotation().get(i));
            gene2.add(new NestPath(female.placement.get(i)));
            rot2.add(female.getRotation().get(i));
        }

        for(int i = 0 ; i<female.placement.size() ;i ++){
            if(!contains(gene1,female.placement.get(i).getId())){
                gene1.add(female.placement.get(i));
                rot1.add(female.rotation.get(i));
            }
        }

        for(int  i= 0 ; i<male.placement.size() ; i ++){
            if(! contains(gene2 , male.placement.get(i).getId())){
                gene2.add(male.placement.get(i));
                rot2.add(male.rotation.get(i));
            }
        }
        Individual individual1 = new Individual(gene1,rot1);
        Individual individual2 = new Individual(gene2,rot2);

        checkAndUpdate(individual1);checkAndUpdate(individual2);


        children.add(individual1); children.add(individual2);
        return children;
    }


    private boolean contains(List<NestPath> gene , int id ){
        for(int i = 0 ; i<gene.size() ; i ++){
            if(gene.get(i).getId() == id ){
                return true;
            }
        }
        return false;
    }

    private Individual randomWeightedIndividual(Individual exclude){
        List<Individual> pop = new ArrayList<Individual>();
        for(int i = 0 ; i < population.size(); i ++){
            Individual individual = population.get(i);
            Individual clone = new Individual(individual);
            pop.add(clone);
        }
        if(exclude!= null){
            int index = pop.indexOf(exclude);
            if(index >= 0){
                pop.remove(index);
            }
        }
        double rand = Math.random();
        double lower = 0;
        double weight = 1/pop.size();
        double upper = weight;

        for(int i =0 ; i <pop.size();i ++){
            if(rand > lower && rand < upper ){
                return pop.get(i);
            }
            lower = upper;
            upper += 2 * weight * ( (pop.size() - i ) / pop.size());
        }
        return pop.get(0);
    }

    private void init(){
        angles = new ArrayList<Integer>();
        for(int i = 0 ; i< adam.size(); i ++){
            int angle = randomAngle(adam.get(i));
            angles.add(angle);
        }
        population.add(new Individual(adam , angles));
        while(population.size() < config.POPULATION_SIZE ){
            Individual mutant = mutate(population.get(0));
            population.add(mutant);
        }
    }

    private Individual mutate(Individual individual ){

        Individual clone = new Individual(individual);
        for(int i = 0 ; i< clone.placement.size() ; i ++){
            double random = Math.random();
            if( random < 0.01 * config.MUTATION_RATE ){
                int j = i +1;
                if( j < clone.placement.size() ){
                    Collections.swap(clone.getPlacement() , i , j );
                }
            }
            random = Math.random();
            if(random < 0.01 * config.MUTATION_RATE ){
                clone.getRotation().set( i , randomAngle(clone.placement.get(i)));
            }
        }
        checkAndUpdate(clone);
        return clone;
    }

    /**
     * 为一个polygon 返回一个角度
     * @param part
     * @return
     */
    private  int randomAngle(NestPath part){
        List<Integer> angleList = new ArrayList<Integer>();
        int rotate = Math.max(1,part.getRotation());
        if(rotate == 0 ){
            angleList.add(0);
        }
        else{
            for(int i = 0 ; i< rotate; i ++){
                angleList.add((360/rotate) * i );
            }
        }
        Collections.shuffle(angleList);
        for(int i = 0 ; i <angleList.size() ; i ++){
            Bound rotatedPart = GeometryUtil.rotatePolygon(part , angleList.get(i));
            if(rotatedPart.getWidth() < binBounds.getWidth() && rotatedPart.getHeight() < binBounds.getHeight() ){
                return angleList.get(i);
            }
        }
        /**
         * 没有找到合法的角度
         */
        return -1;
    }

    public List<NestPath> getAdam() {
        return adam;
    }

    public void setAdam(List<NestPath> adam) {
        this.adam = adam;
    }

    public NestPath getBin() {
        return bin;
    }

    public void setBin(NestPath bin) {
        this.bin = bin;
    }

    public void checkAndUpdate(Individual individual){
        for(int i = 0; i <individual.placement.size();i++){
            int angle = individual.getRotation().get(i);
            NestPath nestPath = individual.getPlacement().get(i);
            Bound rotateBound = GeometryUtil.rotatePolygon(nestPath,angle);
            if(rotateBound.width < binBounds.width && rotateBound.height < binBounds.height){
                continue;
            }
            else{
                int safeAngle = randomAngle(nestPath);
                individual.getRotation().set(i , safeAngle);
            }
        }
    }
}
