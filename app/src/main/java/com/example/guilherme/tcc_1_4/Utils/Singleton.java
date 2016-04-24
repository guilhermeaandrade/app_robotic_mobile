package com.example.guilherme.tcc_1_4.Utils;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.example.guilherme.tcc_1_4.Model.Position;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Singleton {

    private static Singleton mInstance = null;
    private List<Position> moviments;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private DataOutputStream output;
    private DataInputStream input;

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

    public BluetoothDevice getDevice() {
        return device;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public BluetoothSocket getSocket() {
        return socket;
    }

    public void setSocket(BluetoothSocket socket) {
        this.socket = socket;
    }

    public DataOutputStream getOutput() {
        return output;
    }

    public void setOutput(DataOutputStream output) {
        this.output = output;
    }

    public DataInputStream getInput() {
        return input;
    }

    public void setInput(DataInputStream input) {
        this.input = input;
    }
}
