package com.example.guilherme.tcc_1_4.Controller;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import com.example.guilherme.tcc_1_4.Adapter.TabsAdapter;
import com.example.guilherme.tcc_1_4.Extra.SlidingTabLayout;
import com.example.guilherme.tcc_1_4.Model.ManualPosition;
import com.example.guilherme.tcc_1_4.Model.Position;
import com.example.guilherme.tcc_1_4.R;
import com.example.guilherme.tcc_1_4.Utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity{

    private BluetoothDevice device;
    private Toolbar mToolbar;
    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private List<Position> automaticMoviments;
    private List<ManualPosition> manualMoviments;
    private int optionControl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_activity_layout);

        automaticMoviments =  new ArrayList<Position>();
        manualMoviments = new ArrayList<ManualPosition>();

        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        if(!extrasBundle.isEmpty()){
            device = extrasBundle.getParcelable("device");
            optionControl = extrasBundle.getInt("optionControl");
            automaticMoviments = extrasBundle.getParcelableArrayList("automaticMoviments");
            manualMoviments = extrasBundle.getParcelableArrayList("manualMoviments");
            Log.i("TAG", "MapActivity -> " + automaticMoviments.size());
        }

        mViewPager = (ViewPager) findViewById(R.id.vp_tabs);
        mViewPager.setAdapter(new TabsAdapter(getSupportFragmentManager(), MapActivity.this, device, optionControl, automaticMoviments, manualMoviments));

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorSelectedTab));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

            @Override
            public void onPageSelected(int position) {}

            @Override
            public void onPageScrollStateChanged(int state) {}
        });

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle(Constants.PROCESS);
        mToolbar.setLogo(R.drawable.ic_launcher);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == android.R.id.home){
            finish();
        }
        return true;
    }

}
