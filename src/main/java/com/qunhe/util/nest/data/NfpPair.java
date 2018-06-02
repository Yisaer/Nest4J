package com.qunhe.util.nest.data;

/**
 * @author yisa
 */
public class NfpPair {
    NestPath A;
    NestPath B;
    NfpKey key;


    public NfpPair(NestPath a, NestPath b, NfpKey key) {
        A = a;
        B = b;
        this.key = key;
    }

    public NestPath getA() {
        return A;
    }

    public void setA(NestPath a) {
        A = a;
    }

    public NestPath getB() {
        return B;
    }

    public void setB(NestPath b) {
        B = b;
    }

    public NfpKey getKey() {
        return key;
    }

    public void setKey(NfpKey key) {
        this.key = key;
    }
}
