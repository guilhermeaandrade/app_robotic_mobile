package com.example.guilherme.tcc_1_4.Controller;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.example.guilherme.tcc_1_4.Adapter.TabsAdapter;
import com.example.guilherme.tcc_1_4.Extra.ImageHelper;
import com.example.guilherme.tcc_1_4.Extra.SlidingTabLayout;
import com.example.guilherme.tcc_1_4.R;

import java.util.Arrays;

public class MapActivity extends AppCompatActivity{

    private BluetoothDevice device;
    private Toolbar mToolbar;
    private ImageView ivRobo;
    private TextView tvName;
    private TextView tvEndMac;

    private float scale;
    private int width;
    private int height;
    private int roundPixels;

    private XYPlot plot;

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.process_activity_layout);
        //setContentView(R.layout.map_activity_layout);

        mViewPager = (ViewPager) findViewById(R.id.vp_tabs);
        mViewPager.setAdapter(new TabsAdapter(getSupportFragmentManager(), MapActivity.this));

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.stl_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        mSlidingTabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        mSlidingTabLayout.setSelectedIndicatorColors(getResources().getColor(R.color.colorSelectedTab));
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                //Caso tab e navigation tem os mesmos componentes
                //navigationDrawerLeft.setSelection(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        mToolbar = (Toolbar) findViewById(R.id.tb_main);
        mToolbar.setTitle("Mapa Ilustrativo");
        mToolbar.setLogo(R.drawable.ic_launcher);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /*scale = this.getResources().getDisplayMetrics().density;
        width = this.getResources().getDisplayMetrics().widthPixels - (int)(14 * scale + 0.5f);
        height = (width/16)*9;

        roundPixels = (int)(2 * scale + 0.5f);

        Intent intentExtras = getIntent();
        Bundle extrasBundle = intentExtras.getExtras();
        if(!extrasBundle.isEmpty()){
            device = extrasBundle.getParcelable("device");
            if(device == null) {
                launchRingDialog();
            }
            if(device != null){
                launchRingDialog();
                Bitmap bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.robo);
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);
                bitmap = ImageHelper.getRoundedCornerBitmap(this, bitmap, 15, width, height, false, false, true, true);

                ivRobo = (ImageView) findViewById(R.id.robo_photo);
                ivRobo.setImageBitmap(bitmap);

                tvName = (TextView) findViewById(R.id.name_robo);
                tvName.setText(device.getName());

                tvEndMac = (TextView) findViewById(R.id.end_mac_robo);
                tvEndMac.setText(device.getAddress());

                /*dynamicPlot.getGraphWidget().getDomainGridLinePaint().setColor(Color.TRANSPARENT);
                //set all domain lines to transperent

                dynamicPlot.getGraphWidget().getRangeSubGridLinePaint().setColor(Color.TRANSPARENT);
                //set all range lines to transperent

                dynamicPlot.getGraphWidget().getRangeGridLinePaint().setColor(Color.TRANSPARENT);
                //set all sub range lines to transperent

                dynamicPlot.getGraphWidget().getDomainSubGridLinePaint().setColor(Color.TRANSPARENT);
                //set all sub domain lines to transperent


                dynamicPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.WHITE);
                //set background to white to transperent */
     //           drawMap();
//            }
        //}*/
    }
/*
    private void drawMap(){
        // initialize our XYPlot reference:
        plot = (XYPlot) findViewById(R.id.plot);
        // create a couple arrays of y-values to plot:
        Number[] series1Numbers = {1, 4, 2, 8, 4, 16, 8, 32, 16, 64};
        Number[] series2Numbers = {5, 2, 10, 5, 20, 10, 40, 20, 80, 40};

        // turn the above arrays into XYSeries':
        // (Y_VALS_ONLY means use the element index as the x value)
        XYSeries series1 = new SimpleXYSeries(Arrays.asList(series1Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series1");

        XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");

        // create formatters to use for drawing a series using LineAndPointRenderer
        // and configure them from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter();
        series1Format.setPointLabelFormatter(new PointLabelFormatter());
        series1Format.configure(getApplicationContext(),
                R.xml.line_point_formatter_with_label);

        LineAndPointFormatter series2Format = new LineAndPointFormatter();
        series2Format.setPointLabelFormatter(new PointLabelFormatter());
        series2Format.configure(getApplicationContext(),
                R.xml.line_point_formatter_with_label_2);

        // add an "dash" effect to the series2 line:
        series2Format.getLinePaint().setPathEffect(
                new DashPathEffect(new float[] {

                        // always use DP when specifying pixel sizes, to keep things consistent across devices:
                        PixelUtils.dpToPix(20),
                        PixelUtils.dpToPix(15)}, 0));

        // just for fun, add some smoothing to the lines:
        // see: http://androidplot.com/smooth-curves-and-androidplot/
        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        series2Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));

        // add a new series' to the xyplot:

        plot.addSeries(series1, series1Format);
        plot.addSeries(series2, series2Format);

        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);

        // rotate domain labels 45 degrees to make them more compact horizontally:
        plot.getGraphWidget().setDomainLabelOrientation(-45);
    }
    */
    public void launchRingDialog() {
        final ProgressDialog ringProgressDialog = ProgressDialog.show(MapActivity.this, "Please wait ...", "Searching for connections and settings ...", true);
        ringProgressDialog.setCancelable(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(5000);
                    //finish();
                } catch (Exception e) {}
                ringProgressDialog.dismiss();
            }
        }).start();
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
