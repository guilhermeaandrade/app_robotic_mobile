package com.example.guilherme.tcc_1_4.Model;

public class Position {

    private Double x;
    private Double y;
    private Double theta;
    private Double v;
    private Double w;
    private Double e;

    public Position(Double x, Double y, Double theta, Double v, Double w, Double e){
        this.x = x;
        this.y = y;
        this.theta = theta;
        this.v = v;
        this.w = w;
        this.e = e;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getTheta() {
        return theta;
    }

    public void setTheta(Double theta) {
        this.theta = theta;
    }

    public Double getV() {
        return v;
    }

    public void setV(Double v) {
        this.v = v;
    }

    public Double getW() {
        return w;
    }

    public void setW(Double w) {
        this.w = w;
    }

    public Double getE() {
        return e;
    }

    public void setE(Double e) {
        this.e = e;
    }
}
