package com.example.guilherme.tcc_1_4.Fragments;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.widget.TextView;

import com.example.guilherme.tcc_1_4.Model.Position;
import com.example.guilherme.tcc_1_4.R;
import com.example.guilherme.tcc_1_4.Utils.Constants;
import com.example.guilherme.tcc_1_4.Utils.SharedPreference;
import com.example.guilherme.tcc_1_4.Utils.Singleton;

import java.util.List;

public class InformationFragment extends Fragment {

    private BluetoothDevice mDevice;
    private static List<Position> moviments;

    private TextView tvName;
    private TextView tvAddress;

    private TextView tvLastCoordX;
    private TextView tvLastCoordY;
    private TextView tvLastTheta;
    private TextView tvLastVelV;
    private TextView tvLastVelW;
    private TextView tvError;
    private TextView tvErrorText;
    private TextView tvVelWText;

    private static SharedPreference sharedP;

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
        tvErrorText = (TextView) view.findViewById(R.id.erroPosicao);
        tvVelWText = (TextView) view.findViewById(R.id.velW);
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
