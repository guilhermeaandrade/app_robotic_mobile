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
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guilherme.tcc_1_4.R;

import java.util.Set;

public class SelectDevice extends AppCompatActivity{

    private Toolbar mToolbar;
    private BluetoothAdapter adaptador;
    private ListView lista;
    private ArrayAdapter<String> dispositivos;
    private Button btPesquisar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_device_activity);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("Dispositivos");
        mToolbar.setLogo(R.drawable.ic_launcher);
        setSupportActionBar(mToolbar);
        mToolbar.dismissPopupMenus();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


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
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private final Handler h = new Handler() {
        public void handleMessage(Message msg) {
            String content = (String) msg.obj;
            Toast.makeText(SelectDevice.this, content, Toast.LENGTH_SHORT).show();
        }
    };

    public void alerta(String message) {
        Message m = h.obtainMessage();
        m.obj = message;
        h.sendMessage(m);
    }
}
