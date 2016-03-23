package com.example.guilherme.tcc_1_4.Controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.example.guilherme.tcc_1_4.Model.Robo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;

public class BluetoothService {
    private static UUID MY_UUID;

    private final BluetoothAdapter mAdapter;
    private final Handler mHandler;
    private AcceptThread mAcceptThread;
    private ConnectThread mConnectThread;
    private ConnectedThread mConnectedThread;
    private int mState;

    private ArrayList<String> mDeviceAddresses;
    private ArrayList<String> mDeviceNames;
    private ArrayList<ConnectedThread> mConnThreads;
    private ArrayList<BluetoothSocket> mSockets;
    private Set<BluetoothDevice> pairedDevices;
    private Set<BluetoothDevice> allNXTDevices;
    private int quantityNXTDevices;

    private ArrayList<UUID> mUuids;

    private Context context;

    //Construtor
    public BluetoothService(Context context, Handler handler) {
        this.mAdapter = BluetoothAdapter.getDefaultAdapter();
        this.mState = BluetoothConstants.STATE_NONE;
        this.mHandler = handler;
        this.context = context;
        this.pairedDevices = new HashSet<BluetoothDevice>();
        this.allNXTDevices = new HashSet<BluetoothDevice>();
        quantityNXTDevices = 0;
        initializeArrayLists();
    }

    public Set<BluetoothDevice> getPairedDevices() {
        return this.mAdapter.getBondedDevices();
    }

    public BluetoothAdapter getmAdapter() {
        return mAdapter;
    }

    public static UUID getMY_UUID() {
        return MY_UUID;
    }

    public static void setMY_UUID(UUID mY_UUID) {
        MY_UUID = mY_UUID;
    }

    public boolean isDeviceConnectedAtPosition(int position) {
        if (mConnThreads.get(position) == null)
            return false;
        return true;
    }

    private void initializeArrayLists() {
        mDeviceAddresses = new ArrayList<String>(5);
        mDeviceNames = new ArrayList<String>(5);
        mConnThreads = new ArrayList<ConnectedThread>(5);
        mSockets = new ArrayList<BluetoothSocket>(5);
        mUuids = new ArrayList<UUID>(5);

        for (int i = 0; i < BluetoothConstants.QUANTITY_ROBOTS; i++) {
            allNXTDevices.add(null);
            pairedDevices.add(null);
            mDeviceAddresses.add(null);
            mDeviceNames.add(null);
            mConnThreads.add(null);
            mSockets.add(null);
            mUuids.add(null);
        }
    }

    public ArrayList<String> getmDeviceNames() {
        return this.mDeviceNames;
    }

    public void setmDeviceNames(ArrayList<String> mDeviceNames) {
        this.mDeviceNames = mDeviceNames;
    }

    public ArrayList<String> getmDeviceAddresses() {
        return mDeviceAddresses;
    }

    public void setmDeviceAddresses(ArrayList<String> mDeviceAddresses) {
        this.mDeviceAddresses = mDeviceAddresses;
    }

    private synchronized void setState(int state) {
        mState = state;
        mHandler.obtainMessage(BluetoothConstants.MESSAGE_STATE_CHANGE, state, -1).sendToTarget();
    }

    public synchronized int getState() {
        return mState;
    }

    public synchronized void start() {
        this.pairedDevices = mAdapter.getBondedDevices();
        setState(BluetoothConstants.STATE_LISTEN);
        // Cancel any thread attempting to make a connection
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }

