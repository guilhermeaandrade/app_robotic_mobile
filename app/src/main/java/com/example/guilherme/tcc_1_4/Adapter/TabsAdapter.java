package com.example.guilherme.tcc_1_4.Adapter;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.example.guilherme.tcc_1_4.Fragments.GraphFragment;
import com.example.guilherme.tcc_1_4.Fragments.InformationFragment;
import com.example.guilherme.tcc_1_4.Model.Position;
import com.example.guilherme.tcc_1_4.Utils.Constants;
import com.example.guilherme.tcc_1_4.Utils.SharedPreference;

import java.util.ArrayList;
import java.util.List;

public class TabsAdapter extends FragmentPagerAdapter {

    private String[] titles = new String[]{"INFORMAÇÕES", "GRÁFICOS"};
    private Context mContext;
    private BluetoothDevice mDevice;
    //private static List<Position> moviments;

    /*private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(!bundle.isEmpty()){
                moviments = bundle.getParcelableArrayList("moviments");
            }
        }
    };*/

    public TabsAdapter(FragmentManager fragmentManager, Context context, BluetoothDevice device){
        super(fragmentManager);
        //moviments = pos;
        mContext = context;
        mDevice = device;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        if(position == 0){
            frag = new InformationFragment();
        }else if(position == 1){
            frag = new GraphFragment();
        }

        //Passar dados para o fragment
        Bundle bundle = new Bundle();
        bundle.putParcelable("device", mDevice);

        frag.setArguments(bundle);
        //LocalBroadcastManager.getInstance(mContext).registerReceiver(mMessageReceiver, new IntentFilter("data-event"));

        return frag;
    }

    @Override
    public int getCount() {
        return titles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return (titles[position]);
    }
}
