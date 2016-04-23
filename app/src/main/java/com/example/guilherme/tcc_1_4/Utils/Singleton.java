package com.example.guilherme.tcc_1_4.Utils;


import com.example.guilherme.tcc_1_4.Model.Position;

import java.util.ArrayList;
import java.util.List;

public class Singleton {

    private static Singleton mInstance = null;
    private List<Position> moviments;

    private Singleton(){
        moviments = new ArrayList<Position>();
    }

    public synchronized static Singleton getInstance(){
        if(mInstance == null) mInstance = new Singleton();
        return mInstance;
    }

    public synchronized void setMoviments(List<Position> pos){
        mInstance.moviments = pos;
    }

    public synchronized List<Position> getMoviments(){
        return mInstance.moviments;
    }
}
