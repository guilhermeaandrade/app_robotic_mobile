package com.example.guilherme.tcc_1_4.Model;

public class Posicao {

    private long id;
    private float coordX;
    private float coordY;
    private float anguloTeta;
    private Data data;

    public Posicao(float coordX, float coordY, float anguloTeta, Data data) {
        this.coordX = coordX;
        this.coordY = coordY;
        this.anguloTeta = anguloTeta;
        this.data = data;
    }

    public Posicao(){}

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public float getCoordX() {
        return coordX;
    }

    public void setCoordX(float coordX) {
        this.coordX = coordX;
    }

    public float getCoordY() {
        return coordY;
    }

    public void setCoordY(float coordY) {
        this.coordY = coordY;
    }

    public float getAnguloTeta() {
        return anguloTeta;
    }

    public void setAnguloTeta(float anguloTeta) {
        this.anguloTeta = anguloTeta;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
