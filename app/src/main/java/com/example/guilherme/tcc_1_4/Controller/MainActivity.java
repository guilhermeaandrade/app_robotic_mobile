package com.example.guilherme.tcc_1_4.Controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.transition.Explode;
import android.transition.Fade;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.guilherme.tcc_1_4.Extra.SlidingTabLayout;
import com.example.guilherme.tcc_1_4.Model.Position;
import com.example.guilherme.tcc_1_4.R;
import com.example.guilherme.tcc_1_4.Utils.Constants;
import com.example.guilherme.tcc_1_4.Utils.InputFilterMinMax;
import com.example.guilherme.tcc_1_4.Utils.Mask;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SwitchDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.mikepenz.materialdrawer.model.interfaces.OnCheckedChangeListener;
import com.software.shell.fab.ActionButton;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_ENABLE_BT = 1;

    private List<Position> listOfPositions;
    private final int handlerState = 0;
    private Handler bluetoothIn;

    //VARIAVEIS APLICACAO
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
    private BluetoothSocket socket;
    private DataOutputStream output;
    private DataInputStream input;
    private Intent it;
    private static final int TELA2 = 2;
    private int option = -1;

    private String address;
    private ConnectThread teste;
    private Double controlValue;
    private Double coordXAlvo;
    private Double coordYAlvo;
    private Double coordXInicial;
    private Double coordYInicial;

    private final CharSequence[] items = {Constants.CONT_AUTO, Constants.CONT_MANUAL};

    //VARIAVEIS TABS
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;

    private FloatingActionMenu actionMenu;

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
            //referente ao click na Notificação
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
        btConectar.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (adaptador.isEnabled()) {
                    startActivityForResult(it, TELA2);
                } else {
                    Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBTIntent, 1);
                }
            }
        });

        btSelectType = (Button) findViewById(R.id.btnTypeControl);
        btSelectType.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                showControlDialog();
            }
        });

        //botao para frente
        btForward = (Button) findViewById(R.id.btnForward);
        btForward.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (device == null) {
                    Toast.makeText(getApplicationContext(), "Nenhum dispositivo conectado", Toast.LENGTH_LONG).show();
                } else if (device != null) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if (pressedUp == false) {
                                pressedUp = true;
                                new Enviar('m', 'w', Double.valueOf(velocidade)).start();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            new Enviar('m', 'q', Double.valueOf(velocidade)).start();
                            pressedUp = false;
                            break;
                    }
                }
                return true;
            }
        });

        //botao para tras
        btBackward = (Button) findViewById(R.id.btnBackward);
        btBackward.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (device == null) {
                    Toast.makeText(getApplicationContext(), "Nenhum dispositivo conectado", Toast.LENGTH_LONG).show();
                } else if (device != null) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if (pressedUp == false) {
                                pressedUp = true;
                                new Enviar('m', 's', Double.valueOf(velocidade)).start();

                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            new Enviar('m', 'q', Double.valueOf(velocidade)).start();
                            pressedUp = false;
                            break;
                    }
                }
                return true;
            }
        });

        //botao para esquerda
        btLeft = (Button) findViewById(R.id.btnLeft);
        btLeft.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (device == null) {
                    Toast.makeText(getApplicationContext(), "Nenhum dispositivo conectado", Toast.LENGTH_LONG).show();
                } else if (device != null) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if (pressedUp == false) {
                                pressedUp = true;
                                new Enviar('m', 'a', Double.valueOf(velocidade)).start();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            new Enviar('m', 'q', Double.valueOf(velocidade)).start();
                            pressedUp = false;
                            break;
                    }
                }
                return true;
            }
        });

        //botao para direita
        btRight = (Button) findViewById(R.id.btnRight);
        btRight.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (device == null) {
                    Toast.makeText(getApplicationContext(), "Nenhum dispositivo conectado", Toast.LENGTH_LONG).show();
                } else if (device != null) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if (pressedUp == false) {
                                pressedUp = true;
                                new Enviar('m', 'd', Double.valueOf(velocidade)).start();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            new Enviar('m', 'q', Double.valueOf(velocidade)).start();
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
        sbVelocidade.setProgress(sbVelocidade.getMax() / 2);
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

        bluetoothIn = new Handler(){

            @Override
            public void handleMessage(Message msg){
                String write = (String) msg.obj;
                byte[] writeBuf = write.getBytes();
                switch (msg.what){
                    case 1:
                        String readMessage = new String(writeBuf);
                        splitMessage(readMessage);
                        break;
                }
            }
        };

        actionMenu = (FloatingActionMenu) findViewById(R.id.actionMenu);
        actionMenu.setIconAnimated(false);
        actionMenu.getMenuIconView().setImageResource(R.drawable.ic_pencil);
        actionMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean b) {
                if(b){
                    actionMenu.setIconAnimated(false);
                    actionMenu.getMenuIconView().setImageResource(R.drawable.close);
                }else{
                    actionMenu.setIconAnimated(false);
                    actionMenu.getMenuIconView().setImageResource(R.drawable.ic_pencil);
                }
            }
        });
        actionMenu.showMenuButton(true);
        actionMenu.setClosedOnTouchOutside(true);

        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.fabController);
        fab1.setColorNormal(getResources().getColor(R.color.colorPrimary));
        fab1.setColorPressed(getResources().getColor(R.color.colorPrimaryPressed));
        fab1.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeControllerDialog();
            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.fabPosition);
        fab2.setColorNormal(getResources().getColor(R.color.colorPrimary));
        fab2.setColorPressed(getResources().getColor(R.color.colorPrimaryPressed));
        fab2.setOnClickListener(new FloatingActionButton.OnClickListener(){
            @Override
            public void onClick(View v){
                showChangePositionDialog(1);
            }
        });

        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.fabPositionAlvo);
        fab3.setColorNormal(getResources().getColor(R.color.colorPrimary));
        fab3.setColorPressed(getResources().getColor(R.color.colorPrimaryPressed));
        fab3.setOnClickListener(new FloatingActionButton.OnClickListener(){
            @Override
            public void onClick(View v){
                showChangePositionDialog(2);
            }
        });
    }

    //##############################################################################################################################
    private void init(Bundle savedInstanceState){
        // ------------------------------ TABS e VIEWPAGER ----------------------------------------------
        listOfPositions = new ArrayList<Position>();
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
        mToolbar.setTitle(Constants.APP_NAME);
        mToolbar.setSubtitle(Constants.APP_SUBTITLE);
        mToolbar.setLogo(R.drawable.ic_launcher);
        setSupportActionBar(mToolbar);

        // ---------------------------- NAVIGATIONDRAWER --------------------------------------------------
        headerNavigationLeft = new AccountHeader()
                .withActivity(this)
                .withCompactStyle(false)
                .withSavedInstance(savedInstanceState)
                .withThreeSmallProfileImages(false)
                .withHeaderBackground(R.drawable.robo)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {

                    @Override
                    public boolean onProfileChanged(View view, IProfile iProfile, boolean b) {
                        Toast.makeText(MainActivity.this, "onProfileChanged", Toast.LENGTH_SHORT).show();
                        headerNavigationLeft.setBackgroundRes(R.drawable.robo);
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
                            ((SwitchDrawerItem) iDrawerItem).setIcon(getResources().getDrawable(getCorrectDrawerIcon(position, true)));
                            headerNavigationLeft.setBackgroundRes(R.drawable.robo);
                            navigationDrawerLeft.getAdapter().notifyDataSetChanged();
                        }

                        mPositionClicked = position;
                        navigationDrawerLeft.setStatusBarColor(getResources().getColor(R.color.colorBackgroundNavigation));
                        navigationDrawerLeft.getAdapter().notifyDataSetChanged(); //Responsavel pela alteração


                        mItemDrawerSelected = position;
                        Fragment frag = null;

                        switch (mItemDrawerSelected) {
                            case 0:
                                headerNavigationLeft.setBackgroundRes(R.drawable.robo);
                                navigationDrawerLeft.getAdapter().notifyDataSetChanged();

                                if(device != null) {
                                    Intent i = new Intent(MainActivity.this, MapActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable("device", device);
                                    bundle.putParcelableArrayList("moviments", (ArrayList<? extends Parcelable>) listOfPositions);

                                    i.putExtras(bundle);
                                    startActivity(i);
                                    actionMenu.close(true);
                                }else{
                                    Toast.makeText(MainActivity.this, "Conecte-se a um dispositivo para iniciar essa atividade.", Toast.LENGTH_LONG).show();
                                }

                                break;
                            case 2:
                                headerNavigationLeft.setBackgroundRes(R.drawable.robo);
                                navigationDrawerLeft.getAdapter().notifyDataSetChanged();

                                break;
                            case 3:
                                headerNavigationLeft.setBackgroundRes(R.drawable.robo);
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

        navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName(Constants.PROCESS)
                .withIcon(getResources().getDrawable(R.drawable.main)));
        navigationDrawerLeft.addItem(new DividerDrawerItem());
        navigationDrawerLeft.addItem(new PrimaryDrawerItem().withName(Constants.CONFIGURATION)
                .withIcon(getResources().getDrawable(R.drawable.settings)));
        navigationDrawerLeft.addItem(new SwitchDrawerItem().withName(Constants.NOTIFICATION)
                .withChecked(notificationClicked).withOnCheckedChangeListener(mOnCheckedChangeListener)
                .withIcon(getResources().getDrawable(R.drawable.note))
                .withSelectedColor(getResources().getColor(R.color.colorPrimary)));
    }

    private int getCorrectDrawerIcon(int position, boolean isSelected){
        switch (position){
            case 0:
                return (isSelected ? R.drawable.main_selecetd : R.drawable.main);
            case 2:
                return (isSelected ? R.drawable.settings_selected : R.drawable.settings);
            case 3:
                return (isSelected ? R.drawable.note_selected: R.drawable.note);
        }
        return (0);
    }

    //##############################################################################################################################
    private void showChangeControllerDialog(){
        final EditText controllerValue;

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_controller_dialog, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Controlador Proporcional");
        dialogBuilder.setIcon(R.drawable.controller);

        controllerValue = (EditText) dialogView.findViewById(R.id.edtControllerValue);
        controllerValue.addTextChangedListener(Mask.insert("#.###", controllerValue));

        dialogBuilder.setMessage("Digite, por favor, o novo valor desejado: ");

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String valor = controllerValue.getText().toString().trim();
                        if(!valor.isEmpty()){
                            controlValue = Double.parseDouble(valor);
                        }else{
                            controlValue = Constants.KP_INITIAL;
                        }
                        new Enviar(Constants.C_SETTINGS, Constants.I_CONTROLLER, controlValue);
                    }

        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplication(), "O valor padrão para o controlador proporcional será " + Constants.KP_INITIAL, Toast.LENGTH_LONG).show();
            }
        });

        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.show();
    }

    private void showChangePositionDialog(final int option) {
        final EditText edtPosX;
        final EditText edtPosY;

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_position_dialog, null);
        dialogBuilder.setView(dialogView);

        if(option == 1){
            dialogBuilder.setTitle("Posição inicial");
            dialogBuilder.setMessage("Digite, por favor, as coordenadas iniciais desejadas (em cm): ");
            dialogBuilder.setIcon(R.drawable.location);
        }else {
            dialogBuilder.setTitle("Posição Alvo");
            dialogBuilder.setMessage("Digite, por favor, as coordenadas alvo desejadas (em cm): ");
            dialogBuilder.setIcon(R.drawable.final_location);
        }

        edtPosX = (EditText) dialogView.findViewById(R.id.coordX);
        edtPosX.addTextChangedListener(Mask.insert("##.##", edtPosX));

        edtPosY = (EditText) dialogView.findViewById(R.id.coordY);
        edtPosY.addTextChangedListener(Mask.insert("##.##", edtPosY));

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String x = edtPosX.getText().toString().trim();
                String y = edtPosY.getText().toString().trim();

                Double xValue, yValue;

                if(!x.isEmpty() && !y.isEmpty()){
                    xValue = Double.valueOf(x);
                    yValue = Double.valueOf(y);
                }else if(!x.isEmpty()){
                    xValue = Double.valueOf(x);
                    yValue = Constants.Y;
                }else if(!y.isEmpty()){
                    xValue = Constants.X;
                    yValue = Double.valueOf(y);
                }else{
                    xValue = Constants.X;
                    yValue = Constants.Y;
                }

                switch (option) {
                    case 1:
                        Log.i(Constants.TAG, "Valores: "+ xValue + " - " + yValue);
                        new Enviar(Constants.C_SETTINGS, Constants.I_POS_X_INITIAL, (xValue/10));
                        new Enviar(Constants.C_SETTINGS, Constants.I_POS_Y_INITIAL, (yValue/10));
                        break;
                    case 2:
                        Log.i(Constants.TAG, "Valores: "+ xValue + " - " + yValue);
                        new Enviar(Constants.C_SETTINGS, Constants.I_POS_X_FINAL, (xValue/10));
                        new Enviar(Constants.C_SETTINGS, Constants.I_POS_Y_FINAL, (yValue/10));
                        break;
                }

            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getApplication(), "O valor padrão para as coordenadas " +
                        "inicias será ( " + Constants.X + " , " + Constants.Y + " )", Toast.LENGTH_LONG).show();

                switch (option) {
                    case 1:
                        new Enviar(Constants.C_SETTINGS, Constants.I_POS_X_INITIAL, Constants.X);
                        new Enviar(Constants.C_SETTINGS, Constants.I_POS_Y_INITIAL, Constants.Y);
                        break;
                    case 2:
                        new Enviar(Constants.C_SETTINGS, Constants.I_POS_X_FINAL, Constants.X);
                        new Enviar(Constants.C_SETTINGS, Constants.I_POS_Y_FINAL, Constants.Y);
                        break;
                }
            }
        });

        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.show();
    }

    private void showControlDialog(){
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

                        disableAllButtons();
                        if (device != null) {
                            btSelectType.setEnabled(true);
                            sbVelocidade.setProgress(sbVelocidade.getMax() / 2);
                        }

                        new Enviar('u', 'q', Double.parseDouble("0")).start();
                        new Receber().start();

                    } else if (option == 2) {

                        enableAllButtons();
                        new Enviar('m', 'q', Double.parseDouble("0")).start();
                    }
                }
            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //SE A ESCOLHA FOR CANCEL.. A ESCOLHA PADRÃO SERÁ CONTROLE AUTOMATICO
                disableAllButtons();
            }
        });

        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.show();
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
                        if(adaptador.isEnabled()){
                            adaptador.disable();
                        }
                        disableAllButtons();
                        btSelectType.setEnabled(false);
                        device = null;
                        //ENVIO UMA MENSAGEM PARA O ROBOR PARA FECHAR A CONEXAO
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
                        if(adaptador.isEnabled()){
                            adaptador.disable();
                        }
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

    private void enableAllButtons(){
        btBackward.setEnabled(true);
        btForward.setEnabled(true);
        btLeft.setEnabled(true);
        btRight.setEnabled(true);
        sbVelocidade.setEnabled(true);
        if(device != null) btSelectType.setEnabled(true);
    }

    private void disableAllButtons(){
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

    private class Receber extends Thread {

        public Receber() {}

        public void run(){
            try {
                if(socket != null) {
                    input = new DataInputStream(socket.getInputStream());
                    if (socket.isConnected()) {
                        byte[] buffer = new byte[1024];
                        int bytes = 0;
                        while (true) {
                            bytes = input.read(buffer);
                            Log.i("TAG", "-----> bytes: "+bytes);
                            String readMessage = new String(buffer, 0, bytes);
                            if(readMessage.equalsIgnoreCase(Constants.FIM_TRANSFER)) break;
                            Log.i("TAG", "-----> readMessage: "+readMessage);
                            splitMessage(readMessage);
                            bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                        }
                    }
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) { }
        }
    }

    private class Enviar extends Thread {
        private Character comando;
        private Character identificador;
        private Double value;

        public Enviar(Character c, Character i, Double v){
            this.comando = c;
            this.identificador = i;
            this.value = v;
        }

        public void run(){
            try {
                if(socket != null){
                    output = new DataOutputStream(socket.getOutputStream());
                    if(socket.isConnected()){
                        if(comando != null) output.writeChar(comando);
                        if(identificador != null && value != null) {
                            output.writeChar(identificador);
                            output.writeDouble(value);
                        }
                        output.flush();
                    }
                }
            }catch (IOException erro){}
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) { }
        }
    }

    private void splitMessage(String readMessage){
        String[] splits = readMessage.split(",");
        Log.i("TAG", "splitMessage: "+splits.length);
        for(int i = 0; i < splits.length; i++){
            Log.i("TAG", "splitMessage: "+splits[i]);
        }
        try {
            Position position = new Position(
                    Double.parseDouble(splits[0]),
                    Double.parseDouble(splits[1]),
                    Double.parseDouble(splits[2]),
                    Double.parseDouble(splits[3]),
                    Double.parseDouble(splits[4]),
                    Double.parseDouble(splits[5]));
            listOfPositions.add(position);
        }catch (NumberFormatException ex){
            Log.e(Constants.TAG, ex.getMessage().toString());
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
        }else if(actionMenu.isOpened()){
            actionMenu.close(true);
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
            //showControlDialog();
            btSelectType.setEnabled(true);
            address = data.getExtras().getString("msg");

            device = adaptador.getRemoteDevice(address);
            teste = new ConnectThread(device);

            teste.start();
        }

        if(requestCode == REQUEST_ENABLE_BT){
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
                if(device != null) showDisableBluetoothDialog();
                break;

            case R.id.action_out:
                showFinishDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
