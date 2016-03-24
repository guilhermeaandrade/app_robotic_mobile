package com.example.guilherme.tcc_1_4.Controller;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guilherme.tcc_1_4.R;


public class MapActivity extends AppCompatActivity{

    private BluetoothDevice device;
    private Toolbar mToolbar;
    private ImageView ivRobo;
    private TextView tvName;
    private TextView tvEndMac;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity_layout);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("Mapa Ilustrativo");
        mToolbar.setLogo(R.drawable.ic_launcher);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        if(!extrasBundle.isEmpty()){
            device = extrasBundle.getParcelable("device");
            if(device == null) {
                launchRingDialog();
            }
            if(device != null){
                ivRobo = (ImageView) findViewById(R.id.robo_photo);
                ivRobo.setImageResource(R.drawable.robo);

                tvName = (TextView) findViewById(R.id.name_robo);
                tvName.setText(device.getName());

                tvEndMac = (TextView) findViewById(R.id.end_mac_robo);
                tvEndMac.setText(device.getAddress());
            }
        }
    }

    public void launchRingDialog() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(MapActivity.this, "Please wait ...", "Searching for connections and settings ...", true);
        ringProgressDialog.setCancelable(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(4000);
                } catch (Exception e) {}
                ringProgressDialog.dismiss();
            }
        }).start();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }
        return true;
    }

}
