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
import android.widget.Toast;

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
    //private int optionControl; //1-automatico, 2-manual

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
            //optionControl = extrasBundle.getInt("optionControl");
        }

        mViewPager = (ViewPager) findViewById(R.id.vp_tabs);
        mViewPager.setAdapter(new TabsAdapter(getSupportFragmentManager(), ProcessActivity.this, device));

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
                        if (SingletonInformation.getInstance().getOptionControl() == 1 && !SingletonInformation.getInstance().isFinishedTransfer()) {
                            Toast.makeText(getApplicationContext(), "Ação não pode ser realizada!\n" +
                                    "Controle Automático em execução!", Toast.LENGTH_LONG).show();
                        } else {
                            finalizeConnection();
                            Intent intent = new Intent(ProcessActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("disableBluetooth", true);
                            startActivity(intent);
                        }
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
                        if (SingletonInformation.getInstance().getOptionControl() == 1 && !SingletonInformation.getInstance().isFinishedTransfer()) {
                            Toast.makeText(getApplicationContext(), "Ação não pode ser realizada!\n" +
                                    "Controle Automático em execução!", Toast.LENGTH_LONG).show();
                        } else {
                            finalizeConnection();
                            Intent intent = new Intent(ProcessActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            intent.putExtra("exit", true);
                            startActivity(intent);
                        }
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

    private void finalizeConnection(){
        if(SingletonConnection.getInstance().getAdapter().isEnabled())
            SingletonConnection.getInstance().getAdapter().disable();

        SingletonInformation.getInstance().setxValueInitial(Constants.X);
        SingletonInformation.getInstance().setyValueInitial(Constants.Y);
        SingletonInformation.getInstance().setxValueAlvo(Constants.X);
        SingletonInformation.getInstance().setyValueAlvo(Constants.Y);
        SingletonInformation.getInstance().setControlIValue(Constants.KI_INITIAL);
        SingletonInformation.getInstance().setControlPValue(Constants.KP_INITIAL);

        SingletonConnection.getInstance().clearMovimentsList();

        try {
            if(SingletonConnection.getInstance().getSocket() != null &&
                    SingletonConnection.getInstance().getSocket().isConnected())
                SingletonConnection.getInstance().getSocket().close();

            SingletonConnection.getInstance().setDevice(null);
            SingletonConnection.getInstance().setInput(null);
            SingletonConnection.getInstance().setOutput(null);
            SingletonConnection.getInstance().setSocket(null);

        } catch(IOException e) {
            Log.e(Constants.TAG, e.getMessage().toString());
        }
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
            case android.R.id.home:
                finish();
                break;

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