        // Cancel any thread currently running a connection
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }

        // Start the thread to listen on a BluetoothServerSocket
        //if (mAcceptThread == null) {
          //  mAcceptThread = new AcceptThread();
            //mAcceptThread.start();
        //}
        //this.pairedDevices = mAdapter.getBondedDevices();
        //getAllDevicesNXT();
    }

    public void getAllDevicesNXT(){
        Log.d(BluetoothConstants.TAG, "getAllDevicesNXT()");
        int countNXTDevices = 0;
        allNXTDevices.clear();
        for (BluetoothDevice device: pairedDevices) {
            if(device.getName().equals("NXT")){
                countNXTDevices++;
                allNXTDevices.add(device);
                Robo robo = new Robo();
                robo.setNomeRobo("NXT-"+countNXTDevices);
                robo.setEnderecoMAC(device.getAddress());
                robo.setStatusRobo(1);
                robo.setTipoRobo(1);
            }
        }
        quantityNXTDevices = countNXTDevices;
        Log.d(BluetoothConstants.TAG, "getAllDevicesNXT() -> quantity: " + quantityNXTDevices);
        doConnections();
    }

    public void doConnections(){
        Log.d(BluetoothConstants.TAG, "doConnections()");
        int i = 0;
        for (BluetoothDevice device: allNXTDevices) {
            Log.d(BluetoothConstants.TAG, "doConnections() device: "+device.getAddress());
            if(getAvailablePositionIndexForNewConnection(device) != -1) {
                connect(device, i);
            }
            i++;
        }
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver(){

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                pairedDevices.add(device);
            }
        }
    };

    public synchronized void connect(BluetoothDevice device, int selectedPosition) {
        if (getPositionIndexOfDevice(device) == -1) {
            Log.d(BluetoothConstants.TAG, "connect() -> if");
            // Cancel any thread attempting to make a connection
            if (mState == BluetoothConstants.STATE_CONNECTING) {
                Log.d(BluetoothConstants.TAG, "connect() -> if mstate");
                if (mConnectThread != null) {
                    Log.d(BluetoothConstants.TAG, "connect() -> if mConnectThread");
                    mConnectThread.cancel();
                    mConnectThread = null;
                }
            }
            // Cancel any thread currently running a connection
            if (mConnThreads.get(selectedPosition) != null) {
                Log.d(BluetoothConstants.TAG, "connect() -> if mConnThreads");
                mConnThreads.get(selectedPosition).cancel();
                mConnectedThread = null;
                mConnThreads.set(selectedPosition, null);
            }

            try {
                ConnectThread mConnectThread = new ConnectThread(device,
                        UUID.fromString("00001101-0000-1000-8000-"
                                + device.getAddress().replace(":", "")),
                        selectedPosition);
                mConnectThread.start();
                setState(BluetoothConstants.STATE_CONNECTING);
            } catch (Exception e) {
            }
        } else {
            Message msg = mHandler
                    .obtainMessage(BluetoothConstants.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString(BluetoothConstants.TOAST,
                    "This device " + device.getName() + " Already Connected");
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    public synchronized void connected(BluetoothSocket socket, BluetoothDevice device, int selectedPosition) {
        Log.d(BluetoothConstants.TAG, "connected()");
        ConnectedThread mConnectedThread = new ConnectedThread(socket);
        mConnectedThread.start();
        mConnThreads.set(selectedPosition, mConnectedThread);

        Message msg = mHandler
                .obtainMessage(BluetoothConstants.MESSAGE_DEVICE_NAME);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothConstants.DEVICE_NAME, device.getName());
        msg.setData(bundle);
        mHandler.sendMessage(msg);
        setState(BluetoothConstants.STATE_CONNECTED);
    }

    public synchronized void stop() {
        if (mConnectThread != null) {
            mConnectThread.cancel();
            mConnectThread = null;
        }
        if (mConnectedThread != null) {
            mConnectedThread.cancel();
            mConnectedThread = null;
        }
        if (mAcceptThread != null) {
            mAcceptThread.cancel();
            mAcceptThread = null;
        }

        for (int i = 0; i < BluetoothConstants.QUANTITY_ROBOTS; i++) {
            mDeviceNames.set(i, null);
            mDeviceAddresses.set(i, null);
            mSockets.set(i, null);
            if (mConnThreads.get(i) != null) {
                mConnThreads.get(i).cancel();
                mConnThreads.set(i, null);
            }
        }
        setState(BluetoothConstants.STATE_NONE);
    }

    public void write(byte[] out) {
        for (int i = 0; i < mConnThreads.size(); i++) {
            try {
                ConnectedThread r;
                synchronized (this) {
                    if (mState != BluetoothConstants.STATE_CONNECTED)
                        return;
                    r = mConnThreads.get(i);
                }
                r.write(out);
            } catch (Exception e) {
            }
        }
    }

    private void connectionFailed() {
        Log.d(BluetoothConstants.TAG, "connectionFailed()");
        setState(BluetoothConstants.STATE_LISTEN);

        Message msg = mHandler.obtainMessage(BluetoothConstants.MESSAGE_TOAST);
        Bundle bundle = new Bundle();
        bundle.putString(BluetoothConstants.TOAST, "Unable to connect device");
        msg.setData(bundle);
        mHandler.sendMessage(msg);

    }

    private void connectionLost(BluetoothDevice device) {
        int positionIndex = getPositionIndexOfDevice(device);
        if (positionIndex != -1) {

            mDeviceAddresses.set(positionIndex, null);
            mDeviceNames.set(positionIndex, null);
            mConnThreads.set(positionIndex, null);

            Message msg = mHandler
                    .obtainMessage(BluetoothConstants.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString(BluetoothConstants.TOAST,
                    "Device connection was lost from " + device.getName());
            msg.setData(bundle);
            mHandler.sendMessage(msg);
        }
    }

    public void searchDevices(){
        if(mAdapter.isDiscovering()){
            mAdapter.cancelDiscovery();
        }
        mAdapter.startDiscovery();
        pairedDevices.clear();
    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;

        public AcceptThread() {
            Log.d(BluetoothConstants.TAG, "AcceptThread()");
            BluetoothServerSocket tmp = null;
            try {
                if (mAdapter.isEnabled()) {
                    Log.d(BluetoothConstants.TAG, "AcceptThread() -> try -> if");
                    BluetoothService.setMY_UUID(UUID
                            .fromString("00001101-0000-1000-8000-"
                                    + mAdapter.getAddress().replace(":", "")));
                }
                tmp = mAdapter.listenUsingRfcommWithServiceRecord(BluetoothConstants.NAME,
                        BluetoothService.getMY_UUID());
                Log.d(BluetoothConstants.TAG, "AcceptThread() -> try -> tmp: "+tmp.toString());
            } catch (IOException e) {
            }
            mmServerSocket = tmp;
        }

        public void run() {
            setName("AcceptThread");
            BluetoothSocket socket = null;
            while (mState != BluetoothConstants.STATE_CONNECTED) {
                try {
                    Log.d(BluetoothConstants.TAG, "AcceptThread() -> run() -> while -> try");
                    socket = mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                if (socket != null) {
                    Log.d(BluetoothConstants.TAG, "AcceptThread() -> run() -> while -> if");
                    synchronized (BluetoothService.this) {
                        switch (mState) {
                            case BluetoothConstants.STATE_LISTEN:
                            case BluetoothConstants.STATE_CONNECTING:
                                connected(socket, socket.getRemoteDevice(),getAvailablePositionIndexForNewConnection(socket.getRemoteDevice()));
                                break;
                            case BluetoothConstants.STATE_NONE:
                            case BluetoothConstants.STATE_CONNECTED:
                                try {
                                    socket.close();
                                } catch (IOException e) {
                                }
                                break;
                        }
                    }
                }
            }
        }

        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {}
        }
    }

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private UUID tempUuid;
        private int selectedPosition;

        public ConnectThread(BluetoothDevice device, UUID uuidToTry,
                             int selectedPosition) {
            Log.d(BluetoothConstants.TAG, "ConnectThread -> device: "+device.getAddress()+" - UUID: "+uuidToTry.toString() + "selected: "+selectedPosition);
            mmDevice = device;
            BluetoothSocket tmp = null;
            tempUuid = uuidToTry;
            this.selectedPosition = selectedPosition;
            try {
                tmp = device.createRfcommSocketToServiceRecord(uuidToTry);
                Log.d(BluetoothConstants.TAG, "ConnectThread -> try -> tmp: "+tmp.getRemoteDevice());
            } catch (IOException e) {}
            mmSocket = tmp;
            Log.d(BluetoothConstants.TAG, "ConnectThread mmSocket: "+ mmSocket.getRemoteDevice());
        }

        public void run() {
            Log.d(BluetoothConstants.TAG, "ConnectThread() -> run()");
            setName("ConnectThread");
            mAdapter.cancelDiscovery();

            try {
                mmSocket.connect();
                Log.d(BluetoothConstants.TAG, "ConnectThread() -> run() -> try");
            } catch (IOException e) {
                Log.d(BluetoothConstants.TAG, "ConnectThread() -> run() -> catch");
                Log.d(BluetoothConstants.TAG, "ConnectThread() -> run() -> catch e: "+e.getMessage());
                connectionFailed();
                try {
                    mmSocket.close();
                } catch (IOException e2) {}
                BluetoothService.this.start();
                return;
            }

            synchronized (BluetoothService.this) {
                mConnectThread = null;
            }
            mDeviceAddresses.set(selectedPosition, mmDevice.getAddress());
            mDeviceNames.set(selectedPosition, mmDevice.getName());
            Log.d(BluetoothConstants.TAG, "ConnectThread() -> run() -> device: "+ mmDevice.getAddress() );
            connected(mmSocket, mmDevice, selectedPosition);
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {}
        }
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {}

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = mmInStream.read(buffer);
                    mHandler.obtainMessage(BluetoothConstants.MESSAGE_READ, bytes, getPositionIndexOfDevice(mmSocket.getRemoteDevice()),
                            buffer).sendToTarget();
                   } catch (IOException e) {
                        connectionLost(mmSocket.getRemoteDevice());
                        break;
                }
            }
        }

        public void write(byte[] buffer) {
            try {
                mmOutStream.write(buffer);
                mHandler.obtainMessage(BluetoothConstants.MESSAGE_WRITE, -1,
                        -1, buffer).sendToTarget();
            } catch (IOException e) {}
        }

        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {}
        }
    }

    public void searching(){
        alerta("Pesquisando...");
        if(mAdapter.isDiscovering()){
            mAdapter.cancelDiscovery();
        }
        mAdapter.startDiscovery();
        pairedDevices.clear();
    }

    private final Handler h = new Handler() {
        public void handleMessage(Message msg) {
            String content = (String) msg.obj;
        }
    };

    public void alerta(String message) {
        Message m = h.obtainMessage();
        m.obj = message;
        h.sendMessage(m);
    }

    public int getPositionIndexOfDevice(BluetoothDevice device) {
        for (int i = 0; i < mDeviceAddresses.size(); i++) {
            if (mDeviceAddresses.get(i) != null
                    && mDeviceAddresses.get(i).equalsIgnoreCase(
                    device.getAddress()))
                return i;
        }
        return -1;
    }

    public int getAvailablePositionIndexForNewConnection(BluetoothDevice device) {
        if (getPositionIndexOfDevice(device) == -1) {
            for (int i = 0; i < mDeviceAddresses.size(); i++) {
                if (mDeviceAddresses.get(i) == null) {
                    return i;
                }
            }
        }
        return -1;
    }
}