package com.example.guilherme.tcc_1_4.Fragments;


import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guilherme.tcc_1_4.Model.ManualPosition;
import com.example.guilherme.tcc_1_4.Model.Position;
import com.example.guilherme.tcc_1_4.R;
import com.example.guilherme.tcc_1_4.Utils.Constants;

import java.text.DecimalFormat;
import java.util.List;

public class InformationFragment extends Fragment {

    private BluetoothDevice mDevice;
    private List<Position> automaticMoviments;
    private List<ManualPosition> manualMoviments;
    private int optionControl;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.information_fragment_layout, container, false);

        mDevice = this.getArguments().getParcelable("device");
        automaticMoviments = this.getArguments().getParcelableArrayList("automaticMoviments");
        manualMoviments = this.getArguments().getParcelableArrayList("manualMoviments");
        optionControl = this.getArguments().getInt("optionControl");

        Log.i("TAG", "MOVIMENTS -> INFORMATION -> length: " + automaticMoviments.size());
        Log.i(Constants.TAG, "MOVIMENTS -> INFORMATION -> length: " + manualMoviments.size());

        init(view);
        fillAllVariables();
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

        //##############################################################################################################
        if(optionControl == 1){
            if(automaticMoviments.size() > 0){
                tvLastCoordX.setText(String.valueOf(round(automaticMoviments.get(automaticMoviments.size() - 1).getX())));
                tvLastCoordY.setText(String.valueOf(round(automaticMoviments.get(automaticMoviments.size() - 1).getY())));
                tvLastTheta.setText(String.valueOf(round(automaticMoviments.get(automaticMoviments.size() - 1).getTheta())));
                tvLastVelV.setText(String.valueOf(round(automaticMoviments.get(automaticMoviments.size() - 1).getV())));
                tvLastVelW.setText(String.valueOf(round(automaticMoviments.get(automaticMoviments.size() - 1).getW())));
                tvError.setText(String.valueOf(round(automaticMoviments.get(automaticMoviments.size() - 1).getE())));
            }else {
                tvLastCoordX.setText("0");
                tvLastCoordY.setText("0");
                tvLastTheta.setText("0");
                tvLastVelV.setText("0");
                tvLastVelW.setText("0");
                tvError.setText("0");
            }
        }
        if(optionControl == 2){
            tvLastVelW.setTextColor(Color.WHITE);
            tvError.setTextColor(Color.WHITE);
            tvErrorText.setTextColor(Color.WHITE);
            tvVelWText.setTextColor(Color.WHITE);

            if(manualMoviments.size() > 0){
                tvLastCoordX.setText(String.valueOf(round(manualMoviments.get(manualMoviments.size() - 1).getX())));
                tvLastCoordY.setText(String.valueOf(round(manualMoviments.get(manualMoviments.size() - 1).getY())));
                tvLastTheta.setText(String.valueOf(round(manualMoviments.get(manualMoviments.size() - 1).getTheta())));
                tvLastVelV.setText(String.valueOf(round(manualMoviments.get(manualMoviments.size() - 1).getV())));
            }else {
                tvLastCoordX.setText("0");
                tvLastCoordY.setText("0");
                tvLastTheta.setText("0");
                tvLastVelV.setText("0");
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
                    //finish();
                } catch (Exception e) {}
                ringProgressDialog.dismiss();
            }
        }).start();
    }
}
