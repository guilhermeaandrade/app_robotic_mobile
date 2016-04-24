package com.example.guilherme.tcc_1_4.Fragments;


import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.guilherme.tcc_1_4.Model.Position;
import com.example.guilherme.tcc_1_4.R;
import com.example.guilherme.tcc_1_4.Utils.Constants;
import com.example.guilherme.tcc_1_4.Utils.Mask;
import com.example.guilherme.tcc_1_4.Utils.SharedPreference;
import com.example.guilherme.tcc_1_4.Utils.Singleton;
import com.example.guilherme.tcc_1_4.Utils.SingletonInformation;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.w3c.dom.Text;

import java.io.DataOutputStream;
import java.util.List;
import java.util.concurrent.Semaphore;

public class InformationFragment extends Fragment {

    private BluetoothDevice mDevice;
    private static List<Position> moviments;
    private FloatingActionMenu actionMenu;

    private TextView tvName;
    private TextView tvAddress;

    private TextView tvLastCoordX;
    private TextView tvLastCoordY;
    private TextView tvLastTheta;
    private TextView tvLastVelV;
    private TextView tvLastVelW;
    private TextView tvError;
    private TextView tvCoordX;
    private TextView tvCoordY;
    private TextView tvAngulo;
    private TextView tvV;
    private TextView tvW;
    private TextView tvE;

    private Double controlPValue;
    private Double controlIValue;
    private Double xValue, yValue;

