package com.example.bodysway;

public class Mesure implements Comparable<Object> {

    private float x;

    private float z;

    public Mesure() {
    }

    public Mesure(float x, float z) {
        this.x = x;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    @Override
    public int compareTo(Object o) {
        Mesure f = (Mesure) o;
        return (int)((this.x * 1000000) - (f.getX() * 1000000));
    }
}
