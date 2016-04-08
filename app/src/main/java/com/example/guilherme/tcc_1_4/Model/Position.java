package com.example.guilherme.tcc_1_4.Model;

import android.os.Parcel;
import android.os.Parcelable;

public class Position implements Parcelable{

    private Double x;
    private Double y;
    private Double theta;
    private Double v;
    private Double w;
    private Double e;
    private Float t;

    public Position(Double x, Double y, Double theta, Double v, Double w, Double e, Float t){
        this.x = x;
        this.y = y;
        this.theta = theta;
        this.v = v;
        this.w = w;
        this.e = e;
        this.t = t;
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

    public Float getT() {
        return t;
    }

    public void setT(Float t) {
        this.t = t;
    }

    @Override
    public int describeContents() {
        return hashCode();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(x);
        dest.writeDouble(y);
        dest.writeDouble(theta);
        dest.writeDouble(v);
        dest.writeDouble(w);
        dest.writeDouble(e);
        dest.writeFloat(t);
    }

    public static final Parcelable.Creator<Position> CREATOR
            = new Parcelable.Creator<Position>() {
        public Position createFromParcel(Parcel in) {
            return new Position(in);
        }

        public Position[] newArray(int size) {
            return new Position[size];
        }
    };

    private Position(Parcel in) {
        x = in.readDouble();
        y = in.readDouble();
        theta = in.readDouble();
        v = in.readDouble();
        w = in.readDouble();
        e = in.readDouble();
        t = in.readFloat();
    }
}
