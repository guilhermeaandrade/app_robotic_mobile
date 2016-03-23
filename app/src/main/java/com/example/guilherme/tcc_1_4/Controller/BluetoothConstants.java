package com.example.guilherme.tcc_1_4.Controller;

/**
 * Created by guilherme.alvarenga on 27/02/2016.
 */
public class BluetoothConstants {
    public static final int REQUEST_ENABLE_BT = 1;
    public static final int QUANTITY_ROBOTS = 2;
    public static final String TAG = "BluetoothService";
    public static final boolean D = true;
    public static final String NAME = "i";

    public static final int STATE_NONE = 0; //NENHUMA CONEXÃO
    public static final int STATE_LISTEN = 1; //ESCUTANDO POR CONNECTIONS
    public static final int STATE_CONNECTING = 2; //INICIANDO UMA CONEXÃO EXTERNA
    public static final int STATE_CONNECTED = 3; //CONECTADO COM UMA DISPOSITIVO REMOTO

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
}
