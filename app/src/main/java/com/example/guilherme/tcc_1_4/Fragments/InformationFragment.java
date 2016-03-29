package com.example.guilherme.tcc_1_4.Fragments;


import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guilherme.tcc_1_4.Model.Position;
import com.example.guilherme.tcc_1_4.R;

import org.w3c.dom.Text;

import java.util.List;

public class InformationFragment extends Fragment {

    private BluetoothDevice mDevice;
    private List<Position> moviments;

    private TextView tvName;
    private TextView tvAddress;

    private TextView tvLastCoordX;
    private TextView tvLastCoordY;
    private TextView tvLastTheta;
    private TextView tvLastVelV;
    private TextView tvLastVelW;

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
        moviments = this.getArguments().getParcelableArrayList("moviments");

        //##############################################################################################################
        tvName = (TextView) view.findViewById(R.id.name_robo);
        tvName.setText(mDevice.getName());

        tvAddress = (TextView) view.findViewById(R.id.end_mac_robo);
        tvAddress.setText(mDevice.getAddress());

        //##############################################################################################################
        tvLastCoordX = (TextView) view.findViewById(R.id.edtCoordX);
        tvLastCoordX.setText(String.valueOf(moviments.get(moviments.size() - 1).getX()));

        tvLastCoordY = (TextView) view.findViewById(R.id.edtCoordY);
        tvLastCoordY.setText(String.valueOf(moviments.get(moviments.size() - 1).getY()));

        tvLastTheta = (TextView) view.findViewById(R.id.edtAngTheta);
        tvLastTheta.setText(String.valueOf(moviments.get(moviments.size() - 1).getTheta()));

        tvLastVelV = (TextView) view.findViewById(R.id.edtVelV);
        tvLastVelV.setText(String.valueOf(moviments.get(moviments.size() - 1).getV()));

        tvLastVelW = (TextView) view.findViewById(R.id.edtVelW);
        tvLastVelW.setText(String.valueOf(moviments.get(moviments.size() - 1).getW()));

        return view;
    }
}