    public Semaphore sendSemaphore = new Semaphore(1);

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(!bundle.isEmpty()){
                moviments = bundle.getParcelableArrayList("moviments");
            }
            Singleton.getInstance().setMoviments(moviments);
            fillAllVariables();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(Constants.TAG, "INFORMATIONFRAGMENT -> onCreate()");
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.information_fragment_layout, container, false);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("data-event"));

        mDevice = this.getArguments().getParcelable("device");

        Log.i(Constants.TAG, "Device: "+Singleton.getInstance().getDevice().getName()+"-"+Singleton.getInstance().getDevice().getAddress());
        Log.i(Constants.TAG, "Socket: "+Singleton.getInstance().getSocket().getRemoteDevice().getAddress());
        Log.i(Constants.TAG, "DataOutputStream: "+Singleton.getInstance().getOutput().toString());
        Log.i(Constants.TAG, "DataInputStream: "+Singleton.getInstance().getInput().toString());

        init(view);

        return view;
    }

    private void init(View view){
        //##############################################################################################################
        tvName = (TextView) view.findViewById(R.id.name_robo);
        tvAddress = (TextView) view.findViewById(R.id.end_mac_robo);

        //##############################################################################################################
        tvLastCoordX = (TextView) view.findViewById(R.id.edtCoordX);
        tvLastCoordY = (TextView) view.findViewById(R.id.edtCoordY);
        tvLastTheta = (TextView) view.findViewById(R.id.edtAngTheta);
        tvLastVelV = (TextView) view.findViewById(R.id.edtVelV);
        tvLastVelW = (TextView) view.findViewById(R.id.edtVelW);
        tvError = (TextView) view.findViewById(R.id.edtError);

        tvCoordX = (TextView) view.findViewById(R.id.coordX);
        tvCoordY = (TextView) view.findViewById(R.id.coordY);
        tvAngulo = (TextView) view.findViewById(R.id.theta);
        tvV = (TextView) view.findViewById(R.id.velV);
        tvW = (TextView) view.findViewById(R.id.velW);
        tvE = (TextView) view.findViewById(R.id.erroPosicao);

        //##############################################################################################################
        actionMenu = (FloatingActionMenu) view.findViewById(R.id.actionMenu);
        actionMenu.setIconAnimated(false);
        actionMenu.getMenuIconView().setImageResource(R.drawable.ic_pencil);
        actionMenu.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean b) {
                if (b) {
                    actionMenu.setIconAnimated(false);
                    actionMenu.getMenuIconView().setImageResource(R.drawable.close);
                    disableAllComponents();
                } else {
                    actionMenu.setIconAnimated(false);
                    actionMenu.getMenuIconView().setImageResource(R.drawable.ic_pencil);
                    enableAllComponents();
                }
            }
        });
        actionMenu.showMenuButton(true);
        actionMenu.setClosedOnTouchOutside(true);

        FloatingActionButton fab1 = (FloatingActionButton) view.findViewById(R.id.fabController);
        fab1.setColorNormal(getResources().getColor(R.color.colorPrimary));
        fab1.setColorPressed(getResources().getColor(R.color.colorPrimaryPressed));
        fab1.setOnClickListener(new FloatingActionButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeControllerDialog();
            }
        });

        FloatingActionButton fab2 = (FloatingActionButton) view.findViewById(R.id.fabPosition);
        fab2.setColorNormal(getResources().getColor(R.color.colorPrimary));
        fab2.setColorPressed(getResources().getColor(R.color.colorPrimaryPressed));
        fab2.setOnClickListener(new FloatingActionButton.OnClickListener(){
            @Override
            public void onClick(View v){
                showChangePositionDialog(1);
            }
        });

        FloatingActionButton fab3 = (FloatingActionButton) view.findViewById(R.id.fabPositionAlvo);
        fab3.setColorNormal(getResources().getColor(R.color.colorPrimary));
        fab3.setColorPressed(getResources().getColor(R.color.colorPrimaryPressed));
        fab3.setOnClickListener(new FloatingActionButton.OnClickListener(){
            @Override
            public void onClick(View v){
                showChangePositionDialog(2);
            }
        });
    }

    private void fillAllVariables(){
        //##############################################################################################################
        tvName.setText(mDevice.getName());
        tvAddress.setText(mDevice.getAddress());

        if(Singleton.getInstance().getMoviments().size() > 0){
            tvLastCoordX.setText(String.valueOf(round(Singleton.getInstance().getMoviments().get(Singleton.getInstance().getMoviments().size() - 1).getX())));
            tvLastCoordY.setText(String.valueOf(round(Singleton.getInstance().getMoviments().get(Singleton.getInstance().getMoviments().size() - 1).getY())));
            tvLastTheta.setText(String.valueOf(round(Singleton.getInstance().getMoviments().get(Singleton.getInstance().getMoviments().size() - 1).getTheta())));
            tvLastVelV.setText(String.valueOf(round(Singleton.getInstance().getMoviments().get(Singleton.getInstance().getMoviments().size() - 1).getV())));
            tvLastVelW.setText(String.valueOf(round(Singleton.getInstance().getMoviments().get(Singleton.getInstance().getMoviments().size() - 1).getW())));
            tvError.setText(String.valueOf(round(Singleton.getInstance().getMoviments().get(Singleton.getInstance().getMoviments().size() - 1).getE())));

        }else {
            if(moviments != null && moviments.size() > 0){
                tvLastCoordX.setText(String.valueOf(round(moviments.get(moviments.size() - 1).getX())));
                tvLastCoordY.setText(String.valueOf(round(moviments.get(moviments.size() - 1).getY())));
                tvLastTheta.setText(String.valueOf(round(moviments.get(moviments.size() - 1).getTheta())));
                tvLastVelV.setText(String.valueOf(round(moviments.get(moviments.size() - 1).getV())));
                tvLastVelW.setText(String.valueOf(round(moviments.get(moviments.size() - 1).getW())));
                tvError.setText(String.valueOf(round(moviments.get(moviments.size() - 1).getE())));
            }else {
                tvLastCoordX.setText("0");
                tvLastCoordY.setText("0");
                tvLastTheta.setText("0");
                tvLastVelV.setText("0");
                tvLastVelW.setText("0");
                tvError.setText("0");
            }

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
                if(Singleton.getInstance().getSocket() != null){
                    sendSemaphore.acquire();

                    if(Singleton.getInstance().getSocket().isConnected()){
                        if(comando != null) Singleton.getInstance().getOutput().writeChar(comando);
                        if(identificador != null && firstValue != null && secondValue != null) {
                            Singleton.getInstance().getOutput().writeChar(identificador);
                            Singleton.getInstance().getOutput().writeDouble(firstValue);
                            Singleton.getInstance().getOutput().writeDouble(secondValue);
                        }
                        Singleton.getInstance().getOutput().flush();
                    }

                    sendSemaphore.release();
                }
            } catch (Exception e) {
                Log.e(Constants.TAG, "ENVIAR ERRO: "+e.getMessage());
            }
        }
    }

    private void showChangeControllerDialog(){
        final EditText controllerPValue;
        final EditText controllerIValue;

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
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

                new SendInformationsThread(Constants.C_SETTINGS, Constants.I_CONTROLLER, controlPValue, controlIValue).start();
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

    private void showChangePositionDialog(final int option) {
        final EditText edtPosX;
        final EditText edtPosY;

        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
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

                        new SendInformationsThread(Constants.C_SETTINGS, Constants.I_POS_INITIAL, (xValue / 100), (yValue / 100)).start();
                        break;
                    case 2:
                        SingletonInformation.getInstance().setxValueAlvo((xValue / 100));
                        SingletonInformation.getInstance().setyValueAlvo((yValue / 100));

                        new SendInformationsThread(Constants.C_SETTINGS, Constants.I_POS_FINAL, (xValue / 100), (yValue / 100)).start();
                        break;
                }

            }
        }).setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (option) {
                    case 1:
                        new SendInformationsThread(Constants.C_SETTINGS, Constants.I_POS_INITIAL, (Constants.X/100), (Constants.Y/100)).start();
                        break;
                    case 2:
                        new SendInformationsThread(Constants.C_SETTINGS, Constants.I_POS_FINAL, (Constants.X/100), (Constants.Y/100)).start();
                        break;
                }
            }
        });

        final AlertDialog b = dialogBuilder.create();
        b.setCancelable(false);
        b.show();
    }

    private void enableAllComponents(){
        tvLastCoordX.setTextColor(getResources().getColor(R.color.black));
        tvLastCoordY.setTextColor(getResources().getColor(R.color.black));
        tvLastTheta.setTextColor(getResources().getColor(R.color.black));
        tvLastVelV.setTextColor(getResources().getColor(R.color.black));
        tvLastVelW.setTextColor(getResources().getColor(R.color.black));
        tvError.setTextColor(getResources().getColor(R.color.black));

        tvCoordX.setTextColor(getResources().getColor(R.color.black));
        tvCoordY.setTextColor(getResources().getColor(R.color.black));
        tvAngulo.setTextColor(getResources().getColor(R.color.black));
        tvV.setTextColor(getResources().getColor(R.color.black));
        tvW.setTextColor(getResources().getColor(R.color.black));
        tvE.setTextColor(getResources().getColor(R.color.black));
    }

    private void disableAllComponents(){
        tvLastCoordX.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvLastCoordY.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvLastTheta.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvLastVelV.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvLastVelW.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvError.setTextColor(getResources().getColor(R.color.colorDisableText));

        tvCoordX.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvCoordY.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvAngulo.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvV.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvW.setTextColor(getResources().getColor(R.color.colorDisableText));
        tvE.setTextColor(getResources().getColor(R.color.colorDisableText));
    }

    private Double round(Double value){
        long factor = (long) Math.pow(10, Constants.NUMBER_DECIMAIS);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public void launchRingDialog() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ...", "Gerando as informações necessárias", true);
        ringProgressDialog.setCancelable(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (Exception e) {}
                ringProgressDialog.dismiss();
            }
        }).start();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        fillAllVariables();
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
        super.onDestroy();
    }
}
