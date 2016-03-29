package com.example.guilherme.tcc_1_4.Fragments;


import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.guilherme.tcc_1_4.R;

public class InformationFragment extends Fragment {

    private BluetoothDevice mDevice;
    private TextView tvName;
    private TextView tvAddress;

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

        tvName = (TextView) view.findViewById(R.id.name_robo);
        tvName.setText(mDevice.getName());

        tvAddress = (TextView) view.findViewById(R.id.end_mac_robo);
        tvAddress.setText(mDevice.getAddress());

        return view;
    }
}
