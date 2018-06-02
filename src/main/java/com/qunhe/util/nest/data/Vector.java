package com.qunhe.util.nest.data;

import de.lighti.clipper.Paths;

import java.util.List;

/**
 * @author yisa
 */
public class Vector {
    public double x;
    public double y;
    public int id;
    public int rotation;
    public Paths nfp;

    public Vector(double x, double y, int id, int rotation) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.rotation = rotation;
        this.nfp = new Paths();
    }

    public Vector(double x, double y, int id, int rotation, Paths nfp) {
        this.x = x;
        this.y = y;
        this.id = id;
        this.rotation = rotation;
        this.nfp = nfp;
    }

    public Vector() {
        nfp = new Paths();
    }

    @Override
    public String toString() {
        return  "x = "+ x+" , y = "+y ;
    }
}
