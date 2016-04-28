package com.example.guilherme.tcc_1_4.Fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.XYStepMode;
import com.example.guilherme.tcc_1_4.Model.Position;
import com.example.guilherme.tcc_1_4.R;
import com.example.guilherme.tcc_1_4.Utils.Constants;
import com.example.guilherme.tcc_1_4.Utils.SingletonConnection;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GraphFragment extends Fragment{

    private static final int NONE = 0;
    private  static final int ONE_FINGER_DRAG = 1;
    private static final int TWO_FINGERS_DRAG = 2;
    private int mode = NONE;
    private int optionGraphic = 0;
    private boolean updateGraphicControl = true;
    private static DrawGraphic draw;

    PointF firstFinger;
    float lastScrolling;
    float distBetweenFingers;
    float lastZooming;

    private static List<Position> moviments;

    private Spinner spGraphType;

    private static XYPlot xyPlot;
    private XYSeriesShimmer series;
    private LineAndPointFormatter series1Format;
    private ArrayList<Number> ALdataX, ALdataY;
    private float AdataX[], AdataY[];
    private int init = 0;
    private float[] bounderies = new float[4];
    private PointF minXY;
    private PointF maxXY;

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if(!bundle.isEmpty()){
                moviments = bundle.getParcelableArrayList("moviments");
            }
            SingletonConnection.getInstance().setMoviments(moviments);
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        Log.i(Constants.TAG, "GRAPHFRAGMENT -> onCreateView()");

        View view = inflater.inflate(R.layout.graph_fragment_layout, container, false);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("data-event"));

        Log.i(Constants.TAG, "GRAPHFRAGMENT -> onCreateView() -> size: " + SingletonConnection.getInstance().getMoviments().size());

        spGraphType = (Spinner) view.findViewById(R.id.spGraphs);
        spGraphType.setVisibility(View.VISIBLE);

        Drawable spinnerDrawable = spGraphType.getBackground().getConstantState().newDrawable();
        spinnerDrawable.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            spGraphType.setBackground(spinnerDrawable);
        }

        spGraphType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                Log.i(Constants.TAG, "VERIFICAR A THREAD: "+draw.getName()+" - "+draw.getId()+" - "+draw.isAlive());
                optionGraphic = position;
                init = 1;

                if(SingletonConnection.getInstance().getMoviments() != null && SingletonConnection.getInstance().getMoviments().size() > 0)
                    Log.i(Constants.TAG, "ENTREI NA ESCOLHA -> TAMANHO DO VETOR DE POSIÇÕES SINGLETON: " + SingletonConnection.getInstance().getMoviments().size());

                //if (moviments != null)
                  //  Log.i(Constants.TAG, "ENTREI NA ESCOLHA -> TAMANHO DO VETOR DE POSIÇÕES: " + moviments.size());

                switch (optionGraphic) {
                    case 0:
                        xyPlot.removeSeries(series);
                        xyPlot.clear();

                        scriptUpdatePlot();

                        xyPlot.setDomainLabel("x (m)");
                        xyPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 0.025);
                        xyPlot.setDomainValueFormat(new DecimalFormat("#.##"));

                        xyPlot.setRangeLabel("y (m)");
                        xyPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 0.025);
                        xyPlot.setRangeValueFormat(new DecimalFormat("#.##"));

                        xyPlot.redraw();
                        break;

                    case 1:
                        xyPlot.removeSeries(series);
                        xyPlot.clear();

                        scriptUpdatePlot();

                        xyPlot.setDomainLabel("tempo (s)");
                        xyPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 4.0);
                        xyPlot.setDomainValueFormat(new DecimalFormat("#"));

                        xyPlot.setRangeLabel("x (m)");
                        xyPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 0.025);
                        xyPlot.setRangeValueFormat(new DecimalFormat("#.##"));

                        xyPlot.redraw();
                        break;

                    case 2:
                        xyPlot.removeSeries(series);
                        xyPlot.clear();

                        scriptUpdatePlot();

                        xyPlot.setDomainLabel("tempo (s)");
                        xyPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 4.0);
                        xyPlot.setDomainValueFormat(new DecimalFormat("#"));

                        xyPlot.setRangeLabel("y (m)");
                        xyPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 0.025);
                        xyPlot.setRangeValueFormat(new DecimalFormat("#.##"));

                        xyPlot.redraw();
                        break;

                    case 3:
                        xyPlot.removeSeries(series);
                        xyPlot.clear();

                        scriptUpdatePlot();

                        xyPlot.setDomainLabel("tempo (s)");
                        xyPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 4.0);
                        xyPlot.setDomainValueFormat(new DecimalFormat("#"));

                        xyPlot.setRangeLabel("Angulo (graus)");
                        xyPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 0.5);
                        xyPlot.setRangeValueFormat(new DecimalFormat("#.##"));

                        xyPlot.redraw();
                        break;

                    case 4:
                        xyPlot.removeSeries(series);
                        xyPlot.clear();

                        scriptUpdatePlot();

                        xyPlot.setDomainLabel("tempo (s)");
                        xyPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 4.0);
                        xyPlot.setDomainValueFormat(new DecimalFormat("#"));

                        xyPlot.setRangeLabel("v (cm/s)");
                        xyPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 0.005);
                        xyPlot.setRangeValueFormat(new DecimalFormat("#.##"));

                        xyPlot.redraw();
                        break;

                    case 5:
                        xyPlot.removeSeries(series);
                        xyPlot.clear();

                        scriptUpdatePlot();

                        xyPlot.setDomainLabel("tempo (s)");
                        xyPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 4.0);
                        xyPlot.setDomainValueFormat(new DecimalFormat("#"));

                        xyPlot.setRangeLabel("w (cm/s)");
                        xyPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 0.5);
                        xyPlot.setRangeValueFormat(new DecimalFormat("#.##"));

                        xyPlot.redraw();
                        break;

                    case 6:
                        xyPlot.removeSeries(series);
                        xyPlot.clear();

                        scriptUpdatePlot();

                        xyPlot.setDomainLabel("tempo (s)");
                        xyPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 4.0);
                        xyPlot.setDomainValueFormat(new DecimalFormat("#"));

                        xyPlot.setRangeLabel("erro");
                        xyPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 0.025);
                        xyPlot.setRangeValueFormat(new DecimalFormat("#.##"));

                        xyPlot.redraw();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        xyPlot = (XYPlot) view.findViewById(R.id.plot);
        xyPlot.setRangeValueFormat(new DecimalFormat("#.##"));

        ALdataX = new ArrayList<Number>();
        ALdataY = new ArrayList<Number>();

        series1Format = new LineAndPointFormatter();
        series1Format.setPointLabelFormatter(new PointLabelFormatter(Color.TRANSPARENT));
        series1Format.configure(getActivity().getApplicationContext(),
                R.xml.line_point_formatter_with_label);
        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(20, CatmullRomInterpolator.Type.Centripetal));

        xyPlot.setTicksPerRangeLabel(3);
        xyPlot.setTicksPerDomainLabel(3);

        xyPlot.getGraphWidget().setDomainLabelOrientation(-45);

        Log.i(Constants.TAG, "CRIEI A THREAD");
        draw = new DrawGraphic();
        Log.i(Constants.TAG, "INICIEI A THREAD");
        draw.start();

        return view;
    }

    private void scriptUpdatePlot(){
        if(SingletonConnection.getInstance().getMoviments().size() == 0 || SingletonConnection.getInstance().getMoviments() == null) return;
        // if(moviments.size() == 0 || moviments == null) return;
        int tamVector;

        xyPlot.removeSeries(series);
        xyPlot.clear();

        ALdataX.clear();
        ALdataY.clear();

        tamVector = SingletonConnection.getInstance().getMoviments().size();
        //tamVector = moviments.size();
        Log.i(Constants.TAG, "Size: "+tamVector);

        if(tamVector >= 1 && tamVector < 20) {
            Log.i(Constants.TAG, "Entrei no IF -> scriptUpdatePlot()");
            AdataX = new float[tamVector];
            AdataY = new float[tamVector];
        }else {
            Log.i(Constants.TAG, "Entrei no ELSE -> scriptUpdatePlot()");
            AdataX = new float[Constants.COUNT_POINTS];
            AdataY = new float[Constants.COUNT_POINTS];
        }
        Log.i(Constants.TAG, "Size vetor: "+AdataX.length);
        Log.i(Constants.TAG, "Size vetor: "+AdataY.length);

        getDataSource();

        //AdataX = new float[]{1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f};
        //AdataY = new float[]{10f, 20f, 30f, 40f, 50f, 60f, 70f, 80f, 90f, 100f};

        //AdataX = new float[Constants.COUNT_POINTS];
        //AdataY = new float[Constants.COUNT_POINTS];
        //getDataSource();

        bounderies = getBoundaries();
        Log.i(Constants.TAG, "BOUNDERIES: " + bounderies[0] + " - " + bounderies[1] + " - " + bounderies[2] + " - " + bounderies[3]);
        xyPlot.setDomainBoundaries(bounderies[0], bounderies[1], BoundaryMode.AUTO);
        xyPlot.setRangeBoundaries(bounderies[2], bounderies[3], BoundaryMode.AUTO);

        switch (optionGraphic){
            case 0:
                series = new XYSeriesShimmer(ALdataX, ALdataY, 0, "Percurso");
                break;
            case 1:
                series = new XYSeriesShimmer(ALdataX, ALdataY, 0, "Variação de x");
                break;
            case 2:
                series = new XYSeriesShimmer(ALdataX, ALdataY, 0, "Variação de y");
                break;
            case 3:
                series = new XYSeriesShimmer(ALdataX, ALdataY, 0, "Variação de theta");
                break;
            case 4:
                series = new XYSeriesShimmer(ALdataX, ALdataY, 0, "Variação da Vel. Lin");
                break;
            case 5:
                series = new XYSeriesShimmer(ALdataX, ALdataY, 0, "Variação da Vel. Ang");
                break;
            case 6:
                series = new XYSeriesShimmer(ALdataX, ALdataY, 0, "Variação do erro");
                break;
        }

        xyPlot.addSeries(series, series1Format);

        for(int i=0; i< AdataX.length; i++){
            ALdataX.add(AdataX[i]);
            ALdataY.add(AdataY[i]);

            series.updateData(ALdataX, ALdataY);
            xyPlot.redraw();
        }
        xyPlot.redraw();
    }

    private void getDataSource(){
        if(SingletonConnection.getInstance().getMoviments().size() <= 0) return;

        if(SingletonConnection.getInstance().getMoviments().size() >= 1 && SingletonConnection.getInstance().getMoviments().size() < 20){
            Log.i(Constants.TAG, "Entrei no IF -> getDataSource()");
            switch (optionGraphic){
                case 0:
                    for(int i = 0; i < SingletonConnection.getInstance().getMoviments().size(); i++){
                        AdataX[i] = SingletonConnection.getInstance().getMoviments().get(i).getX().floatValue();
                        AdataY[i] = SingletonConnection.getInstance().getMoviments().get(i).getY().floatValue();
                    }
                    break;
                case 1:
                    for(int i = 0; i < SingletonConnection.getInstance().getMoviments().size(); i++){
                        AdataX[i] = SingletonConnection.getInstance().getMoviments().get(i).getT().floatValue();
                        AdataY[i] = SingletonConnection.getInstance().getMoviments().get(i).getX().floatValue();
                    }
                    break;
                case 2:
                    for(int i = 0; i < SingletonConnection.getInstance().getMoviments().size(); i++){
                        AdataX[i] = SingletonConnection.getInstance().getMoviments().get(i).getT().floatValue();
                        AdataY[i] = SingletonConnection.getInstance().getMoviments().get(i).getY().floatValue();
                    }
                    break;
                case 3:
                    for(int i = 0; i < SingletonConnection.getInstance().getMoviments().size(); i++){
                        AdataX[i] = SingletonConnection.getInstance().getMoviments().get(i).getT().floatValue();
                        AdataY[i] = SingletonConnection.getInstance().getMoviments().get(i).getTheta().floatValue();
                    }
                    break;
                case 4:
                    for(int i = 0; i < SingletonConnection.getInstance().getMoviments().size(); i++){
                        AdataX[i] = SingletonConnection.getInstance().getMoviments().get(i).getT().floatValue();
                        AdataY[i] = SingletonConnection.getInstance().getMoviments().get(i).getV().floatValue();
                    }
                    break;
                case 5:
                    for(int i = 0; i < SingletonConnection.getInstance().getMoviments().size(); i++){
                        AdataX[i] = SingletonConnection.getInstance().getMoviments().get(i).getT().floatValue();
                        AdataY[i] = SingletonConnection.getInstance().getMoviments().get(i).getW().floatValue();
                    }
                    break;
                case 6:
                    for(int i = 0; i < SingletonConnection.getInstance().getMoviments().size(); i++){
                        AdataX[i] = SingletonConnection.getInstance().getMoviments().get(i).getT().floatValue();
                        AdataY[i] = SingletonConnection.getInstance().getMoviments().get(i).getE().floatValue();
                    }
                    break;
            }
        }else {
            Log.i(Constants.TAG, "Entrei no ELSE -> getDataSource()");
            float high = SingletonConnection.getInstance().getMoviments().get(SingletonConnection.getInstance().getMoviments().size() - 1).getT();
            float low = SingletonConnection.getInstance().getMoviments().get(0).getT();

            double interval = (double) ((high - low + 1)/Constants.BUCKET_COUNT);
            ArrayList<Position> buckets[] = new ArrayList[Constants.BUCKET_COUNT];

            for(int i = 0; i < Constants.BUCKET_COUNT; i++){
                buckets[i] =  new ArrayList<>();
            }

            Log.i(Constants.TAG, "---> Moviments size: " + SingletonConnection.getInstance().getMoviments().size());
            for (int i = 0; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                Log.i(Constants.TAG, "-------> POSICIONAMENTO DO BUCKET: " +(int)((SingletonConnection.getInstance().getMoviments().get(i).getT() - low)/interval));
                buckets[(int)((SingletonConnection.getInstance().getMoviments().get(i).getT() - low)/interval)].add(SingletonConnection.getInstance().getMoviments().get(i));
            }

            fillFirsAndLastValue();

            for(int i = 0; i < buckets.length; i++){
                Log.i(Constants.TAG, "BUCKET: " + i);
                Log.i(Constants.TAG, "BUCKET SIZE: " + buckets[i].size());
                getValuesXY(buckets[i]);
            }

            Log.i(Constants.TAG, "AdataX size: "+AdataX.length);
            Log.i(Constants.TAG, "AdataY size: " + AdataY.length);
            for (int i = 0; i < AdataX.length; i++){
                Log.i(Constants.TAG, "AdataX: "+AdataX[i]);
                Log.i(Constants.TAG, "AdataY: "+AdataY[i]);
            }
        }
    }

    private float[] getBoundaries() {
        float[] bound = new float[4];

        Float minValueX;
        Float maxValueX;
        Float minValueY;
        Float maxValueY;

        if(SingletonConnection.getInstance().getMoviments().size() > 0){
            switch (optionGraphic){
                case 0:
                    minValueX = (SingletonConnection.getInstance().getMoviments().get(0).getX()).floatValue();
                    maxValueX = (SingletonConnection.getInstance().getMoviments().get(0).getX()).floatValue();
                    for (int i = 1; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                        if (SingletonConnection.getInstance().getMoviments().get(i).getX() < minValueX) {
                            minValueX = (SingletonConnection.getInstance().getMoviments().get(i).getX()).floatValue();
                        } else if (SingletonConnection.getInstance().getMoviments().get(i).getX() > maxValueX) {
                            maxValueX = (SingletonConnection.getInstance().getMoviments().get(i).getX()).floatValue();
                        }
                    }
                    bound[0] = minValueX;
                    bound[1] = maxValueX;

                    minValueY = (SingletonConnection.getInstance().getMoviments().get(0).getY()).floatValue();
                    maxValueY = (SingletonConnection.getInstance().getMoviments().get(0).getY()).floatValue();
                    for (int i = 1; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                        if (SingletonConnection.getInstance().getMoviments().get(i).getY() < minValueY) {
                            minValueY = (SingletonConnection.getInstance().getMoviments().get(i).getY()).floatValue();
                        } else if (SingletonConnection.getInstance().getMoviments().get(i).getY() > maxValueY) {
                            maxValueY = (SingletonConnection.getInstance().getMoviments().get(i).getY()).floatValue();
                        }
                    }
                    bound[2] = minValueY;
                    bound[3] = maxValueY;

                    break;

                case 1:
                    minValueX = SingletonConnection.getInstance().getMoviments().get(0).getT();
                    maxValueX = SingletonConnection.getInstance().getMoviments().get(0).getT();
                    for (int i = 1; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                        if (SingletonConnection.getInstance().getMoviments().get(i).getT() < minValueX) {
                            minValueX = SingletonConnection.getInstance().getMoviments().get(i).getT();
                        } else if (SingletonConnection.getInstance().getMoviments().get(i).getT() > maxValueX) {
                            maxValueX = SingletonConnection.getInstance().getMoviments().get(i).getT();
                        }
                    }
                    bound[0] = minValueX;
                    bound[1] = maxValueX;

                    minValueY = SingletonConnection.getInstance().getMoviments().get(0).getX().floatValue();
                    maxValueY = SingletonConnection.getInstance().getMoviments().get(0).getX().floatValue();
                    for (int i = 1; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                        if (SingletonConnection.getInstance().getMoviments().get(i).getX() < minValueY) {
                            minValueY = SingletonConnection.getInstance().getMoviments().get(i).getX().floatValue();
                        } else if (SingletonConnection.getInstance().getMoviments().get(i).getX() > maxValueY) {
                            maxValueY = SingletonConnection.getInstance().getMoviments().get(i).getX().floatValue();
                        }
                    }
                    bound[2] = minValueY;
                    bound[3] = maxValueY;

                    break;
                case 2:
                    minValueX = SingletonConnection.getInstance().getMoviments().get(0).getT();
                    maxValueX = SingletonConnection.getInstance().getMoviments().get(0).getT();
                    for (int i = 1; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                        if (SingletonConnection.getInstance().getMoviments().get(i).getT() < minValueX) {
                            minValueY = SingletonConnection.getInstance().getMoviments().get(i).getT();
                        } else if (SingletonConnection.getInstance().getMoviments().get(i).getT() > maxValueX) {
                            maxValueY = SingletonConnection.getInstance().getMoviments().get(i).getT();
                        }
                    }
                    bound[0] = minValueX;
                    bound[1] = maxValueX;

                    minValueY = SingletonConnection.getInstance().getMoviments().get(0).getY().floatValue();
                    maxValueY = SingletonConnection.getInstance().getMoviments().get(0).getY().floatValue();
                    for (int i = 1; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                        if (SingletonConnection.getInstance().getMoviments().get(i).getY() < minValueY) {
                            minValueY = SingletonConnection.getInstance().getMoviments().get(i).getY().floatValue();
                        } else if (SingletonConnection.getInstance().getMoviments().get(i).getY() > maxValueY) {
                            maxValueY = SingletonConnection.getInstance().getMoviments().get(i).getY().floatValue();
                        }
                    }
                    bound[2] = minValueY;
                    bound[3] = maxValueY;

                    break;
                case 3:
                    minValueX = SingletonConnection.getInstance().getMoviments().get(0).getT();
                    maxValueX = SingletonConnection.getInstance().getMoviments().get(0).getT();
                    for (int i = 1; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                        if (SingletonConnection.getInstance().getMoviments().get(i).getT() < minValueX) {
                            minValueX = SingletonConnection.getInstance().getMoviments().get(i).getT();
                        } else if (SingletonConnection.getInstance().getMoviments().get(i).getT() > maxValueX) {
                            maxValueX = SingletonConnection.getInstance().getMoviments().get(i).getT();
                        }
                    }
                    bound[0] = minValueX;
                    bound[1] = maxValueX;

                    minValueY = SingletonConnection.getInstance().getMoviments().get(0).getTheta().floatValue();
                    maxValueY = SingletonConnection.getInstance().getMoviments().get(0).getTheta().floatValue();
                    for (int i = 1; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                        if (SingletonConnection.getInstance().getMoviments().get(i).getTheta() < minValueY) {
                            minValueY = SingletonConnection.getInstance().getMoviments().get(i).getTheta().floatValue();
                        } else if (SingletonConnection.getInstance().getMoviments().get(i).getTheta() > maxValueY) {
                            maxValueY = SingletonConnection.getInstance().getMoviments().get(i).getTheta().floatValue();
                        }
                    }
                    bound[2] = minValueY;
                    bound[3] = maxValueY;

                    break;
                case 4:
                    minValueX = SingletonConnection.getInstance().getMoviments().get(0).getT();
                    maxValueX = SingletonConnection.getInstance().getMoviments().get(0).getT();
                    for (int i = 1; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                        if (SingletonConnection.getInstance().getMoviments().get(i).getT() < minValueX) {
                            minValueX = SingletonConnection.getInstance().getMoviments().get(i).getT();
                        } else if (SingletonConnection.getInstance().getMoviments().get(i).getT() > maxValueX) {
                            maxValueX = SingletonConnection.getInstance().getMoviments().get(i).getT();
                        }
                    }
                    bound[0] = minValueX;
                    bound[1] = maxValueX;

                    minValueY = SingletonConnection.getInstance().getMoviments().get(0).getV().floatValue();
                    maxValueY = SingletonConnection.getInstance().getMoviments().get(0).getV().floatValue();
                    for (int i = 1; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                        if (SingletonConnection.getInstance().getMoviments().get(i).getV() < minValueY) {
                            minValueY = SingletonConnection.getInstance().getMoviments().get(i).getV().floatValue();
                        } else if (SingletonConnection.getInstance().getMoviments().get(i).getV() > maxValueY) {
                            maxValueY = SingletonConnection.getInstance().getMoviments().get(i).getV().floatValue();
                        }
                    }
                    bound[2] = minValueY;
                    bound[3] = maxValueY;

                    break;
                case 5:
                    minValueX = SingletonConnection.getInstance().getMoviments().get(0).getT();
                    maxValueX = SingletonConnection.getInstance().getMoviments().get(0).getT();
                    for (int i = 1; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                        if (SingletonConnection.getInstance().getMoviments().get(i).getT() < minValueX) {
                            minValueX = SingletonConnection.getInstance().getMoviments().get(i).getT();
                        } else if (SingletonConnection.getInstance().getMoviments().get(i).getT() > maxValueX) {
                            maxValueX = SingletonConnection.getInstance().getMoviments().get(i).getT();
                        }
                    }
                    bound[0] = minValueX;
                    bound[1] = maxValueX;

                    minValueY = SingletonConnection.getInstance().getMoviments().get(0).getW().floatValue();
                    maxValueY = SingletonConnection.getInstance().getMoviments().get(0).getW().floatValue();
                    for (int i = 1; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                        if (SingletonConnection.getInstance().getMoviments().get(i).getW() < minValueY) {
                            minValueY = SingletonConnection.getInstance().getMoviments().get(i).getW().floatValue();
                        } else if (SingletonConnection.getInstance().getMoviments().get(i).getW() > maxValueY) {
                            maxValueY = SingletonConnection.getInstance().getMoviments().get(i).getW().floatValue();
                        }
                    }
                    bound[2] = minValueY;
                    bound[3] = maxValueY;

                    break;
                case 6:
                    minValueX = SingletonConnection.getInstance().getMoviments().get(0).getT();
                    maxValueX = SingletonConnection.getInstance().getMoviments().get(0).getT();
                    for (int i = 1; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                        if (SingletonConnection.getInstance().getMoviments().get(i).getT() < minValueX) {
                            minValueX = SingletonConnection.getInstance().getMoviments().get(i).getT();
                        } else if (SingletonConnection.getInstance().getMoviments().get(i).getT() > maxValueX) {
                            maxValueX = SingletonConnection.getInstance().getMoviments().get(i).getT();
                        }
                    }
                    bound[0] = minValueX;
                    bound[1] = maxValueX;

                    minValueY = SingletonConnection.getInstance().getMoviments().get(0).getE().floatValue();
                    maxValueY = SingletonConnection.getInstance().getMoviments().get(0).getE().floatValue();
                    for (int i = 1; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                        if (SingletonConnection.getInstance().getMoviments().get(i).getE() < minValueY) {
                            minValueY = SingletonConnection.getInstance().getMoviments().get(i).getE().floatValue();
                        } else if (SingletonConnection.getInstance().getMoviments().get(i).getE() > maxValueY) {
                            maxValueY = SingletonConnection.getInstance().getMoviments().get(i).getE().floatValue();
                        }
                    }
                    bound[2] = minValueY;
                    bound[3] = maxValueY;

                    break;
            }
        }
        return bound;
    }

    private float getIntervalScale(float low, float high){
        float interval = (float) (high - low + 1)/Constants.NUMBER_SCALE;
        Log.i(Constants.TAG, "------------> Interval: " + interval);
        return  interval;
    }

    private void fillFirsAndLastValue() {
        /*Log.i(Constants.TAG, "Primeiro e ultimo");
        Log.i(Constants.TAG, "x: "+SingletonConnection.getInstance().getMoviments().get(0).getX());
        Log.i(Constants.TAG, "lx: "+SingletonConnection.getInstance().getMoviments().get(SingletonConnection.getInstance().getMoviments().size() - 1).getX());

        Log.i(Constants.TAG, "y: "+SingletonConnection.getInstance().getMoviments().get(0).getY());
        Log.i(Constants.TAG, "ly: "+moviments.get(moviments.size() - 1).getY());

        Log.i(Constants.TAG, "theta: "+moviments.get(0).getTheta());
        Log.i(Constants.TAG, "ltheta: "+moviments.get(moviments.size() - 1).getTheta());

        Log.i(Constants.TAG, "t: "+moviments.get(0).getT());
        Log.i(Constants.TAG, "lt: "+moviments.get(moviments.size() - 1).getT());*/

        switch (optionGraphic){
            case 0:
                AdataX[0] = (SingletonConnection.getInstance().getMoviments().get(0).getX().floatValue());
                AdataY[0] = (SingletonConnection.getInstance().getMoviments().get(0).getY().floatValue());

                AdataX[19] = (SingletonConnection.getInstance().getMoviments().get(SingletonConnection.getInstance().getMoviments().size() - 1).getX().floatValue());
                AdataY[19] = (SingletonConnection.getInstance().getMoviments().get(SingletonConnection.getInstance().getMoviments().size() - 1).getY().floatValue());
                break;

            case 1:
                AdataX[0] = (SingletonConnection.getInstance().getMoviments().get(0).getT().floatValue());
                AdataY[0] = (SingletonConnection.getInstance().getMoviments().get(0).getX().floatValue());

                AdataX[19] = (SingletonConnection.getInstance().getMoviments().get(SingletonConnection.getInstance().getMoviments().size() - 1).getT().floatValue());
                AdataY[19] = (SingletonConnection.getInstance().getMoviments().get(SingletonConnection.getInstance().getMoviments().size() - 1).getX().floatValue());
                break;

            case 2:
                AdataX[0] = (SingletonConnection.getInstance().getMoviments().get(0).getT().floatValue());
                AdataY[0] = (SingletonConnection.getInstance().getMoviments().get(0).getY().floatValue());

                AdataX[19] = (SingletonConnection.getInstance().getMoviments().get(SingletonConnection.getInstance().getMoviments().size() - 1).getT().floatValue());
                AdataY[19] = (SingletonConnection.getInstance().getMoviments().get(SingletonConnection.getInstance().getMoviments().size() - 1).getY().floatValue());
                break;

            case 3:
                AdataX[0] = (SingletonConnection.getInstance().getMoviments().get(0).getT().floatValue());
                AdataY[0] = (SingletonConnection.getInstance().getMoviments().get(0).getTheta().floatValue());

                AdataX[19] = (SingletonConnection.getInstance().getMoviments().get(SingletonConnection.getInstance().getMoviments().size() - 1).getT().floatValue());
                AdataY[19] = (SingletonConnection.getInstance().getMoviments().get(SingletonConnection.getInstance().getMoviments().size() - 1).getTheta().floatValue());
                break;

            case 4:
                AdataX[0] = (SingletonConnection.getInstance().getMoviments().get(0).getT().floatValue());
                AdataY[0] = (SingletonConnection.getInstance().getMoviments().get(0).getV().floatValue());

                AdataX[19] = (SingletonConnection.getInstance().getMoviments().get(SingletonConnection.getInstance().getMoviments().size() - 1).getT().floatValue());
                AdataY[19] = (SingletonConnection.getInstance().getMoviments().get(SingletonConnection.getInstance().getMoviments().size() - 1).getV().floatValue());
                break;

            case 5:
                AdataX[0] = (SingletonConnection.getInstance().getMoviments().get(0).getT().floatValue());
                AdataY[0] = (SingletonConnection.getInstance().getMoviments().get(0).getW().floatValue());

                AdataX[19] = (SingletonConnection.getInstance().getMoviments().get(SingletonConnection.getInstance().getMoviments().size() - 1).getT().floatValue());
                AdataY[19] = (SingletonConnection.getInstance().getMoviments().get(SingletonConnection.getInstance().getMoviments().size() - 1).getW().floatValue());
                break;

            case 6:
                AdataX[0] = (SingletonConnection.getInstance().getMoviments().get(0).getT().floatValue());
                AdataY[0] = (SingletonConnection.getInstance().getMoviments().get(0).getE().floatValue());

                AdataX[19] = (SingletonConnection.getInstance().getMoviments().get(SingletonConnection.getInstance().getMoviments().size() - 1).getT().floatValue());
                AdataY[19] = (SingletonConnection.getInstance().getMoviments().get(SingletonConnection.getInstance().getMoviments().size() - 1).getE().floatValue());
                break;
        }
    }

    private void getValuesXY(ArrayList<Position> bucket) {
        Log.i(Constants.TAG, "getValuesXY ----> BUCKET SIZE: " + bucket.size());

        //int interval = (int) ((bucket.size()-1) - 0 + 1)/Constants.COUNT_VALUES;
        int interval = (int) (bucket.size() - 0 + 1)/Constants.COUNT_VALUES;
        Log.i(Constants.TAG, "getValuesXY ----> interval: "+interval);
        //Log.i(Constants.TAG, "AdataX[init] = "+AdataX[init] + " | AdataY[init] = " +AdataY[init]);
        //Log.i(Constants.TAG, "AdataX[init] = "+AdataX[Constants.COUNT_POINTS - 1] + " | AdataY[init] = " +AdataY[Constants.COUNT_POINTS - 1]);

        if(bucket.size() == 0){
            Log.i(Constants.TAG, "BUCKET VAZIO");
        }else{
            for(int i = 0; i < bucket.size(); i++){
                Log.i(Constants.TAG, "VALOR TEMPO: "+bucket.get(i).getT());
            }
        }

        Log.i(Constants.TAG, "Initial Value: "+init);
        switch (optionGraphic){
            case 0:
                switch(bucket.size()){
                    case 0:
                        AdataX[init] = AdataX[init - 1];
                        AdataY[init] = AdataY[init - 1];
                        init += 1;
                        AdataX[init] = AdataX[init - 1];
                        AdataY[init] = AdataY[init - 1];
                        init += 1;
                        break;

                    case 1:
                        AdataX[init] = bucket.get(0).getX().floatValue();
                        AdataY[init] = bucket.get(0).getY().floatValue();
                        init += 1;
                        AdataX[init] = bucket.get(0).getX().floatValue();
                        AdataY[init] = bucket.get(0).getY().floatValue();
                        init += 1;
                        break;

                    case 2:
                        AdataX[init] = bucket.get(0).getX().floatValue();
                        AdataY[init] = bucket.get(0).getY().floatValue();
                        init += 1;
                        AdataX[init] = bucket.get(1).getX().floatValue();
                        AdataY[init] = bucket.get(1).getY().floatValue();
                        init += 1;
                        break;
                    case 3:
                        AdataX[init] = bucket.get(0).getX().floatValue();
                        AdataY[init] = bucket.get(0).getY().floatValue();
                        init += 1;
                        AdataX[init] = bucket.get(2).getX().floatValue();
                        AdataY[init] = bucket.get(2).getY().floatValue();
                        init += 1;
                        break;

                    default:
                        AdataX[init] = (bucket.get(interval).getX()).floatValue();
                        AdataY[init] = (bucket.get(interval).getY()).floatValue();
                        init += 1;
                        AdataX[init] = (bucket.get(interval*2).getX()).floatValue();
                        AdataY[init] = (bucket.get(interval*2).getY()).floatValue();
                        init += 1;
                }
                break;

            case 1:
                switch(bucket.size()){
                    case 0:
                        AdataX[init] = AdataX[init - 1];
                        AdataY[init] = AdataY[init - 1];
                        init += 1;
                        AdataX[init] = AdataX[init - 1];
                        AdataY[init] = AdataY[init - 1];
                        init += 1;
                        break;

                    case 1:
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getX().floatValue();
                        init += 1;
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getX().floatValue();
                        init += 1;
                        break;

                    case 2:
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getX().floatValue();
                        init += 1;
                        AdataX[init] = bucket.get(1).getT().floatValue();
                        AdataY[init] = bucket.get(1).getX().floatValue();
                        init += 1;
                        break;
                    case 3:
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getX().floatValue();
                        init += 1;
                        AdataX[init] = bucket.get(2).getT().floatValue();
                        AdataY[init] = bucket.get(2).getX().floatValue();
                        init += 1;
                        break;

                    default:
                        AdataX[init] = (bucket.get(interval).getT()).floatValue();
                        AdataY[init] = (bucket.get(interval).getX()).floatValue();
                        init += 1;
                        AdataX[init] = (bucket.get(interval*2).getT()).floatValue();
                        AdataY[init] = (bucket.get(interval*2).getX()).floatValue();
                        init += 1;
                }
                break;

            case 2:
                switch(bucket.size()){
                    case 0:
                        AdataX[init] = AdataX[init - 1];
                        AdataY[init] = AdataY[init - 1];
                        init += 1;
                        AdataX[init] = AdataX[init - 1];
                        AdataY[init] = AdataY[init - 1];
                        init += 1;
                        break;

                    case 1:
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getY().floatValue();
                        init += 1;
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getY().floatValue();
                        init += 1;
                        break;

                    case 2:
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getY().floatValue();
                        init += 1;
                        AdataX[init] = bucket.get(1).getT().floatValue();
                        AdataY[init] = bucket.get(1).getY().floatValue();
                        init += 1;
                        break;
                    case 3:
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getY().floatValue();
                        init += 1;
                        AdataX[init] = bucket.get(2).getT().floatValue();
                        AdataY[init] = bucket.get(2).getY().floatValue();
                        init += 1;
                        break;

                    default:
                        AdataX[init] = (bucket.get(interval).getT()).floatValue();
                        AdataY[init] = (bucket.get(interval).getY()).floatValue();
                        init += 1;
                        AdataX[init] = (bucket.get(interval*2).getT()).floatValue();
                        AdataY[init] = (bucket.get(interval*2).getY()).floatValue();
                        init += 1;
                }
                break;

            case 3:
                switch(bucket.size()){
                    case 0:
                        AdataX[init] = AdataX[init - 1];
                        AdataY[init] = AdataY[init - 1];
                        init += 1;
                        AdataX[init] = AdataX[init - 1];
                        AdataY[init] = AdataY[init - 1];
                        init += 1;
                        break;

                    case 1:
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getTheta().floatValue();
                        init += 1;
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getTheta().floatValue();
                        init += 1;
                        break;

                    case 2:
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getTheta().floatValue();
                        init += 1;
                        AdataX[init] = bucket.get(1).getT().floatValue();
                        AdataY[init] = bucket.get(1).getTheta().floatValue();
                        init += 1;
                        break;
                    case 3:
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getTheta().floatValue();
                        init += 1;
                        AdataX[init] = bucket.get(2).getT().floatValue();
                        AdataY[init] = bucket.get(2).getTheta().floatValue();
                        init += 1;
                        break;

                    default:
                        AdataX[init] = (bucket.get(interval).getT()).floatValue();
                        AdataY[init] = (bucket.get(interval).getTheta()).floatValue();
                        init += 1;
                        AdataX[init] = (bucket.get(interval*2).getT()).floatValue();
                        AdataY[init] = (bucket.get(interval*2).getTheta()).floatValue();
                        init += 1;
                }
                break;

            case 4:
                switch(bucket.size()){
                    case 0:
                        AdataX[init] = AdataX[init - 1];
                        AdataY[init] = AdataY[init - 1];
                        init += 1;
                        AdataX[init] = AdataX[init - 1];
                        AdataY[init] = AdataY[init - 1];
                        init += 1;
                        break;

                    case 1:
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getV().floatValue();
                        init += 1;
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getV().floatValue();
                        init += 1;
                        break;

                    case 2:
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getV().floatValue();
                        init += 1;
                        AdataX[init] = bucket.get(1).getT().floatValue();
                        AdataY[init] = bucket.get(1).getV().floatValue();
                        init += 1;
                        break;
                    case 3:
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getV().floatValue();
                        init += 1;
                        AdataX[init] = bucket.get(2).getT().floatValue();
                        AdataY[init] = bucket.get(2).getV().floatValue();
                        init += 1;
                        break;

                    default:
                        AdataX[init] = (bucket.get(interval).getT()).floatValue();
                        AdataY[init] = (bucket.get(interval).getV()).floatValue();
                        init += 1;
                        AdataX[init] = (bucket.get(interval*2).getT()).floatValue();
                        AdataY[init] = (bucket.get(interval*2).getV()).floatValue();
                        init += 1;
                }
                break;

            case 5:
                switch(bucket.size()){
                    case 0:
                        AdataX[init] = AdataX[init - 1];
                        AdataY[init] = AdataY[init - 1];
                        init += 1;
                        AdataX[init] = AdataX[init - 1];
                        AdataY[init] = AdataY[init - 1];
                        init += 1;
                        break;

                    case 1:
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getW().floatValue();
                        init += 1;
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getW().floatValue();
                        init += 1;
                        break;

                    case 2:
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getW().floatValue();
                        init += 1;
                        AdataX[init] = bucket.get(1).getT().floatValue();
                        AdataY[init] = bucket.get(1).getW().floatValue();
                        init += 1;
                        break;
                    case 3:
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getW().floatValue();
                        init += 1;
                        AdataX[init] = bucket.get(2).getT().floatValue();
                        AdataY[init] = bucket.get(2).getW().floatValue();
                        init += 1;
                        break;

                    default:
                        AdataX[init] = (bucket.get(interval).getT()).floatValue();
                        AdataY[init] = (bucket.get(interval).getW()).floatValue();
                        init += 1;
                        AdataX[init] = (bucket.get(interval*2).getT()).floatValue();
                        AdataY[init] = (bucket.get(interval*2).getW()).floatValue();
                        init += 1;
                }
                break;

            case 6:
                switch(bucket.size()){
                    case 0:
                        AdataX[init] = AdataX[init - 1];
                        AdataY[init] = AdataY[init - 1];
                        init += 1;
                        AdataX[init] = AdataX[init - 1];
                        AdataY[init] = AdataY[init - 1];
                        init += 1;
                        break;

                    case 1:
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getE().floatValue();
                        init += 1;
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getE().floatValue();
                        init += 1;
                        break;

                    case 2:
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getE().floatValue();
                        init += 1;
                        AdataX[init] = bucket.get(1).getT().floatValue();
                        AdataY[init] = bucket.get(1).getE().floatValue();
                        init += 1;
                        break;
                    case 3:
                        AdataX[init] = bucket.get(0).getT().floatValue();
                        AdataY[init] = bucket.get(0).getE().floatValue();
                        init += 1;
                        AdataX[init] = bucket.get(2).getT().floatValue();
                        AdataY[init] = bucket.get(2).getE().floatValue();
                        init += 1;
                        break;

                    default:
                        AdataX[init] = (bucket.get(interval).getT()).floatValue();
                        AdataY[init] = (bucket.get(interval).getE()).floatValue();
                        init += 1;
                        AdataX[init] = (bucket.get(interval*2).getT()).floatValue();
                        AdataY[init] = (bucket.get(interval*2).getE()).floatValue();
                        init += 1;
                }
                break;
        }
    }

    private class XYSeriesShimmer implements XYSeries {
        private List<Number> dataX;
        private List<Number> dataY;
        private int seriesIndex;
        private String title;

        public XYSeriesShimmer(List<Number> datasource, int seriesIndex, String title) {
            this.dataY = datasource;
            this.seriesIndex = seriesIndex;
            this.title = title;
        }

        public XYSeriesShimmer(List<Number> datasourceX, List<Number> datasourceY, int seriesIndex, String title) {
            this.dataX = datasourceX;
            this.dataY = datasourceY;
            this.seriesIndex = seriesIndex;
            this.title = title;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public int size() {
            return dataY.size();
        }

        @Override
        public Number getY(int index) {
            return dataY.get(index);
        }

        @Override
        public Number getX(int index) {
            return dataX.get(index);
        }

        public void updateData(List<Number> datasourceX){
            this.dataY=datasourceX;
        }

        public void updateData(List<Number> datasourceX, List<Number> datasourceY){
            this.dataX=datasourceX;
            this.dataY=datasourceY;
        }

    }

    public boolean onTouch(View arg0, MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN: // Start gesture
                firstFinger = new PointF(event.getX(), event.getY());
                mode = ONE_FINGER_DRAG;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                //When the gesture ends, a thread is created to give inertia to the scrolling and zoom
                Timer t = new Timer();
                t.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        while (Math.abs(lastScrolling) > 1f || Math.abs(lastZooming - 1) < 1.01) {
                            lastScrolling *= .8;
                            scroll(lastScrolling);
                            lastZooming += (1 - lastZooming) * .2;
                            zoom(lastZooming);
                            xyPlot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.AUTO);
                            xyPlot.redraw();
                        }
                    }
                }, 0);

            case MotionEvent.ACTION_POINTER_DOWN: // second finger
                distBetweenFingers = spacing(event);
                // the distance check is done to avoid false alarms
                if (distBetweenFingers > 5f) {
                    mode = TWO_FINGERS_DRAG;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ONE_FINGER_DRAG) {
                    PointF oldFirstFinger = firstFinger;
                    firstFinger = new PointF(event.getX(), event.getY());
                    lastScrolling = oldFirstFinger.x - firstFinger.x;
                    scroll(lastScrolling);
                    lastZooming = (firstFinger.y - oldFirstFinger.y) / xyPlot.getHeight();
                    if (lastZooming < 0)
                        lastZooming = 1 / (1 - lastZooming);
                    else
                        lastZooming += 1;
                    zoom(lastZooming);
                    xyPlot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.AUTO);
                    xyPlot.redraw();

                } else if (mode == TWO_FINGERS_DRAG) {
                    float oldDist = distBetweenFingers;
                    distBetweenFingers = spacing(event);
                    lastZooming = oldDist / distBetweenFingers;
                    zoom(lastZooming);
                    xyPlot.setDomainBoundaries(minXY.x, maxXY.x, BoundaryMode.AUTO);
                    xyPlot.redraw();
                }
                break;
        }
        return true;
    }

    private void zoom(float scale) {
        float domainSpan = maxXY.x	- minXY.x;
        float domainMidPoint = maxXY.x		- domainSpan / 2.0f;
        float offset = domainSpan * scale / 2.0f;
        minXY.x=domainMidPoint- offset;
        maxXY.x=domainMidPoint+offset;
    }

    private void scroll(float pan) {
        float domainSpan = maxXY.x	- minXY.x;
        float step = domainSpan / xyPlot.getWidth();
        float offset = pan * step;
        minXY.x+= offset;
        maxXY.x+= offset;
    }

    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float)Math.sqrt(x * x + y * y);
    }

    private class DrawGraphic extends Thread {

        public DrawGraphic(){}

        public void run(){
            while(updateGraphicControl){
                try {
                    Log.i(Constants.TAG, "Antes sleep 10s -> "+System.currentTimeMillis());
                    Thread.sleep(2500);
                    //scriptUpdatePlot();
                    Log.i(Constants.TAG, "Depois sleep 10s -> "+System.currentTimeMillis());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onDestroy() {
        Log.i(Constants.TAG, "GRAPHFRAGMENT -> onDestroy()");
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
        updateGraphicControl = false;
        super.onDestroy();
    }

    @Override
    public void onStart() {
        Log.i(Constants.TAG, "GRAPHFRAGMENT -> onStart()");
        super.onStart();
    }

    @Override
    public void onResume() {
        Log.i(Constants.TAG, "GRAPHFRAGMENT -> onResume()");
        super.onResume();
    }
}
