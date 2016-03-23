package com.example.guilherme.tcc_1_4.Controller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.guilherme.tcc_1_4.R;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class ManualControlActivity  extends AppCompatActivity{

    private Toolbar mToolbar;

    private Button btConectar;
    private Button btForward;
    private Button btBackward;
    private Button btRight;
    private Button btLeft;
    private SeekBar sbVelocidade;
    private byte velocidade = 1;
    private boolean pressedUp = false;

    private int idRobo;

    //VAR
    private BluetoothAdapter adaptador;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private DataOutputStream output;
    private DataInputStream input;
    private Intent it;
    private static final int TELA2 = 1;

    private String address;
    private ConnectThread teste;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manual_control_activity);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("Controle Manual");
        mToolbar.setLogo(R.drawable.ic_launcher);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //indice do robo selecionado
        Intent intent = getIntent();
        Bundle params = intent.getExtras();

        if(params != null){
            idRobo = params.getInt("positionList");
        }
        //-------------------------------------------------------------------------------------------------------------------
        //INICIO DO CODIGO
        device = null;
        address = null;
        it = new Intent(this, Tela2.class);

        adaptador = BluetoothAdapter.getDefaultAdapter();
        if(!adaptador.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, 1);
        }

        //botao para conectar
        btConectar = (Button) findViewById(R.id.btConectar);
        btConectar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                startActivityForResult(it,TELA2);
            }
        });

        //botao para frente
        btForward = (Button) findViewById(R.id.btForward);
        btForward.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(device == null){
                    alerta("Sem dispositivo conectado");
                }else if(device != null){
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            if(pressedUp == false){
                                pressedUp = true;
                                new Enviar('w', velocidade).start();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            new Enviar('q', velocidade).start();
                            pressedUp = false;
                            break;
                    }
                }

                return true;
            }
        });

        //botao para tras
        btBackward = (Button) findViewById(R.id.btBackward);
        btBackward.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(device == null){
                    alerta("Sem dispositivo conectado");
                }else if(device != null){
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            if(pressedUp == false){
                                pressedUp = true;
                                new Enviar('s', velocidade).start();

                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            new Enviar('q', velocidade).start();
                            pressedUp = false;
                            break;
                    }
                }
                return true;
            }
        });

        //botao para esquerda
        btLeft = (Button) findViewById(R.id.btLeft);
        btLeft.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(device == null){
                    alerta("Sem dispositivo conectado");
                }else if(device != null){
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            if(pressedUp == false){
                                pressedUp = true;
                                new Enviar('a', velocidade).start();

                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            new Enviar('q', velocidade).start();
                            pressedUp = false;
                            break;
                    }
                }
                return true;
            }
        });

        //botao para direita
        btRight = (Button) findViewById(R.id.btRight);
        btRight.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(device == null){
                    alerta("Sem dispositivo conectado");
                }else if(device != null){
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            if(pressedUp == false){
                                pressedUp = true;
                                new Enviar('d', velocidade).start();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            new Enviar('q', velocidade).start();
                            pressedUp = false;
                            break;
                    }
                }
                return true;
            }
        });

        sbVelocidade = (SeekBar) findViewById(R.id.seekBar);
        sbVelocidade.setProgress(velocidade);
        sbVelocidade.setMinimumWidth(1);
        sbVelocidade.setMax(50);
        sbVelocidade.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                velocidade = (byte) progress;
                Toast.makeText(getApplicationContext(), ""+velocidade, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }
        return true;
    }

    private class ConnectThread extends Thread{
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device){
            mmDevice = device;
            BluetoothSocket tmp = null;
            try {
                tmp = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
            }catch (IOException e){
                e.printStackTrace();
            }
            socket = tmp;
        }

        public void run(){
            adaptador.cancelDiscovery();
            try {
                socket.connect();
            }catch (IOException e){
                e.printStackTrace();
            }
            return;
        }

        public void cancel(){
            try {
                socket.close();
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(data == null){
            alerta("Nenhum endereço selecionado");
            return;
        }

        address = data.getExtras().getString("msg");
        alerta("Novo endereço: "+address);

        device = adaptador.getRemoteDevice(address);
        teste = new ConnectThread(device);

        teste.start();
    }

    public class Enviar extends Thread{
            private char letra;
            private byte speed;

            public Enviar(char letra, byte speed){
                this.letra = letra;
                this.speed = speed;
            }

            public void run(){
                try {
                    if(socket != null){
                        output = new DataOutputStream(socket.getOutputStream());
                        input = new DataInputStream(socket.getInputStream());
                        if(socket.isConnected()){
                            output.writeChar(letra);
                            output.writeByte(speed);
                            output.flush();
                        }else{
                            alerta(letra + " - " + speed);
                            alerta("Conexão Fechada");
                        }
                    }
                }catch (IOException erro){
                    alerta("Erro de Transferência");
                }
            }
    }

    private final Handler h = new Handler(){
        public void handleMessage(Message msg){
            String content = (String) msg.obj;
            Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT)
                    .show();
        }
    };

    public void alerta(String message) {
        Message m = h.obtainMessage();
        m.obj = message;
        h.sendMessage(m);
    }
}
