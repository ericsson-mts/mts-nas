package com.ericsson.mts.NAS.message;

public class Length {
    private int length;
    private int ub;
    private int lb;

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getUb() {
        return ub;
    }

    public void setUb(int ub) {
        this.ub = ub;
    }

    public int getLb() {
        return lb;
    }

    public void setLb(int lb) {
        this.lb = lb;
    }
}
