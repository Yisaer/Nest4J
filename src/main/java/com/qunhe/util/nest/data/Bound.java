package com.qunhe.util.nest.data;

/**
 * @author yisa
 */
public class Bound {
    public double xmin;
    public double ymin;
    public double width;
    public double height;

    public Bound(double xmin, double ymin, double width, double height) {
        this.xmin = xmin;
        this.ymin = ymin;
        this.width = width;
        this.height = height;
    }

    public Bound() {
    }

    public double getXmin() {
        return xmin;
    }

    public void setXmin(double xmin) {
        this.xmin = xmin;
    }

    public double getYmin() {
        return ymin;
    }

    public void setYmin(double ymin) {
        this.ymin = ymin;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    @Override
    public String toString() {
        return "xmin = "+ xmin +" , ymin = "+ymin +" , width = "+width + ", height = "+height;
    }
}
