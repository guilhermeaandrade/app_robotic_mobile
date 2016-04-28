package com.example.guilherme.tcc_1_4.Utils;


import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import com.example.guilherme.tcc_1_4.Model.Position;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.ArrayList;
import java.util.List;

public class SingletonConnection {

    private static SingletonConnection mInstance = null;
    private List<Position> moviments;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private DataOutputStream output;
    private DataInputStream input;

    private SingletonConnection(){
        moviments = new ArrayList<Position>();
    }

    public synchronized static SingletonConnection getInstance(){
        if(mInstance == null) mInstance = new SingletonConnection();
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

    public void clearMovimentsList(){
        this.moviments.clear();
    }
}
