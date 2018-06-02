package com.qunhe.util.nest.data;

/**
 * Created with Intellij IDEA.
 *
 * @author : yisa
 */
public class NfpKey {

    int A;
    int B;
    boolean inside;
    int Arotation;
    int Brotation;

    public NfpKey(int a, int b, boolean inside, int arotation, int brotation) {
        A = a;
        B = b;
        this.inside = inside;
        Arotation = arotation;
        Brotation = brotation;
    }

    public NfpKey() {
    }

    public int getA() {
        return A;
    }

    public void setA(int a) {
        A = a;
    }

    public int getB() {
        return B;
    }

    public void setB(int b) {
        B = b;
    }

    public boolean isInside() {
        return inside;
    }

    public void setInside(boolean inside) {
        this.inside = inside;
    }

    public int getArotation() {
        return Arotation;
    }

    public void setArotation(int arotation) {
        Arotation = arotation;
    }

    public int getBrotation() {
        return Brotation;
    }

    public void setBrotation(int brotation) {
        Brotation = brotation;
    }
}
