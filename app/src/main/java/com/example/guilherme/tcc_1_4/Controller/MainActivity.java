package com.example.guilherme.tcc_1_4.Controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
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
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.guilherme.tcc_1_4.Extra.SlidingTabLayout;
import com.example.guilherme.tcc_1_4.Model.Position;
import com.example.guilherme.tcc_1_4.R;
import com.example.guilherme.tcc_1_4.Utils.Constants;
import com.example.guilherme.tcc_1_4.Utils.Mask;
import com.example.guilherme.tcc_1_4.Utils.SingletonConnection;
import com.example.guilherme.tcc_1_4.Utils.SingletonInformation;
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Semaphore;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_ENABLE_BT = 1;

    private List<Position> listOfPositions;
    private final int handlerState = 0;
    private Handler bluetoothIn;
    private ProgressDialog ringProgressDialog;

    //VARIAVEIS APLICACAO
    private Button btConectar;
    private RadioGroup radioControlGroup;
    private Button btForward;
    private Button btBackward;
    private Button btRight;
    private Button btLeft;
    private SeekBar sbVelocidade;
    private byte velocidade = 1;
    private boolean pressedUp = false;
    private TextView edtXInicial;
    private TextView edtYInicial;
    private TextView edtXAlvo;
    private TextView edtYAlvo;
    private TextView edtControladorP;
    private TextView edtControladorI;

    private TextView tvPosicaoInicial;
    private TextView tvXPosInicial;
    private TextView tvYPosInicial;
    private TextView tvPosicaoAlvo;
    private TextView tvXPosAlvo;
    private TextView tvYPosAlvo;
    private TextView tvControllerP;
    private TextView tvControllerI;
    private TextView tvVelocidade;
    private TextView tvMinSeekVelocidade;
    private TextView tvMaxSeekVelocidade;
    private TextView tvTypeController;
    private TextView tvValueSeekVelocidade;
    private RadioButton radioButtonCA;
    private RadioButton radioButtonM;

    private BluetoothAdapter adaptador;
    private BluetoothDevice device;
    private BluetoothSocket socket;
    private DataOutputStream output;
    private DataInputStream input;
    private Intent it;
    private static final int TELA2 = 2;
    private int option = -1;
    private int optionControl = -1;

    private String address;
    private ConnectThread connectDeviceThread;
    private boolean isConnected;
    private Double controlPValue;
    private Double controlIValue;
    private Double xValue, yValue;

    public Semaphore sendSemaphore = new Semaphore(1);
    public Semaphore receiveSemaphore = new Semaphore(1);

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
        setContentView(R.layout.activity_main_optional);

        init(savedInstanceState);

        SingletonInformation.getInstance();
        isConnected = false;
        device = null;
        it = new Intent(this, SelectDevice.class);

        adaptador = BluetoothAdapter.getDefaultAdapter();
        if(!adaptador.isEnabled()){
            Intent enableBTIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBTIntent, 1);
        }

        initVariables();
    }

    //##############################################################################################################################

    private void initVariables(){
        tvTypeController = (TextView) findViewById(R.id.tvControle);
        tvPosicaoInicial = (TextView) findViewById(R.id.tvPosInicial);
        tvXPosInicial= (TextView) findViewById(R.id.tvXInicial);
        tvYPosInicial = (TextView) findViewById(R.id.tvYInicial);
        tvPosicaoAlvo = (TextView) findViewById(R.id.tvPosAlvo);
        tvXPosAlvo = (TextView) findViewById(R.id.tvXAlvo);
        tvYPosAlvo = (TextView) findViewById(R.id.tvYAlvo);
        tvControllerP = (TextView) findViewById(R.id.tvControladorP);
        tvControllerI = (TextView) findViewById(R.id.tvControladorI);
        tvVelocidade = (TextView) findViewById(R.id.tvVelocidade);
        tvMinSeekVelocidade = (TextView) findViewById(R.id.tvISeek);
        tvMaxSeekVelocidade = (TextView) findViewById(R.id.tvFSeek);
        tvValueSeekVelocidade = (TextView) findViewById(R.id.tvValueSeekBar);
        radioButtonCA = (RadioButton) findViewById(R.id.radioButtonAutomatico);
        radioButtonM = (RadioButton) findViewById(R.id.radioButtonManul);

        edtXInicial = (TextView) findViewById(R.id.tvXInicialValue);
        edtXInicial.setText(String.valueOf(SingletonInformation.getInstance().getxValueInitial()));

        edtYInicial = (TextView) findViewById(R.id.tvYInicialValue);
        edtYInicial.setText(String.valueOf(SingletonInformation.getInstance().getyValueInitial()));

        edtXAlvo = (TextView) findViewById(R.id.tvXAlvoValue);
        edtXAlvo.setText(String.valueOf(SingletonInformation.getInstance().getxValueAlvo()));

        edtYAlvo = (TextView) findViewById(R.id.tvYAlvoValue);
        edtYAlvo.setText(String.valueOf(SingletonInformation.getInstance().getyValueAlvo()));

        edtControladorP = (TextView) findViewById(R.id.tvControladorPValue);
        edtControladorP.setText(String.valueOf(SingletonInformation.getInstance().getControlPValue()));

        edtControladorI = (TextView) findViewById(R.id.tvControladorIValue);
        edtControladorI.setText(String.valueOf(SingletonInformation.getInstance().getControlIValue()));

        //botao para conectar
        btConectar = (Button) findViewById(R.id.btnConectar);
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

        radioControlGroup = (RadioGroup) findViewById(R.id.radioGroup);
        radioControlGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                switch (checkedId) {
                    case R.id.radioButtonAutomatico:
                        optionControl = 1;
                        if (device != null) {
                            disableManualControlComponents();
                            sbVelocidade.setProgress(sbVelocidade.getMax() / 2);
                            velocidade = (byte) sbVelocidade.getProgress();
                        }
                        new SendThread(Constants.C_AUTOMATIC_CONTROL, Constants.I_STOP, 0d).start();
                        new ReceiveThread().start();
                        break;

                    case R.id.radioButtonManul:
                        optionControl = 2;
                        enableAllComponents();
                        velocidade = (byte) sbVelocidade.getProgress();
                        new SendThread(Constants.C_MANUAL_CONTROL, Constants.I_STOP, Double.valueOf(velocidade)).start();
                        new ReceiveThread().start();
                        break;
                }
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
                                new SendThread(Constants.C_MANUAL_CONTROL, Constants.I_FWD, Double.valueOf(velocidade)).start();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            new SendThread(Constants.C_MANUAL_CONTROL, Constants.I_STOP, Double.valueOf(velocidade)).start();
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
                                new SendThread(Constants.C_MANUAL_CONTROL, Constants.I_BWD, Double.valueOf(velocidade)).start();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            new SendThread(Constants.C_MANUAL_CONTROL, Constants.I_STOP, Double.valueOf(velocidade)).start();
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
                                new SendThread(Constants.C_MANUAL_CONTROL, Constants.I_LEFT, Double.valueOf(velocidade)).start();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            new SendThread(Constants.C_MANUAL_CONTROL, Constants.I_STOP, Double.valueOf(velocidade)).start();
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
                                new SendThread(Constants.C_MANUAL_CONTROL, Constants.I_RIGTH, Double.valueOf(velocidade)).start();
                            }
                            break;
                        case MotionEvent.ACTION_UP:
                            new SendThread(Constants.C_MANUAL_CONTROL, Constants.I_STOP, Double.valueOf(velocidade)).start();
                            pressedUp = false;
                            break;
                    }
                }
                return true;
            }
        });

        sbVelocidade = (SeekBar) findViewById(R.id.sbVelocidade);
        sbVelocidade.setProgress(velocidade);
        sbVelocidade.setMinimumWidth(1);
        sbVelocidade.setMax(100);
        sbVelocidade.setProgress(sbVelocidade.getMax() / 2);
        sbVelocidade.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                velocidade = (byte) progress;
                tvValueSeekVelocidade.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                actionMenu.hideMenuButton(true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                actionMenu.showMenuButton(true);
            }
        });

        disableAllComponents();

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
                if (b) {
                    actionMenu.setIconAnimated(false);
                    actionMenu.getMenuIconView().setImageResource(R.drawable.close);
                } else {
                    actionMenu.setIconAnimated(false);
                    actionMenu.getMenuIconView().setImageResource(R.drawable.ic_pencil);
                }
            }
        });
        actionMenu.hideMenuButton(true);
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
        mToolbar.setLogo(R.drawable.ic_robo);
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
                        return false;
                    }
                })
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l, IDrawerItem iDrawerItem) {
                        for (int count = 0, tam = navigationDrawerLeft.getDrawerItems().size(); count < tam; count++) {
                            if (count == mPositionClicked && mPositionClicked != 1 && mPositionClicked <= 3) {
                                PrimaryDrawerItem aux = (PrimaryDrawerItem) navigationDrawerLeft.getDrawerItems().get(count);
                                aux.setIcon(getResources().getDrawable(getCorrectDrawerIcon(count, false)));
                                break;
                            }
                        }

                        if (position <= 3) {
                            ((PrimaryDrawerItem) iDrawerItem).setIcon(getResources().getDrawable(getCorrectDrawerIcon(position, true)));
                        }

                        if (position == 3) {
                            ((SwitchDrawerItem) iDrawerItem).setIcon(getResources().getDrawable(getCorrectDrawerIcon(position, true)));
                            headerNavigationLeft.setBackgroundRes(R.drawable.robo);
                            navigationDrawerLeft.getAdapter().notifyDataSetChanged();
                        }

                        mPositionClicked = position;
                        navigationDrawerLeft.setStatusBarColor(getResources().getColor(R.color.colorBackgroundNavigation));
                        navigationDrawerLeft.getAdapter().notifyDataSetChanged();

                        mItemDrawerSelected = position;

                        switch (mItemDrawerSelected) {
                            case 0:
                                headerNavigationLeft.setBackgroundRes(R.drawable.robo);
                                navigationDrawerLeft.getAdapter().notifyDataSetChanged();

                                if (device != null) {
                                    if (isConnected) {
                                        if (optionControl == 1 || optionControl == 2) {

                                            Intent i = new Intent(MainActivity.this, ProcessActivity.class);
                                            Bundle bundle = new Bundle();

                                            bundle.putParcelable("device", device);
                                            SingletonConnection.getInstance().setMoviments(listOfPositions);
                                            i.putExtras(bundle);
                                            startActivity(i);
                                            actionMenu.close(true);

                                        } else {
                                            Toast.makeText(MainActivity.this, "Nenhuma ação realizada para iniciar essa atividade.", Toast.LENGTH_LONG).show();
                                        }
                                    } else {
                                        Toast.makeText(MainActivity.this, "Conexão não foi estabelecida com sucesso.", Toast.LENGTH_LONG).show();
                                    }
                                } else {
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
        final EditText controllerPValue;
        final EditText controllerIValue;

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.custom_controller_dialog, null);
        dialogBuilder.setView(dialogView);

        dialogBuilder.setTitle("Ganho dos Controladores");
        dialogBuilder.setIcon(R.drawable.controller);

        controllerPValue = (EditText) dialogView.findViewById(R.id.edtControllerPValue);
        controllerPValue.addTextChangedListener(Mask.insert("#.###", controllerPValue));

        controllerIValue = (EditText) dialogView.findViewById(R.id.edtControllerIValue);
        controllerIValue.addTextChangedListener(Mask.insert("#.###", controllerIValue));

        dialogBuilder.setMessage("Digite, por favor, o novo valor desejado: ");

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String valorP = controllerPValue.getText().toString().trim();
                String valorI = controllerIValue.getText().toString().trim();

                if (!valorP.isEmpty() && !valorI.isEmpty()) {

                    controlPValue = Double.parseDouble(valorP);
                    controlIValue = Double.parseDouble(valorI);

                } else if (!valorP.isEmpty()) {

                    controlPValue = Double.parseDouble(valorP);
                    controlIValue = Constants.KI_INITIAL;

                } else if (!valorI.isEmpty()) {

                    controlPValue = Constants.KP_INITIAL;
                    controlIValue = Double.parseDouble(valorI);

                } else {

                    controlPValue = Constants.KP_INITIAL;
                    controlIValue = Constants.KI_INITIAL;
                }

                SingletonInformation.getInstance().setControlIValue(controlIValue);
                SingletonInformation.getInstance().setControlPValue(controlPValue);

                edtControladorP.setText(String.valueOf(SingletonInformation.getInstance().getControlPValue()));
                edtControladorI.setText(String.valueOf(SingletonInformation.getInstance().getControlIValue()));

                new SendInformationsThread(Constants.C_SETTINGS, Constants.I_CONTROLLER, controlPValue, controlIValue).start();
            }

        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //DO NOTHING
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
        edtPosX.addTextChangedListener(Mask.insert("###.##", edtPosX));
        edtPosX.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);

        edtPosY = (EditText) dialogView.findViewById(R.id.coordY);
        edtPosY.addTextChangedListener(Mask.insert("###.##", edtPosY));
        edtPosY.setInputType(InputType.TYPE_NUMBER_FLAG_SIGNED | InputType.TYPE_CLASS_NUMBER);

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String x = edtPosX.getText().toString().trim();
                String y = edtPosY.getText().toString().trim();

                if (!x.isEmpty() && !y.isEmpty()) {
                    xValue = Double.valueOf(x);
                    yValue = Double.valueOf(y);
                } else if (!x.isEmpty()) {
                    xValue = Double.valueOf(x);
                    yValue = Constants.Y;
                } else if (!y.isEmpty()) {
                    xValue = Constants.X;
                    yValue = Double.valueOf(y);
                } else {
                    xValue = Constants.X;
                    yValue = Constants.Y;
                }

                switch (option) {
                    case 1:
                        SingletonInformation.getInstance().setxValueInitial((xValue / 100));
                        SingletonInformation.getInstance().setyValueInitial((yValue / 100));

                        edtXInicial.setText(String.valueOf((SingletonInformation.getInstance().getxValueInitial())));
                        edtYInicial.setText(String.valueOf((SingletonInformation.getInstance().getyValueInitial())));

                        new SendInformationsThread(Constants.C_SETTINGS, Constants.I_POS_INITIAL, (xValue / 100), (yValue / 100)).start();
                        break;
                    case 2:
                        SingletonInformation.getInstance().setxValueAlvo((xValue / 100));
                        SingletonInformation.getInstance().setyValueAlvo((yValue / 100));

                        edtXAlvo.setText(String.valueOf((SingletonInformation.getInstance().getxValueAlvo())));
                        edtYAlvo.setText(String.valueOf((SingletonInformation.getInstance().getyValueAlvo())));

                        new SendInformationsThread(Constants.C_SETTINGS, Constants.I_POS_FINAL, (xValue / 100), (yValue / 100)).start();
                        break;
                }

            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //DO NOTHING
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
                        isConnected = false;
                        finalizeConnection();
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
                        isConnected = false;
                        finalizeConnection();
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

    private void finalizeConnection(){
        disableAllComponents();

        if(radioButtonCA.isChecked()){
            radioButtonCA.setChecked(false);
            radioButtonM.setChecked(false);
        }

        if (adaptador.isEnabled()) {
            adaptador.disable();
        }

        SingletonInformation.getInstance().setxValueInitial(Constants.X);
        SingletonInformation.getInstance().setyValueInitial(Constants.Y);
        SingletonInformation.getInstance().setxValueAlvo(Constants.X);
        SingletonInformation.getInstance().setyValueAlvo(Constants.Y);
        SingletonInformation.getInstance().setControlIValue(Constants.KI_INITIAL);
        SingletonInformation.getInstance().setControlPValue(Constants.KP_INITIAL);

        edtXInicial.setText(String.valueOf(SingletonInformation.getInstance().getxValueInitial()));
        edtYInicial.setText(String.valueOf(SingletonInformation.getInstance().getyValueInitial()));
        edtXAlvo.setText(String.valueOf(SingletonInformation.getInstance().getxValueAlvo()));
        edtYAlvo.setText(String.valueOf(SingletonInformation.getInstance().getyValueAlvo()));
        edtControladorP.setText(String.valueOf(SingletonInformation.getInstance().getControlPValue()));
        edtControladorI.setText(String.valueOf(SingletonInformation.getInstance().getControlIValue()));

        SingletonConnection.getInstance().clearMovimentsList();

        try {
            if(socket != null) socket.close();
            device = null;
            address = null;
            input = null;
            output = null;
            socket = null;

            SingletonConnection.getInstance().setDevice(null);
            SingletonConnection.getInstance().setInput(null);
            SingletonConnection.getInstance().setOutput(null);
            SingletonConnection.getInstance().setSocket(null);

        } catch(IOException e) {
            Log.e(Constants.TAG, e.getMessage().toString());
        }

        actionMenu.hideMenuButton(true);
    }

    private void enableAllComponents(){
        tvControllerP.setTextColor(getResources().getColor(R.color.black));
        tvMaxSeekVelocidade.setTextColor(getResources().getColor(R.color.black));
        tvMinSeekVelocidade.setTextColor(getResources().getColor(R.color.black));
        tvValueSeekVelocidade.setTextColor(getResources().getColor(R.color.black));
        tvPosicaoAlvo.setTextColor(getResources().getColor(R.color.black));
        tvPosicaoInicial.setTextColor(getResources().getColor(R.color.black));
        tvVelocidade.setTextColor(getResources().getColor(R.color.black));
        tvXPosInicial.setTextColor(getResources().getColor(R.color.black));
        tvYPosInicial.setTextColor(getResources().getColor(R.color.black));
        tvXPosAlvo.setTextColor(getResources().getColor(R.color.black));
        tvYPosAlvo.setTextColor(getResources().getColor(R.color.black));
        tvTypeController.setTextColor(getResources().getColor(R.color.black));

        for (int i = 0; i < radioControlGroup.getChildCount(); i++) {
            radioControlGroup.getChildAt(i).setEnabled(true);
        }
        radioButtonCA.setTextColor(getResources().getColor(R.color.black));
        radioButtonM.setTextColor(getResources().getColor(R.color.black));

        edtControladorP.setTextColor(getResources().getColor(R.color.black));
        edtXInicial.setTextColor(getResources().getColor(R.color.black));
        edtYInicial.setTextColor(getResources().getColor(R.color.black));
        edtXAlvo.setTextColor(getResources().getColor(R.color.black));
        edtYAlvo.setTextColor(getResources().getColor(R.color.black));

        btBackward.setEnabled(true);
        btBackward.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        btForward.setEnabled(true);
        btForward.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        btLeft.setEnabled(true);
        btLeft.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        btRight.setEnabled(true);
        btRight.setBackgroundColor(getResources().getColor(R.color.colorPrimary));

        sbVelocidade.setEnabled(true);
    }

    private void disableAllComponents(){
        tvControllerP.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvControllerI.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvMaxSeekVelocidade.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvMinSeekVelocidade.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvValueSeekVelocidade.setTextColor(getResources().getColor(R.color.grey));
        tvPosicaoAlvo.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvPosicaoInicial.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvVelocidade.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvXPosInicial.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvYPosInicial.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvXPosAlvo.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvYPosAlvo.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvTypeController.setTextColor(getResources().getColor(R.color.colorDisableText));

        for (int i = 0; i < radioControlGroup.getChildCount(); i++) {
            radioControlGroup.getChildAt(i).setEnabled(false);
        }
        radioButtonCA.setTextColor(getResources().getColor(R.color.colorDisableText));
        radioButtonCA.setChecked(false);

        radioButtonM.setTextColor(getResources().getColor(R.color.colorDisableText));
        radioButtonM.setChecked(false);

        edtControladorP.setTextColor(getResources().getColor(R.color.colorDisableText));
        edtControladorI.setTextColor(getResources().getColor(R.color.colorDisableText));
        edtXInicial.setTextColor(getResources().getColor(R.color.colorDisableText));
        edtYInicial.setTextColor(getResources().getColor(R.color.colorDisableText));
        edtXAlvo.setTextColor(getResources().getColor(R.color.colorDisableText));
        edtYAlvo.setTextColor(getResources().getColor(R.color.colorDisableText));

        btBackward.setEnabled(false);
        btBackward.setBackgroundColor(getResources().getColor(R.color.colorDisableText));

        btForward.setEnabled(false);
        btForward.setBackgroundColor(getResources().getColor(R.color.colorDisableText));

        btLeft.setEnabled(false);
        btLeft.setBackgroundColor(getResources().getColor(R.color.colorDisableText));

        btRight.setEnabled(false);
        btRight.setBackgroundColor(getResources().getColor(R.color.colorDisableText));

        sbVelocidade.setProgress(sbVelocidade.getMax()/2);
        sbVelocidade.setEnabled(false);
    }

    private void enableChoiceControl(){
        tvControllerP.setTextColor(getResources().getColor(R.color.black));
        tvControllerI.setTextColor(getResources().getColor(R.color.black));
        tvPosicaoAlvo.setTextColor(getResources().getColor(R.color.black));
        tvPosicaoInicial.setTextColor(getResources().getColor(R.color.black));
        tvXPosInicial.setTextColor(getResources().getColor(R.color.black));
        tvYPosInicial.setTextColor(getResources().getColor(R.color.black));
        tvXPosAlvo.setTextColor(getResources().getColor(R.color.black));
        tvYPosAlvo.setTextColor(getResources().getColor(R.color.black));

        edtControladorP.setTextColor(getResources().getColor(R.color.black));
        edtControladorI.setTextColor(getResources().getColor(R.color.black));
        edtXInicial.setTextColor(getResources().getColor(R.color.black));
        edtYInicial.setTextColor(getResources().getColor(R.color.black));
        edtXAlvo.setTextColor(getResources().getColor(R.color.black));
        edtYAlvo.setTextColor(getResources().getColor(R.color.black));

        tvTypeController.setTextColor(getResources().getColor(R.color.black));

        for (int i = 0; i < radioControlGroup.getChildCount(); i++) {
            radioControlGroup.getChildAt(i).setEnabled(true);
        }
        radioButtonCA.setTextColor(getResources().getColor(R.color.black));
        radioButtonM.setTextColor(getResources().getColor(R.color.black));
    }

    private void disableManualControlComponents(){
        tvMaxSeekVelocidade.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvMinSeekVelocidade.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvVelocidade.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvValueSeekVelocidade.setTextColor(getResources().getColor(R.color.grey));

        btBackward.setEnabled(false);
        btBackward.setBackgroundColor(getResources().getColor(R.color.colorDisableText));

        btForward.setEnabled(false);
        btForward.setBackgroundColor(getResources().getColor(R.color.colorDisableText));

        btLeft.setEnabled(false);
        btLeft.setBackgroundColor(getResources().getColor(R.color.colorDisableText));

        btRight.setEnabled(false);
        btRight.setBackgroundColor(getResources().getColor(R.color.colorDisableText));

        sbVelocidade.setEnabled(false);
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
                SingletonConnection.getInstance().setSocket(socket);

                new ReceiveThread().start();

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

    private class ReceiveThread extends Thread {

        public ReceiveThread() {}

        public void run(){
            try {
                if(socket != null) {
                    input = new DataInputStream(socket.getInputStream());
                    SingletonConnection.getInstance().setInput(input);
                    receiveSemaphore.acquire();

                    if (socket.isConnected()) {
                        byte[] buffer = new byte[1024];
                        int bytes = 0;
                        while (true) {
                            try {
                                bytes = input.read(buffer);
                            }catch (IOException e){
                                break;
                            }
                            String readMessage = new String(buffer, 0, bytes);
                            if(readMessage.contains(Constants.FIM_TRANSFER)) break;
                            splitMessage(readMessage);
                            bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                        }
                    }

                    receiveSemaphore.release();
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) { }
        }
    }

    private class SendInformationsThread extends Thread {
        private Character comando;
        private Character identificador;
        private Double firstValue;
        private Double secondValue;

        public SendInformationsThread(Character c, Character i, Double v1, Double v2) {
            this.comando = c;
            this.identificador = i;
            this.firstValue = v1;
            this.secondValue = v2;
        }

        public void run(){
            try {
                if(socket != null){
                    output = new DataOutputStream(socket.getOutputStream());
                    SingletonConnection.getInstance().setOutput(output);
                    sendSemaphore.acquire();

                    if(socket.isConnected()){
                        if(comando != null) output.writeChar(comando);
                        if(identificador != null && firstValue != null && secondValue != null) {
                            output.writeChar(identificador);
                            output.writeDouble(firstValue);
                            output.writeDouble(secondValue);
                        }
                        output.flush();
                    }

                    sendSemaphore.release();
                }
            } catch (Exception e) {
                Log.e(Constants.TAG, "ENVIAR ERRO: "+e.getMessage());
            }
        }
    }

    private class SendThread extends Thread {
        private Character comando;
        private Character identificador;
        private Double value;

        public SendThread(Character c, Character i, Double v){
            this.comando = c;
            this.identificador = i;
            this.value = v;
        }

        public void run(){
            try {
                if(socket != null){
                    output = new DataOutputStream(socket.getOutputStream());
                    SingletonConnection.getInstance().setOutput(output);
                    sendSemaphore.acquire();

                    if(socket.isConnected()){
                        if(comando != null) output.writeChar(comando);
                        if(identificador != null && value != null) {
                            output.writeChar(identificador);
                            output.writeDouble(value);
                        }
                        output.flush();
                    }

                    sendSemaphore.release();
                }
            }catch (Exception erro){}
        }

        public void cancel() {
            try {
                socket.close();
            } catch (IOException e) { }
        }
    }

    private void launchRingDialog() {
        ringProgressDialog = ProgressDialog.show(MainActivity.this,"","Conectando-se ao dispositivo...", true);
        ringProgressDialog.setCancelable(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!isConnected){

                }
            }
        }).start();
    }

    private void splitMessage(String readMessage){
        String[] splits = readMessage.split(",");

        if(readMessage.contains(Constants.CONNECTED)) {
            isConnected = true;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ringProgressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Conexão estabelecida com sucesso!\nEsolha o controle desejado.", Toast.LENGTH_LONG).show();
                    enableChoiceControl();
                    actionMenu.showMenuButton(true);
                }
            });
        }else {
            try {
                Position position = new Position(
                        Double.parseDouble(splits[0]),
                        Double.parseDouble(splits[1]),
                        Double.parseDouble(splits[2]),
                        Double.parseDouble(splits[3]),
                        Double.parseDouble(splits[4]),
                        Double.parseDouble(splits[5]),
                        Float.parseFloat(splits[6]),
                        Integer.parseInt(splits[7]));
                listOfPositions.add(position);

                Intent intent = new Intent("data-event");
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("moviments", (ArrayList<? extends Parcelable>) listOfPositions);
                intent.putExtras(bundle);
                LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            }catch (NumberFormatException ex){
                Log.e(Constants.TAG, ex.getMessage().toString());
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

        edtXInicial.setText(String.valueOf(SingletonInformation.getInstance().getxValueInitial()));
        edtYInicial.setText(String.valueOf(SingletonInformation.getInstance().getyValueInitial()));
        edtXAlvo.setText(String.valueOf(SingletonInformation.getInstance().getxValueAlvo()));
        edtYAlvo.setText(String.valueOf(SingletonInformation.getInstance().getyValueAlvo()));
        edtControladorP.setText(String.valueOf(SingletonInformation.getInstance().getControlPValue()));
        edtControladorI.setText(String.valueOf(SingletonInformation.getInstance().getControlIValue()));
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

            address = data.getExtras().getString("msg");
            device = adaptador.getRemoteDevice(address);
            SingletonConnection.getInstance().setDevice(device);

            launchRingDialog();
            connectDeviceThread = new ConnectThread(device);
            connectDeviceThread.start();
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
                showDisableBluetoothDialog();
                break;

            case R.id.action_out:
                showFinishDialog();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
