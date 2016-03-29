package com.example.guilherme.tcc_1_4.Adapter;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.guilherme.tcc_1_4.Fragments.GraphFragment;
import com.example.guilherme.tcc_1_4.Fragments.InformationFragment;

public class TabsAdapter extends FragmentPagerAdapter {

    private String[] titles = new String[]{"INFORMAÇÕES", "GRÁFICOS"};
    private Context mContext;
    private BluetoothDevice mDevice;

    public TabsAdapter(FragmentManager fragmentManager, Context context, BluetoothDevice device){
        super(fragmentManager);
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
