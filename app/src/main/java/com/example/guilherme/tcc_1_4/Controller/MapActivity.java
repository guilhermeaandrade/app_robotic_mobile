package com.example.guilherme.tcc_1_4.Controller;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guilherme.tcc_1_4.Extra.ImageHelper;
import com.example.guilherme.tcc_1_4.R;


public class MapActivity extends AppCompatActivity{

    private BluetoothDevice device;
    private Toolbar mToolbar;
    private ImageView ivRobo;
    private TextView tvName;
    private TextView tvEndMac;

    private float scale;
    private int width;
    private int height;
    private int roundPixels;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_activity_layout);

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("Mapa Ilustrativo");
        mToolbar.setLogo(R.drawable.ic_launcher);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        scale = this.getResources().getDisplayMetrics().density;
        width = this.getResources().getDisplayMetrics().widthPixels - (int)(14 * scale + 0.5f);
        height = (width/16)*9;

        roundPixels = (int)(2 * scale + 0.5f);


        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        if(!extrasBundle.isEmpty()){
            device = extrasBundle.getParcelable("device");
            if(device == null) {
                launchRingDialog();
            }
            if(device != null){

                Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.robo);
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
                bitmap = ImageHelper.getRoundedCornerBitmap(this, bitmap, 15, width, height, false, false, true, true);

                ivRobo = (ImageView) findViewById(R.id.robo_photo);
                ivRobo.setImageBitmap(bitmap);
                //ivRobo.setImageResource(R.drawable.robo);

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
