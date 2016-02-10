package com.example.guilherme.tcc_1_4.Controller;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guilherme.tcc_1_4.R;

import java.util.Set;

public class Tela2 extends AppCompatActivity{

    private BluetoothAdapter adaptador;
    private ListView lista;
    private ArrayAdapter<String> dispositivos;
    private Button btPesquisar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tela02);

        adaptador = BluetoothAdapter.getDefaultAdapter();
        btPesquisar = (Button) findViewById(R.id.btPesquisar);
        btPesquisar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                pesquisar();
            }
        });

        lista = (ListView) findViewById(R.id.lvLista);
        dispositivos = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1);
        lista.setAdapter(dispositivos);

        lista.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String info = ((TextView) view).getText().toString();
                String address = info.substring(info.length() - 17);

                Intent it = new Intent();
                it.putExtra("msg", address);
                setResult(Activity.RESULT_OK, it);
                finish();
            }
        });

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        Set<BluetoothDevice> pairedDevices = adaptador.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                dispositivos.add(device.getName() + "\n"+ device.getAddress());
            }
        }
    }

    private void pesquisar(){
        alerta("Pesquisando ... ");
        if (adaptador.isDiscovering()) {
            adaptador.cancelDiscovery();
        }
        adaptador.startDiscovery();
        dispositivos.clear();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adaptador != null) {
            adaptador.cancelDiscovery();
        }

        this.unregisterReceiver(mReceiver);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                dispositivos.add(device.getName() + "\n"+ device.getAddress());
            }
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private final Handler h = new Handler() {
        public void handleMessage(Message msg) {
            String content = (String) msg.obj;
            Toast.makeText(Tela2.this, content, Toast.LENGTH_SHORT).show();
        }
    };

    public void alerta(String message) {
        Message m = h.obtainMessage();
        m.obj = message;
        h.sendMessage(m);
    }
}
