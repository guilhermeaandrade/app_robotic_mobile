package com.example.guilherme.tcc_1_4.Utils;

public class SingletonInformation {

    private static SingletonInformation mInstance = null;
    private Double controlPValue;
    private Double controlIValue;
    private Double xValueInitial, yValueInitial, xValueAlvo, yValueAlvo;

    private SingletonInformation(){
        xValueInitial = Constants.X;
        yValueInitial = Constants.Y;
        xValueAlvo = Constants.X;
        yValueAlvo = Constants.Y;
        controlIValue = Constants.KI_INITIAL;
        controlPValue = Constants.KP_INITIAL;
    }

    public static SingletonInformation getInstance () {
        if(mInstance == null) mInstance = new SingletonInformation();
        return mInstance;
    }

    public Double getxValueInitial() {
        return xValueInitial;
    }

    public void setxValueInitial(Double xValueInitial) {
        this.xValueInitial = xValueInitial;
    }

    public Double getyValueInitial() {
        return yValueInitial;
    }

    public void setyValueInitial(Double yValueInitial) {
        this.yValueInitial = yValueInitial;
    }

    public Double getxValueAlvo() {
        return xValueAlvo;
    }

    public void setxValueAlvo(Double xValueAlvo) {
        this.xValueAlvo = xValueAlvo;
    }

    public Double getyValueAlvo() {
        return yValueAlvo;
    }

    public void setyValueAlvo(Double yValueAlvo) {
        this.yValueAlvo = yValueAlvo;
    }

    public Double getControlPValue() {
        return controlPValue;
    }

    public void setControlPValue(Double controlPValue) {
        this.controlPValue = controlPValue;
    }

    public Double getControlIValue() {
        return controlIValue;
    }

    public void setControlIValue(Double controlIValue) {
        this.controlIValue = controlIValue;
    }
}
