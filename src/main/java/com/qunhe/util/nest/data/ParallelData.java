package com.qunhe.util.nest.data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yisa
 */
public class ParallelData {
    public NfpKey key ;
    public List<NestPath> value ;

    public ParallelData() {
        value = new ArrayList<NestPath>();
    }

    public ParallelData(NfpKey key, List<NestPath> value) {
        this.key = key;
        this.value = value;
    }

    public NfpKey getKey() {
        return key;
    }

    public void setKey(NfpKey key) {
        this.key = key;
    }

    public List<NestPath> getValue() {
        return value;
    }

    public void setValue(List<NestPath> value) {
        this.value = value;
    }
}
