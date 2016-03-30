package com.example.guilherme.tcc_1_4.Fragments;


import android.bluetooth.BluetoothDevice;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.guilherme.tcc_1_4.Model.Position;
import com.example.guilherme.tcc_1_4.R;

import java.util.List;

public class GraphFragment extends Fragment{

    private BluetoothDevice mDevice;
    private List<Position> moviments;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graph_fragment_layout, container, false);

        mDevice = this.getArguments().getParcelable("device");
        moviments = this.getArguments().getParcelableArrayList("moviments");

        return view;
    }
}
