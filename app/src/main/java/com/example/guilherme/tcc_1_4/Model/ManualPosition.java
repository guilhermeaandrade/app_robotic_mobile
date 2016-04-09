package com.example.guilherme.tcc_1_4.Model;

import android.os.Parcel;
import android.os.Parcelable;


public class ManualPosition implements Parcelable {

    private Double x;
    private Double y;
    private Double theta;
    private Double v;

    public ManualPosition(Double x, Double y, Double theta, Double v){
        this.x = x;
        this.y = y;
        this.theta = theta;
        this.v = v;
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

    }

    public static final Parcelable.Creator<ManualPosition> CREATOR
            = new Parcelable.Creator<ManualPosition>() {
        public ManualPosition createFromParcel(Parcel in) {
            return new ManualPosition(in);
        }

        public ManualPosition[] newArray(int size) {
            return new ManualPosition[size];
        }
    };

    private ManualPosition(Parcel in) {
        x = in.readDouble();
        y = in.readDouble();
        theta = in.readDouble();
        v = in.readDouble();
    }
}
