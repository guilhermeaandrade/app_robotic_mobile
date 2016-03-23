package com.example.guilherme.tcc_1_4.Controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.guilherme.tcc_1_4.Extra.SlidingTabLayout;
import com.example.guilherme.tcc_1_4.Model.Robo;
import com.example.guilherme.tcc_1_4.R;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.OnCheckedChangeListener;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    //VARIAVEIS APLICACAO
    private static final int NUMBER_ROBO = 0;
    private static final int[] idPhoto = new int[]{R.drawable.i1,R.drawable.i2, R.drawable.i3, R.drawable.i4};
    public List<Robo> listRobos;
    private Button btConectar;
    private Button btSelectType;
    private Button btForward;
    private Button btBackward;
    private Button btRight;
    private Button btLeft;
    private SeekBar sbVelocidade;
    private byte velocidade = 1;
    private boolean pressedUp = false;

    private BluetoothAdapter adaptador;
    private BluetoothDevice device;
    private BluetoothDevice mDevice;
    private BluetoothSocket socket;
    private DataOutputStream output;
    private DataInputStream input;
    private Intent it;
    private boolean bluetoothIsAtivo = false;
    private static final int TELA2 = 2;
    private int option = -1;

    private String address;
    private ConnectThread teste;

    private final CharSequence[] items = {"Controle Automático", "Controle Manual"};

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

        init(savedInstanceState);

        device = null;
        address = null;
        it = new Intent(this, SelectDevice.class);

        adaptador = BluetoothAdapter.getDefaultAdapter();
        if(!adaptador.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, 1);
        }

        //botao para conectar
        btConectar = (Button) findViewById(R.id.btnConnect);
        btConectar.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                if(adaptador.isEnabled()){
                    startActivityForResult(it,TELA2);
                }else{
                    Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBTIntent, 1);
                }
            }
        });

        btSelectType = (Button) findViewById(R.id.btnTypeControl);
        btSelectType.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v){
                showControlDialog();
            }
        });

        //botao para frente
        btForward = (Button) findViewById(R.id.btnForward);
        btForward.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(device == null){
                    Toast.makeText(getApplicationContext(), "Nenhum dispositivo conectado", Toast.LENGTH_LONG).show();
                }else if(device != null){
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            if(pressedUp == false){
                                pressedUp = true;
                                new Enviar('m','w', velocidade).start();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            new Enviar('m','q', velocidade).start();
                            pressedUp = false;
                            break;
                    }
                }

                return true;
            }
        });

        //botao para tras
        btBackward = (Button) findViewById(R.id.btnBackward);
        btBackward.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(device == null){
                    Toast.makeText(getApplicationContext(), "Nenhum dispositivo conectado", Toast.LENGTH_LONG).show();
                }else if(device != null){
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            if(pressedUp == false){
                                pressedUp = true;
                                new Enviar('m', 's', velocidade).start();

                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            new Enviar('m', 'q', velocidade).start();
                            pressedUp = false;
                            break;
                    }
                }
                return true;
            }
        });

        //botao para esquerda
        btLeft = (Button) findViewById(R.id.btnLeft);
        btLeft.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(device == null){
                    Toast.makeText(getApplicationContext(), "Nenhum dispositivo conectado", Toast.LENGTH_LONG).show();
                }else if(device != null){
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            if(pressedUp == false){
                                pressedUp = true;
                                new Enviar('m', 'a', velocidade).start();

                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            new Enviar('m', 'q', velocidade).start();
                            pressedUp = false;
                            break;
                    }
                }
                return true;
            }
        });

        //botao para direita
        btRight = (Button) findViewById(R.id.btnRight);
        btRight.setOnTouchListener(new View.OnTouchListener(){

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(device == null){
                    Toast.makeText(getApplicationContext(), "Nenhum dispositivo conectado", Toast.LENGTH_LONG).show();
                }else if(device != null){
                    switch (event.getAction()){
                        case MotionEvent.ACTION_DOWN:
                            if(pressedUp == false){
                                pressedUp = true;
                                new Enviar('m', 'd', velocidade).start();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            new Enviar('m', 'q', velocidade).start();
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
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        disableAllButtons();

    }

    //##############################################################################################################################
    public void init(Bundle savedInstanceState){
        // ------------------------------ TABS e VIEWPAGER ----------------------------------------------
        mViewPager = (ViewPager) findViewById(R.id.vp_tabs);

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
        mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorSelectedTab));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
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

                        return false;
                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l, IDrawerItem iDrawerItem) {
                        int valor = sortBackgraound(); //SORTEAR BACKGROUND

                        for (int count = 0, tam = navigationDrawerLeft.getDrawerItems().size(); count < tam;  count++) {
                            if(count == mPositionClicked && mPositionClicked != 1 && mPositionClicked <= 3){
                                PrimaryDrawerItem aux = (PrimaryDrawerItem) navigationDrawerLeft.getDrawerItems().get(count);
                                aux.setIcon(getResources().getDrawable(getCorrectDrawerIcon(count,false)));
                                break;
                            }
                        }

                        if(position <= 3){
                            ((PrimaryDrawerItem) iDrawerItem).setIcon(getResources().getDrawable(getCorrectDrawerIcon(position,true)));
                        }

                        if(position == 3){
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

                                startActivity(new Intent(MainActivity.this, MapActivity.class));

                                break;
                            case 2:
                                headerNavigationLeft.setBackgroundRes(valor);
                                navigationDrawerLeft.getAdapter().notifyDataSetChanged();

                                break;
                            case 3:
                                headerNavigationLeft.setBackgroundRes(valor);
                                navigationDrawerLeft.getAdapter().notifyDataSetChanged();

                                break;

                        }
                    }
                }).withOnDrawerItemLongClickListener(new Drawer.OnDrawerItemLongClickListener() {

                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l, IDrawerItem iDrawerItem) {
                        return false;
                    }
                }).build();

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

    private int getCorrectDrawerIcon(int position, boolean isSelected){
        switch (position){
            case 0:
                return (isSelected ? R.drawable.map_selected : R.drawable.map);
            case 2:
                return (isSelected ? R.drawable.settings_selected : R.drawable.settings);
            case 3:
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
    public void showControlDialog(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Escolha o tipo de controle: ")
                .setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                option = 1;
                                break;
                            case 1:
                                option = 2;
                                break;
                        }
                    }
                }).setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (option != -1) {
                    if (option == 1) {
                        new Enviar('u', 'q', Byte.parseByte("0")).start();
                        disableAllButtons();
                    } else if (option == 2) {
                        enableAllButtons();
                        new Enviar('m', 'q', Byte.parseByte("0")).start();
                    }
                }
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //SE A ESCOLHA FOR CANCEL.. A ESCOLHA PADRÃO SERÁ CONTROLE AUTOMATICO
                disableAllButtons();
                new Enviar('u', 'q', Byte.parseByte("0")).start();
            }
        });

        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.show();
    }

    public void enableAllButtons(){
        btBackward.setEnabled(true);
        btForward.setEnabled(true);
        btLeft.setEnabled(true);
        btRight.setEnabled(true);
        sbVelocidade.setEnabled(true);
        if(device != null) btSelectType.setEnabled(true);
    }

    public void disableAllButtons(){
        btBackward.setEnabled(false);
        btForward.setEnabled(false);
        btLeft.setEnabled(false);
        btRight.setEnabled(false);
        sbVelocidade.setEnabled(false);
        if(device == null) btSelectType.setEnabled(false);
    }

    //##############################################################################################################################
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

    public class Enviar extends Thread{
        private Character letra;
        private Byte speed;
        private Character controle;


        public Enviar(Character controle, Character letra, Byte speed) {
            this.letra = letra;
            this.speed = speed;
            this.controle = controle;
        }

        public void run(){
            try {
                if(socket != null){
                    output = new DataOutputStream(socket.getOutputStream());
                    input = new DataInputStream(socket.getInputStream());
                    if(socket.isConnected()){
                        if(controle != null) output.writeChar(controle);
                        if(letra != null && speed != null) {
                            output.writeChar(letra);
                            output.writeByte(speed);
                        }
                        output.flush();
                    }
                }
            }catch (IOException erro){

            }
        }
    }

    //###############################################################################################################################
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == TELA2){
            if(data == null) return;
            showControlDialog();
            address = data.getExtras().getString("msg");

            device = adaptador.getRemoteDevice(address);
            teste = new ConnectThread(device);

            teste.start();
        }

        if(requestCode == BluetoothConstants.REQUEST_ENABLE_BT){
            if(resultCode == Activity.RESULT_OK){
                Toast.makeText(this, "Conecte-se a um dispositivo para iniciar a aplicação", Toast.LENGTH_LONG).show();
            }else if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "Ative o Bluetooth antes de iniciar a aplicação", Toast.LENGTH_LONG).show();
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
                break;

            case R.id.action_out:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
