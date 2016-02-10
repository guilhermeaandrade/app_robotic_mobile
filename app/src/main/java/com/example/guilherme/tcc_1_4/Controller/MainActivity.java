package com.example.guilherme.tcc_1_4.Controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guilherme.tcc_1_4.Adapter.TabsAdapter;
import com.example.guilherme.tcc_1_4.Extra.SlidingTabLayout;
import com.example.guilherme.tcc_1_4.Fragment.ComponentFragment;
import com.example.guilherme.tcc_1_4.Fragment.MapFragment;
import com.example.guilherme.tcc_1_4.Fragment.ProcessFragment;
import com.example.guilherme.tcc_1_4.Fragment.RoboFragment;
import com.example.guilherme.tcc_1_4.Fragment.SettingsFragment;
import com.example.guilherme.tcc_1_4.Model.Robo;
import com.example.guilherme.tcc_1_4.R;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.OnCheckedChangeListener;

import org.w3c.dom.Text;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    //VARIAVEIS APLICACAO
    private static final int NUMBER_ROBO = 4;
    private static String TAG = "LOG";
    private static final int[] idPhoto = new int[]{R.drawable.i1,R.drawable.i2, R.drawable.i3, R.drawable.i4};
    public List<Robo> listRobos;
    private Integer coordenadaX;
    private Integer coordenadaY;

    //VARIAVEIS TABS
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    //VARIAVEIS NAVIGATION
    private int mItemDrawerSelected;
    private int mPositionClicked;
    private boolean notificationClicked = true;
    private Drawer.Result navigationDrawerLeft;
    private Drawer.Result navigationDrawerRight;
    private AccountHeader.Result headerNavigationLeft;
    private OnCheckedChangeListener mOnCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(IDrawerItem iDrawerItem, CompoundButton compoundButton, boolean b) {
            Toast.makeText(MainActivity.this, "onCheckedChanged: " + (b ? "True" : "False"), Toast.LENGTH_SHORT).show();
        }
    };

    //VARIAVEIS TOOLBAR
    private Toolbar mToolbar;
    private Toolbar mToolbarBottom; //CASO SEJA NECESSÁRIO TOOLBAR EMBAIXO

    //VARIAVEIS BLUETOOTH
    private static final int REQUEST_ENABLE_BT = 1;
    boolean bluetoothAtivo = false;
    private ArrayList<String> dispositivos;
    private ArrayList<BluetoothDevice> dispositivosLEGO;
    private BluetoothAdapter mbtAdapter;
    private BluetoothDevice mbtDevice_LEGO_1;
    private BluetoothDevice mbtDevice_LEGO_2;
    private BluetoothSocket mbtSocket_1;
    private BluetoothSocket mbtSocket_2;
    //private DataOutputStream output;
    //private DataInputStream input;
    private String address_device_1;
    private String address_device_2;
    private ConnectThread connectDevice_1;
    private ConnectThread connectDevice_2;

    /*private BluetoothChatService mChatService = null;
    private StringBuffer mOutStringBuffer;
    private String mConnectedDeviceName = null;
    private static final UUID MY_UUID_SECURE = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final UUID MY_UUID_INSECURE = UUID.fromString("8ce255c0-200a-11e0-ac64-0800200c9a66");
    private boolean flag_bluetooth_enable = true;
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;
    private ArrayList<String> devices;
    private IntentFilter filter;
    private BroadcastReceiver broadcastReceiver;
    private Handler mHandler;
    private static final int SUCCES_CONNECT = 0;
    private static final int MESSAGE_READ = 1;

    private int mState;
    private static final String NAME_SECURE = "BluetoothChatSecure";
    private static final String NAME_INSECURE = "BluetoothChatInsecure";
    public static final int STATE_NONE = 0;
    public static final int STATE_LISTEN = 1;
    public static final int STATE_CONNECTING = 2;
    public static final int STATE_CONNECTED = 3;

    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //TRANSITIONS
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            Explode transGo = new Explode();
            transGo.setDuration(3000);

            Fade transBack = new Fade();
            transBack.setDuration(3000);

            getWindow().setExitTransition(transGo);
            getWindow().setReenterTransition(transBack);
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init(savedInstanceState); //inicializa variaveis

        mbtAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mbtAdapter == null){
            Toast.makeText(getApplicationContext(), "Este dispositivo não suporta conexão Bluetooth", Toast.LENGTH_LONG).show();
        }else {
            if (!mbtAdapter.isEnabled()) {
                Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBTIntent, REQUEST_ENABLE_BT);
            }
            showInitialDialog();
        }
    }

    public void init(Bundle savedInstanceState){
        // ----------------------------- BLUETOOTH -----------------------------------------------------
        mbtDevice_LEGO_1 = null;
        mbtDevice_LEGO_2 = null;
        address_device_1 = null;
        address_device_2 = null;

        dispositivos = new ArrayList<String>();
        dispositivosLEGO = new ArrayList<BluetoothDevice>();

        // ------------------------------ LIST ROBOS ----------------------------------------------------
        listRobos = new ArrayList<Robo>();
        listRobos = getSetRoboList(4);

        // ------------------------------ TABS e VIEWPAGER ----------------------------------------------
        mViewPager = (ViewPager) findViewById(R.id.vp_tabs);
        mViewPager.setAdapter(new TabsAdapter(getSupportFragmentManager(), MainActivity.this));

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorSelectedTab));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //Caso tab e navigation tem os mesmos componentes
                //navigationDrawerLeft.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        // ----------------------------- TOOLBAR -------------------------------------------------------
        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("CONTIC");
        mToolbar.setSubtitle("Robótica");
        mToolbar.setLogo(R.drawable.ic_launcher);
        setSupportActionBar(mToolbar);

        // ---------------------------- NAVIGATIONDRAWER --------------------------------------------------
        headerNavigationLeft = new AccountHeader()
                .withActivity(this)
                .withCompactStyle(false)
                .withSavedInstance(savedInstanceState)
                .withThreeSmallProfileImages(false)
                .withHeaderBackground(sortBackgraound())
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {

                    @Override
                    public boolean onProfileChanged(View view, IProfile iProfile, boolean b) {
                        Toast.makeText(MainActivity.this, "onProfileChanged", Toast.LENGTH_SHORT).show();
                        headerNavigationLeft.setBackgroundRes(sortBackgraound());
                        navigationDrawerLeft.getAdapter().notifyDataSetChanged();
                        return true;
                    }
                })
                .build();

        //LEFT
        navigationDrawerLeft = new Drawer()
                .withActivity(this)
                .withToolbar(mToolbar)
                .withDisplayBelowToolbar(true)
                .withActionBarDrawerToggleAnimated(true)
                .withDrawerGravity(Gravity.LEFT)
                .withSavedInstance(savedInstanceState)
                .withAccountHeader(headerNavigationLeft)
                .withSelectedItem(-1)
                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {

                    @Override
                    public boolean onNavigationClickListener(View view) {
                        Toast.makeText(MainActivity.this, "onNavigationClickListener: " + mPositionClicked, Toast.LENGTH_SHORT).show();
                        //Caso seja aberto uma nova activity
                        //dou o finish() para voltar para a outra activity
                        return false;
                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l, IDrawerItem iDrawerItem) {
                        //Toast.makeText(MainActivity.this, "onItemClick: " + position, Toast.LENGTH_SHORT).show();

                        int valor = sortBackgraound(); //SORTEAR BACKGROUND

                        for (int count = 0, tam = navigationDrawerLeft.getDrawerItems().size(); count < tam;  count++) {
                            if(count == mPositionClicked && mPositionClicked != 2 && mPositionClicked <= 3){
                                PrimaryDrawerItem aux = (PrimaryDrawerItem) navigationDrawerLeft.getDrawerItems().get(count);
                                aux.setIcon(getResources().getDrawable(getCorrectDrawerIcon(count,false)));
                                break;
                            }
                        }

                        if(position <= 3){
                            ((PrimaryDrawerItem) iDrawerItem).setIcon(getResources().getDrawable(getCorrectDrawerIcon(position,true)));
                        }

                        if(position == 4){
                            ((SwitchDrawerItem) iDrawerItem).setIcon(getResources().getDrawable(getCorrectDrawerIcon(position,true)));
                            headerNavigationLeft.setBackgroundRes(valor);
                            navigationDrawerLeft.getAdapter().notifyDataSetChanged();
                        }

                        mPositionClicked = position;
                        navigationDrawerLeft.setStatusBarColor(getResources().getColor(R.color.colorBackgroundNavigation));
                        navigationDrawerLeft.getAdapter().notifyDataSetChanged(); //Responsavel pela alteração


                        mItemDrawerSelected = position;
                        Fragment frag = null;

                        switch (mItemDrawerSelected) {
                            case 0:
                                headerNavigationLeft.setBackgroundRes(valor);
                                navigationDrawerLeft.getAdapter().notifyDataSetChanged();

                                //TODA IMPLEMENTAÇÃO PARA COMPONENTES
                                startActivity(new Intent(MainActivity.this, ComponentActivity.class));

                                break;
                            case 1:
                                headerNavigationLeft.setBackgroundRes(valor);
                                navigationDrawerLeft.getAdapter().notifyDataSetChanged();

                                //TODA IMPLEMENTAÇÃO PARA MAPA_ILUSTRATIVO
                                startActivity(new Intent(MainActivity.this, MapActivity.class));

                                break;
                            case 3:
                                headerNavigationLeft.setBackgroundRes(valor);
                                navigationDrawerLeft.getAdapter().notifyDataSetChanged();

                                //TODA IMPLEMENTAÇÃO PARA CONFIGURAÇÕES
                                break;
                            case 4:
                                headerNavigationLeft.setBackgroundRes(valor);
                                navigationDrawerLeft.getAdapter().notifyDataSetChanged();
                                //TODA IMPLEMENTAÇÃO PARA NOTIFICAÇÕES
                                break;
                        }
                    }
                }).withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l, IDrawerItem iDrawerItem) {
                        Toast.makeText(MainActivity.this, "onItemLongClick: "+position, Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }).build();

        navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName("Componentes")
                .withIcon(getResources().getDrawable(R.drawable.componentes)));
        navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName("Mapa Ilustrativo do Processo")
                .withIcon(getResources().getDrawable(R.drawable.map)));
        navigationDrawerLeft.addItem(new DividerDrawerItem());
        navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName("Configurações")
                .withIcon(getResources().getDrawable(R.drawable.settings)));
        navigationDrawerLeft.addItem(new SwitchDrawerItem().withName("Notificação")
                .withChecked(notificationClicked).withOnCheckedChangeListener(mOnCheckedChangeListener)
                .withIcon(getResources().getDrawable(R.drawable.note))
                .withSelectedColor(getResources().getColor(R.color.colorPrimary)));

        //RIGHT
        navigationDrawerRight = new Drawer()
                .withActivity(this)
                .withDisplayBelowToolbar(true)
                .withActionBarDrawerToggleAnimated(true)
                .withDrawerGravity(Gravity.END)
                .withSavedInstance(savedInstanceState)
                .withSelectedItem(-1)
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener(){

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l, IDrawerItem iDrawerItem) {

                    }
                }).withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l, IDrawerItem iDrawerItem) {

                        return false;
                    }
                }).build();

        navigationDrawerRight.addItem(new PrimaryDrawerItem().withName("Componentes")
                .withIcon(getResources().getDrawable(R.drawable.componentes)));
        navigationDrawerRight.addItem(new PrimaryDrawerItem().withName("Mapa Ilustrativo do Processo")
                .withIcon(getResources().getDrawable(R.drawable.map)));
        navigationDrawerRight.addItem(new DividerDrawerItem());
        navigationDrawerRight.addItem(new PrimaryDrawerItem().withName("Configurações")
                .withIcon(getResources().getDrawable(R.drawable.settings)));
        navigationDrawerRight.addItem(new SwitchDrawerItem().withName("Notificação")
                .withChecked(notificationClicked).withOnCheckedChangeListener(mOnCheckedChangeListener)
                .withIcon(getResources().getDrawable(R.drawable.note))
                .withSelectedColor(getResources().getColor(R.color.colorPrimary)));
    }

    public void showInitialDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_initial_dialog, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Coordenadas Alvo");
        dialogBuilder.setMessage("Digite, por favor, as coordenadas alvo desejadas: ");
        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {
                EditText coordX = (EditText) dialogView.findViewById(R.id.coordX);
                EditText coordY = (EditText) dialogView.findViewById(R.id.coordY);

                TextView tv_ast_sup = (TextView) dialogView.findViewById(R.id.tv_asterisco_sup);
                TextView tv_ast_inf = (TextView) dialogView.findViewById(R.id.tv_asterisco_inf);
                TextView tv_fieldOb = (TextView) dialogView.findViewById(R.id.tv_fieldObrigatorio);


                if(!checkIfIsEmpty(coordX.getText().toString()) && !checkIfIsEmpty(coordY.getText().toString())){
                    Integer coordenadaX = Integer.parseInt(coordX.getText().toString().trim());
                    setCoordenadaX(coordenadaX);

                    Integer coordenadaY = Integer.parseInt(coordY.getText().toString().trim());
                    setCoordenadaY(coordenadaY);
                }else{
                    tv_fieldOb.setTextColor(Color.RED);
                    if(checkIfIsEmpty(coordX.getText().toString())){
                        tv_ast_sup.setTextColor(Color.RED);
                        coordX.requestFocus();
                    }
                    if(checkIfIsEmpty(coordY.getText().toString())) {
                        tv_ast_inf.setTextColor(Color.RED);
                        coordY.requestFocus();
                    }
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Toast.makeText(MainActivity.this, "As coordenadas alvo podem ser definidas posteriormente acessando o Menu", Toast.LENGTH_LONG).show();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.show();
    }

    //##############################################################################################################################
    //METODO NAVIGATION DRAWER
    private int getCorrectDrawerIcon(int position, boolean isSelected){
        switch (position){
            //case 0:
                //return (isSelected ? R.drawable.main_selecetd: R.drawable.main);
            case 0:
                return (isSelected ? R.drawable.componentes_selected : R.drawable.componentes);
            case 1:
                return (isSelected ? R.drawable.map_selected : R.drawable.map);
            case 3:
                return (isSelected ? R.drawable.settings_selected : R.drawable.settings);
            case 4:
                return (isSelected ? R.drawable.note_selected: R.drawable.note);
        }
        return (0);
    }

    private int sortBackgraound(){
        Random gerador = new Random();
        int valor = (gerador.nextInt(4));
        return idPhoto[valor];
    }

    //##############################################################################################################################
    // METODOS BLUETOOTH
    public void conectarDispositivos(){
        pesquisarDispositivos();

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        byte velocidade = 10;
        char comando = 'a';

        Set<BluetoothDevice> pairedDevices = mbtAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            Toast.makeText(getApplicationContext(), "DISPOSITIVOS PAREADOS", Toast.LENGTH_SHORT).show();
            for (BluetoothDevice device : pairedDevices) {
                dispositivos.add(device.getName() + ","+ device.getAddress());
                if(device.getName().equalsIgnoreCase("NXT")){
                    dispositivosLEGO.add(device);
                }
            }
        }

        if(!dispositivosLEGO.isEmpty()){
            address_device_1 = dispositivosLEGO.get(0).getAddress();
            mbtDevice_LEGO_1 = mbtAdapter.getRemoteDevice(address_device_1);
            if(mbtDevice_LEGO_1 != null){
                //Toast.makeText(getApplicationContext(), "removete device 1", Toast.LENGTH_SHORT).show();
                connectDevice_1 = new ConnectThread(mbtDevice_LEGO_1);
                mbtSocket_1 = connectDevice_1.getSocket();
                if(mbtSocket_1 != null){
                    connectDevice_1.start();
                    new Enviar(comando, velocidade, mbtSocket_1).start();
                    //for(int i = 0; i < 10; i++){

                    //}
                }
            }

            address_device_2 = dispositivosLEGO.get(1).getAddress();
            mbtDevice_LEGO_2 = mbtAdapter.getRemoteDevice(address_device_2);
            if(mbtDevice_LEGO_2 != null){
                //Toast.makeText(getApplicationContext(), "remote device 2", Toast.LENGTH_SHORT).show();
                connectDevice_2 = new ConnectThread(mbtDevice_LEGO_2);
                mbtSocket_2 = connectDevice_2.getSocket();
                connectDevice_2.start();
                for(int i = 0; i < 10; i++){
                    new Enviar(comando, velocidade, mbtSocket_2).start();
                }
            }
        }else {
            Toast.makeText(getApplicationContext(), "Nenhum dispositivo LEGO encontrado", Toast.LENGTH_SHORT).show();
        }
    }

    public void pesquisarDispositivos(){
        if(mbtAdapter.isDiscovering()){
            mbtAdapter.cancelDiscovery();
        }
        mbtAdapter.startDiscovery();
        dispositivos.clear();
    }

    private final Handler handler = new Handler(){
        public void handleMessage(Message msg){
            String content = (String) msg.obj;
            Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT).show();
        }
    };

    public void alerta(String mensagem){
        Message msg = handler.obtainMessage();
        msg.obj = mensagem;
        handler.sendMessage(msg);
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {

        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                dispositivos.add(device.getName() + "\n"+ device.getAddress());
                if(device.getName().equalsIgnoreCase("NXT")){
                    dispositivosLEGO.add(device);
                }
            }
        }
    };

    private class ConnectThread extends Thread{
        private final BluetoothDevice mmDevice;
        private final BluetoothSocket socket;

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
            mbtAdapter.cancelDiscovery();
            try {
                socket.connect();
                if(socket.isConnected()){
                    //alerta("Dispositivo conectado: " + socket.getRemoteDevice().getAddress());
                }else{
                    //alerta("Dispositivo nao conectado");
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            return;
        }

        public void cancel(){
            try {
                socket.close();
                //alerta("Conexão Fechada");
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        public BluetoothSocket getSocket() {
            return socket;
        }
    }

    public class Enviar extends Thread{
        private char letra;
        private byte speed;
        private final BluetoothSocket socket;

        private DataOutputStream output;
        private DataInputStream input;

        public Enviar(char letra, byte speed, BluetoothSocket sckt){
            this.letra = letra;
            this.speed = speed;
            this.socket = sckt;
        }

        public void run(){
            try {
                if(socket != null){
                    //alerta("Socket diferente de null");
                    output = new DataOutputStream(socket.getOutputStream());
                    input = new DataInputStream(socket.getInputStream());
                    //alerta("Nome dispositivo: "+socket.getRemoteDevice().getAddress());
                    if(socket.isConnected()){
                        //alerta("if: => "+letra + " - " + speed);
                        output.writeChar(letra);
                        output.writeByte(speed);
                        output.flush();
                    }else{
                        alerta(letra + " - " + speed);
                    }
                }
            }catch (IOException erro){
                alerta("Erro de Transferência");
            }
        }
    }

    //##############################################################################################################################
    //METODO RECYCLERVIEW - CARDVIEW
    public List<Robo> getSetRoboList(int quantidade){
        String[] names = new String[]{"Robo 1", "Robo 2", "Robo 3", "Robo 4"};
        int[] types = new int[]{1, 2, 2, 2};
        String[] macs = new String[]{""};
        List<Robo> listAux = new ArrayList<Robo>();

        for (int i = 0; i < quantidade; i++) {
            Robo r = new Robo(names[i % names.length], types[i % types.length], idPhoto[i % idPhoto.length]);
            listAux.add(r);
        }
        return listAux;
    }

    //##############################################################################################################################
    // METODOS APLICACAO

    public boolean checkIfIsEmpty(String coord){
        if(coord.trim().equals("")){
            return true;
        }
        return false;
    }

    public Integer getCoordenadaX() {
        return coordenadaX;
    }

    public void setCoordenadaX(Integer coordenadaX) {
        this.coordenadaX = coordenadaX;
    }

    public Integer getCoordenadaY() {
        return coordenadaY;
    }

    public void setCoordenadaY(Integer coordenadaY) {
        this.coordenadaY = coordenadaY;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mbtAdapter != null) {
            mbtAdapter.cancelDiscovery();
        }

        this.unregisterReceiver(mReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        if(navigationDrawerLeft.isDrawerOpen()){
            navigationDrawerLeft.closeDrawer();
        }else{
            super.onBackPressed();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        //outState.putParcelableArrayList("listRobos", (ArrayList<Robo>)listRobos);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_ENABLE_BT){
            if(resultCode == Activity.RESULT_OK){
                bluetoothAtivo = true;
                conectarDispositivos();
                showInitialDialog(); // MÉTODO RESPONSÁVEL POR DEFINIR AS COORDENADAS INICIAIS
            }else if(resultCode == Activity.RESULT_CANCELED){
                bluetoothAtivo = false;
            }
        }
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
                //TODA AÇÃO PARA DESATIVAR BLUETOOTH
                Toast.makeText(MainActivity.this, "Selected: Desativar Bluetooth", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_define_coordinate:
                //TODA AÇÃO PARA DEFINIR COORDENADAS ALVO
                showInitialDialog();
                break;

            case R.id.action_reset_sys:
                //TODA AÇÃO PARA RESETAR SISTEMA
                Toast.makeText(MainActivity.this, "Selected: Resetar Sistema", Toast.LENGTH_SHORT).show();
                break;

            case R.id.action_out:
                //TODA AÇÃO PARA SAIR
                Toast.makeText(MainActivity.this, "Selected: Sair", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //###################################################################################################################
}
