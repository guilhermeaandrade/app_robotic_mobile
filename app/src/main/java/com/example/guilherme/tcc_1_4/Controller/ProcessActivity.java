package com.example.guilherme.tcc_1_4.Controller;

import android.app.AlertDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import com.example.guilherme.tcc_1_4.Adapter.TabsAdapter;
import com.example.guilherme.tcc_1_4.Extra.SlidingTabLayout;
import com.example.guilherme.tcc_1_4.Model.Position;
import com.example.guilherme.tcc_1_4.R;
import com.example.guilherme.tcc_1_4.Utils.Constants;
import com.example.guilherme.tcc_1_4.Utils.SharedPreference;
import com.example.guilherme.tcc_1_4.Utils.SingletonConnection;
import com.example.guilherme.tcc_1_4.Utils.SingletonInformation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ProcessActivity extends AppCompatActivity{

    private BluetoothDevice device;
    private Toolbar mToolbar;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private static List<Position> moviments;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(!bundle.isEmpty()){
                moviments = bundle.getParcelableArrayList("moviments");
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_activity_layout);

        moviments =  new ArrayList<Position>();
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("data-event"));

        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        if(!extrasBundle.isEmpty()){
            device = extrasBundle.getParcelable("device");
        }

        mViewPager = (ViewPager) findViewById(R.id.vp_tabs);
        mViewPager.setAdapter(new TabsAdapter(getSupportFragmentManager(), ProcessActivity.this, device, moviments));

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorSelectedTab));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle(Constants.PROCESS);
        mToolbar.setLogo(R.drawable.ic_robo);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void showDisableBluetoothDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder
                .setTitle("Desativar Bluetooth ")
                .setMessage("Bluetooth está ativo e comunicando com outro dispositivo. Deseja desativá-lo?")
                .setIcon(R.drawable.ic_bluetooth_connect)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.show();
    }

    private void showFinishDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder
                .setTitle("Sair ")
                .setMessage("Deseja sair da aplicação?")
                .setIcon(R.drawable.ic_logout)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.show();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.action_disabled_bluetooth:
                showDisableBluetoothDialog();
                break;

            case R.id.action_out:
                showFinishDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
