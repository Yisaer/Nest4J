package com.qunhe.util.nest.data;

import java.util.List;

/**
 * @author yisa
 */
public class Result {
    public List<List<Vector>> placements;
    public double fitness ;
    public List<NestPath> paths;
    public double area;

    public Result(List<List<Vector>> placements, double fitness, List<NestPath> paths, double area) {
        this.placements = placements;
        this.fitness = fitness;
        this.paths = paths;
        this.area = area;
    }

    public Result() {
    }
}
