package com.example.guilherme.tcc_1_4.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class TabsAdapter extends FragmentPagerAdapter {

    private String[] titles = new String[]{"PROCESSO", "DADOS"};
    private Context mContext;

    public TabsAdapter(FragmentManager fragmentManager, Context context){
        super(fragmentManager);
        mContext = context;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment frag = null;
        if(position == 0){

        }else if(position == 1){

        }

        //Passar dados para o fragment
        Bundle bundle = new Bundle();
        bundle.putInt("Position", position);

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
