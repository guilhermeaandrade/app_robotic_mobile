package com.example.guilherme.tcc_1_4.Fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
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
import com.example.guilherme.tcc_1_4.Utils.SingletonInformation;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;

public class GraphFragment extends Fragment{

    private static final int NONE = 0;
    private int optionGraphic = 0;
    private boolean updateGraphicControl = true;
    private static DrawGraphic draw;

    private static List<Position> moviments;

    private Spinner spGraphType;

    private static XYPlot xyPlot;
    private XYSeriesShimmer series;
    private LineAndPointFormatter series1Format;
    private ArrayList<Number> ALdataX, ALdataY;
    private float AdataX[], AdataY[];
    private int init = 0;
    private float[] bounderies = new float[4];

    private float xDomainStep;
    private float yRangeStep;
    private boolean optionChanged = false;
    private int choiceOptionControl = -1;
    public Semaphore drawSemaphore = new Semaphore(1);

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
        draw = new DrawGraphic();
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graph_fragment_layout, container, false);

        LocalBroadcastManager.getInstance(getContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("data-event"));

        spGraphType = (Spinner) view.findViewById(R.id.spGraphs);
        spGraphType.setVisibility(View.VISIBLE);

        Drawable spinnerDrawable = spGraphType.getBackground().getConstantState().newDrawable();
        spinnerDrawable.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            spGraphType.setBackground(spinnerDrawable);
        }

        Log.i(Constants.TAG, "PREVOPTIONCONTROL: " + SingletonInformation.getInstance().getPrevOptionControl());
        Log.i(Constants.TAG, "OPTIONCONTROL: " + SingletonInformation.getInstance().getOptionControl());
        if(SingletonInformation.getInstance().getPrevOptionControl() == -1) {
            optionChanged = false;
            SingletonInformation.getInstance().setPrevOptionControl(
                    SingletonInformation.getInstance().getOptionControl());
        }else {
            if(SingletonInformation.getInstance().getPrevOptionControl() == 1 &&
                    SingletonInformation.getInstance().getOptionControl() == 2) {
                optionChanged = true;
                SingletonInformation.getInstance().setPrevOptionControl(
                        SingletonInformation.getInstance().getOptionControl());
            }
            if(SingletonInformation.getInstance().getPrevOptionControl() == 2 &&
                    SingletonInformation.getInstance().getOptionControl() == 1){
                optionChanged = true;
                SingletonInformation.getInstance().setPrevOptionControl(
                        SingletonInformation.getInstance().getOptionControl());
            }
        }
        spGraphType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                optionGraphic = position;
                init = 1;

                switch (optionGraphic) {
                    case 0:
                        scriptUpdatePlot();

                        xyPlot.setDomainLabel("x (m)");
                        xyPlot.setDomainStep(XYStepMode.SUBDIVIDE, 25);
                        xyPlot.setDomainValueFormat(new DecimalFormat("#.##"));

                        xyPlot.setRangeLabel("y (m)");
                        xyPlot.setRangeStep(XYStepMode.SUBDIVIDE, 25);
                        xyPlot.setRangeValueFormat(new DecimalFormat("#.##"));

                        xyPlot.redraw();
                        break;

                    case 1:
                        scriptUpdatePlot();

                        xyPlot.setDomainLabel("tempo (s)");
                        xyPlot.setDomainStep(XYStepMode.SUBDIVIDE, 25);
                        xyPlot.setDomainValueFormat(new DecimalFormat("#"));

                        xyPlot.setRangeLabel("x (m)");
                        xyPlot.setRangeStep(XYStepMode.SUBDIVIDE, 25);
                        xyPlot.setRangeValueFormat(new DecimalFormat("#.##"));

                        xyPlot.redraw();
                        break;

                    case 2:
                        scriptUpdatePlot();

                        xyPlot.setDomainLabel("tempo (s)");
                        xyPlot.setDomainStep(XYStepMode.SUBDIVIDE, 25);
                        xyPlot.setDomainValueFormat(new DecimalFormat("#"));

                        xyPlot.setRangeLabel("y (m)");
                        xyPlot.setRangeStep(XYStepMode.SUBDIVIDE, 25);
                        xyPlot.setRangeValueFormat(new DecimalFormat("#.##"));

                        xyPlot.redraw();
                        break;

                    case 3:
                        scriptUpdatePlot();

                        xyPlot.setDomainLabel("tempo (s)");
                        xyPlot.setDomainStep(XYStepMode.SUBDIVIDE, 25);
                        xyPlot.setDomainValueFormat(new DecimalFormat("#"));

                        xyPlot.setRangeLabel("Angulo (graus)");
                        xyPlot.setRangeStep(XYStepMode.SUBDIVIDE, 25);
                        xyPlot.setRangeValueFormat(new DecimalFormat("#.##"));

                        xyPlot.redraw();
                        break;

                    case 4:
                        scriptUpdatePlot();

                        xyPlot.setDomainLabel("tempo (s)");
                        xyPlot.setDomainStep(XYStepMode.SUBDIVIDE, 25);
                        xyPlot.setDomainValueFormat(new DecimalFormat("#"));

                        xyPlot.setRangeLabel("v (cm/s)");
                        xyPlot.setRangeStep(XYStepMode.SUBDIVIDE, 25);
                        xyPlot.setRangeValueFormat(new DecimalFormat("#.##"));

                        xyPlot.redraw();
                        break;

                    case 5:
                        scriptUpdatePlot();

                        xyPlot.setDomainLabel("tempo (s)");
                        xyPlot.setDomainStep(XYStepMode.SUBDIVIDE, 25);
                        xyPlot.setDomainValueFormat(new DecimalFormat("#"));

                        xyPlot.setRangeLabel("w (cm/s)");
                        xyPlot.setRangeStep(XYStepMode.SUBDIVIDE, 25);
                        xyPlot.setRangeValueFormat(new DecimalFormat("#.##"));

                        xyPlot.redraw();
                        break;

                    case 6:
                        scriptUpdatePlot();

                        xyPlot.setDomainLabel("tempo (s)");
                        xyPlot.setDomainStep(XYStepMode.SUBDIVIDE, 25);
                        xyPlot.setDomainValueFormat(new DecimalFormat("#"));

                        xyPlot.setRangeLabel("erro");
                        xyPlot.setRangeStep(XYStepMode.SUBDIVIDE, 25);
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
                new CatmullRomInterpolator.Params(50, CatmullRomInterpolator.Type.Uniform));

        xyPlot.setTicksPerRangeLabel(3);
        xyPlot.setTicksPerDomainLabel(3);

        xyPlot.getGraphWidget().setDomainLabelOrientation(-45);

        draw.start();

        return view;
    }

    private void scriptUpdatePlot(){
        if(SingletonConnection.getInstance().getMoviments().size() == 0 || SingletonConnection.getInstance().getMoviments() == null) return;

        int tamVector = SingletonConnection.getInstance().getMoviments().size();;

        xyPlot.removeSeries(series);
        xyPlot.clear();

        ALdataX.clear();
        ALdataY.clear();

        if(optionChanged) {
            choiceOptionControl = 3;
        }else {
            choiceOptionControl = SingletonInformation.getInstance().getOptionControl();
        }

        switch (choiceOptionControl) {
            case 1: //AUTOMATICO
                AdataX = new float[Constants.COUNT_POINTS];
                AdataY = new float[Constants.COUNT_POINTS];
                Arrays.fill(AdataX, 0);
                Arrays.fill(AdataY, 0);
                break;

            case 2: //MANUAL
                AdataX = new float[tamVector];
                AdataY = new float[tamVector];
                Arrays.fill(AdataX, 0);
                Arrays.fill(AdataY, 0);
                break;

            case 3: //AMBOS
                AdataX = new float[Constants.COUNT_POINTS_HYBRID];
                AdataY = new float[Constants.COUNT_POINTS_HYBRID];
                Arrays.fill(AdataX, 0);
                Arrays.fill(AdataY, 0);
        }

        getDataSource();

        bounderies = getBoundaries();
        Log.i(Constants.TAG, "BOUNDERIES: " + bounderies[0] + "-" + bounderies[1] + "-" + bounderies[2] + "-" + bounderies[3]);

        xyPlot.setDomainBoundaries(bounderies[0], BoundaryMode.FIXED, bounderies[1], BoundaryMode.FIXED);
        xyPlot.setRangeBoundaries(bounderies[2], BoundaryMode.FIXED, bounderies[3], BoundaryMode.FIXED);

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

        for(int i=0; i < AdataX.length; i++){
            Log.i(Constants.TAG, "Verificando valores: "+AdataX[i]+" - "+AdataY[i]);
            ALdataX.add(AdataX[i]);
            ALdataY.add(AdataY[i]);

            series.updateData(ALdataX, ALdataY);
            xyPlot.redraw();
        }
        xyPlot.redraw();
    }

    private void getDataSource(){
        if(SingletonConnection.getInstance().getMoviments().size() <= 0) return;

        switch(choiceOptionControl){
            case 1: {
                float high = SingletonConnection.getInstance().getMoviments().get(SingletonConnection.getInstance().getMoviments().size() - 1).getT();
                float low = SingletonConnection.getInstance().getMoviments().get(0).getT();

                double interval = (double) ((high - low + 1) / Constants.BUCKET_COUNT);
                ArrayList<Position> buckets[] = new ArrayList[Constants.BUCKET_COUNT];

                for (int i = 0; i < Constants.BUCKET_COUNT; i++) {
                    buckets[i] = new ArrayList<>();
                }

                for (int i = 0; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                    buckets[(int) ((SingletonConnection.getInstance().getMoviments().get(i).getT() - low) / interval)].add(SingletonConnection.getInstance().getMoviments().get(i));
                }

                fillFirsAndLastValue();

                for (int i = 0; i < buckets.length; i++) {
                    getValuesXY(buckets[i]);
                }
            }
            break;

            case 2:{
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
            }
            break;

            case 3:{
                float high = SingletonConnection.getInstance().getMoviments().get(SingletonConnection.getInstance().getMoviments().size() - 1).getT();
                float low = SingletonConnection.getInstance().getMoviments().get(0).getT();

                double interval = (double) ((high - low + 1) / Constants.BUCKET_COUNT);
                ArrayList<Position> buckets[] = new ArrayList[Constants.BUCKET_COUNT];

                for (int i = 0; i < Constants.BUCKET_COUNT; i++) {
                    buckets[i] = new ArrayList<>();
                }

                for (int i = 0; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                    buckets[(int) ((SingletonConnection.getInstance().getMoviments().get(i).getT() - low) / interval)].add(SingletonConnection.getInstance().getMoviments().get(i));
                }

                fillFirsAndLastValue();

                for (int i = 0; i < buckets.length; i++) {
                    getValuesXY(buckets[i]);
                }
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
                    bound[0] = minValueX - Constants.INCREMENT_VALUE;
                    bound[1] = maxValueX + Constants.INCREMENT_VALUE;;

                    xDomainStep = (bound[1] - bound[0])/Constants.NUMBER_SCALE;

                    minValueY = (SingletonConnection.getInstance().getMoviments().get(0).getY()).floatValue();
                    maxValueY = (SingletonConnection.getInstance().getMoviments().get(0).getY()).floatValue();
                    for (int i = 1; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                        if (SingletonConnection.getInstance().getMoviments().get(i).getY() < minValueY) {
                            minValueY = (SingletonConnection.getInstance().getMoviments().get(i).getY()).floatValue();
                        } else if (SingletonConnection.getInstance().getMoviments().get(i).getY() > maxValueY) {
                            maxValueY = (SingletonConnection.getInstance().getMoviments().get(i).getY()).floatValue();
                        }
                    }
                    bound[2] = minValueY - Constants.INCREMENT_VALUE;
                    bound[3] = maxValueY + Constants.INCREMENT_VALUE;

                    yRangeStep = ((bound[3] - bound[2])/Constants.NUMBER_SCALE);

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
                    bound[0] = minValueX - Constants.INCREMENT_VALUE;
                    bound[1] = maxValueX + Constants.INCREMENT_VALUE;

                    xDomainStep = ((bound[1] - bound[0])/Constants.NUMBER_SCALE);

                    minValueY = SingletonConnection.getInstance().getMoviments().get(0).getX().floatValue();
                    maxValueY = SingletonConnection.getInstance().getMoviments().get(0).getX().floatValue();
                    for (int i = 1; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                        if (SingletonConnection.getInstance().getMoviments().get(i).getX() < minValueY) {
                            minValueY = SingletonConnection.getInstance().getMoviments().get(i).getX().floatValue();
                        } else if (SingletonConnection.getInstance().getMoviments().get(i).getX() > maxValueY) {
                            maxValueY = SingletonConnection.getInstance().getMoviments().get(i).getX().floatValue();
                        }
                    }
                    bound[2] = minValueY - Constants.INCREMENT_VALUE;
                    bound[3] = maxValueY + Constants.INCREMENT_VALUE;

                    yRangeStep = ((bound[3] - bound[2])/Constants.NUMBER_SCALE);

                    break;
                case 2:
                    minValueX = SingletonConnection.getInstance().getMoviments().get(0).getT();
                    maxValueX = SingletonConnection.getInstance().getMoviments().get(0).getT();
                    for (int i = 1; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                        if (SingletonConnection.getInstance().getMoviments().get(i).getT() < minValueX) {
                            minValueX = SingletonConnection.getInstance().getMoviments().get(i).getT();
                        } else if (SingletonConnection.getInstance().getMoviments().get(i).getT() > maxValueX) {
                            maxValueX = SingletonConnection.getInstance().getMoviments().get(i).getT();
                        }
                    }
                    bound[0] = minValueX - Constants.INCREMENT_VALUE;
                    bound[1] = maxValueX + Constants.INCREMENT_VALUE;

                    xDomainStep = ((bound[1] - bound[0])/Constants.NUMBER_SCALE);

                    minValueY = SingletonConnection.getInstance().getMoviments().get(0).getY().floatValue();
                    maxValueY = SingletonConnection.getInstance().getMoviments().get(0).getY().floatValue();
                    for (int i = 1; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                        if (SingletonConnection.getInstance().getMoviments().get(i).getY() < minValueY) {
                            minValueY = SingletonConnection.getInstance().getMoviments().get(i).getY().floatValue();
                        } else if (SingletonConnection.getInstance().getMoviments().get(i).getY() > maxValueY) {
                            maxValueY = SingletonConnection.getInstance().getMoviments().get(i).getY().floatValue();
                        }
                    }
                    bound[2] = minValueY - Constants.INCREMENT_VALUE;
                    bound[3] = maxValueY + Constants.INCREMENT_VALUE;

                    yRangeStep = ((bound[3] - bound[2])/Constants.NUMBER_SCALE);

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
                    bound[0] = minValueX - Constants.INCREMENT_VALUE;
                    bound[1] = maxValueX + Constants.INCREMENT_VALUE;

                    xDomainStep = ((bound[1] - bound[0])/Constants.NUMBER_SCALE);

                    minValueY = SingletonConnection.getInstance().getMoviments().get(0).getTheta().floatValue();
                    maxValueY = SingletonConnection.getInstance().getMoviments().get(0).getTheta().floatValue();
                    for (int i = 1; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                        if (SingletonConnection.getInstance().getMoviments().get(i).getTheta() < minValueY) {
                            minValueY = SingletonConnection.getInstance().getMoviments().get(i).getTheta().floatValue();
                        } else if (SingletonConnection.getInstance().getMoviments().get(i).getTheta() > maxValueY) {
                            maxValueY = SingletonConnection.getInstance().getMoviments().get(i).getTheta().floatValue();
                        }
                    }
                    bound[2] = minValueY - Constants.INCREMENT_VALUE;
                    bound[3] = maxValueY + Constants.INCREMENT_VALUE;

                    yRangeStep = ((bound[3] - bound[2])/Constants.NUMBER_SCALE);

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
                    bound[0] = minValueX - Constants.INCREMENT_VALUE;
                    bound[1] = maxValueX + Constants.INCREMENT_VALUE;

                    xDomainStep = ((bound[1] - bound[0])/Constants.NUMBER_SCALE);

                    minValueY = SingletonConnection.getInstance().getMoviments().get(0).getV().floatValue();
                    maxValueY = SingletonConnection.getInstance().getMoviments().get(0).getV().floatValue();
                    for (int i = 1; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                        if (SingletonConnection.getInstance().getMoviments().get(i).getV() < minValueY) {
                            minValueY = SingletonConnection.getInstance().getMoviments().get(i).getV().floatValue();
                        } else if (SingletonConnection.getInstance().getMoviments().get(i).getV() > maxValueY) {
                            maxValueY = SingletonConnection.getInstance().getMoviments().get(i).getV().floatValue();
                        }
                    }
                    bound[2] = minValueY - Constants.INCREMENT_VALUE;
                    bound[3] = maxValueY + Constants.INCREMENT_VALUE;

                    yRangeStep = ((bound[3] - bound[2])/Constants.NUMBER_SCALE);

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
                    bound[0] = minValueX - Constants.INCREMENT_VALUE;
                    bound[1] = maxValueX + Constants.INCREMENT_VALUE;

                    xDomainStep = ((bound[1] - bound[0])/Constants.NUMBER_SCALE);

                    minValueY = SingletonConnection.getInstance().getMoviments().get(0).getW().floatValue();
                    maxValueY = SingletonConnection.getInstance().getMoviments().get(0).getW().floatValue();
                    for (int i = 1; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                        if (SingletonConnection.getInstance().getMoviments().get(i).getW() < minValueY) {
                            minValueY = SingletonConnection.getInstance().getMoviments().get(i).getW().floatValue();
                        } else if (SingletonConnection.getInstance().getMoviments().get(i).getW() > maxValueY) {
                            maxValueY = SingletonConnection.getInstance().getMoviments().get(i).getW().floatValue();
                        }
                    }
                    bound[2] = minValueY - Constants.INCREMENT_VALUE;
                    bound[3] = maxValueY + Constants.INCREMENT_VALUE;

                    yRangeStep = ((bound[3] - bound[2])/Constants.NUMBER_SCALE);

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
                    bound[0] = minValueX - Constants.INCREMENT_VALUE;
                    bound[1] = maxValueX + Constants.INCREMENT_VALUE;

                    xDomainStep = ((bound[1] - bound[0])/Constants.NUMBER_SCALE);

                    minValueY = SingletonConnection.getInstance().getMoviments().get(0).getE().floatValue();
                    maxValueY = SingletonConnection.getInstance().getMoviments().get(0).getE().floatValue();
                    for (int i = 1; i < SingletonConnection.getInstance().getMoviments().size(); i++) {
                        if (SingletonConnection.getInstance().getMoviments().get(i).getE() < minValueY) {
                            minValueY = SingletonConnection.getInstance().getMoviments().get(i).getE().floatValue();
                        } else if (SingletonConnection.getInstance().getMoviments().get(i).getE() > maxValueY) {
                            maxValueY = SingletonConnection.getInstance().getMoviments().get(i).getE().floatValue();
                        }
                    }
                    bound[2] = minValueY - Constants.INCREMENT_VALUE;
                    bound[3] = maxValueY + Constants.INCREMENT_VALUE;

                    yRangeStep = ((bound[3] - bound[2])/Constants.NUMBER_SCALE);

                    break;
            }
        }
        return bound;
    }

    private void fillFirsAndLastValue() {
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
        int interval = (bucket.size() + 1)/Constants.COUNT_VALUES;

        switch (optionGraphic) {
            case 0:{
                switch (bucket.size()) {
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
                        AdataX[init] = (bucket.get(interval * 2).getX()).floatValue();
                        AdataY[init] = (bucket.get(interval * 2).getY()).floatValue();
                        init += 1;
                }
            }
            break;

            case 1:{
                switch (bucket.size()) {
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
                        AdataX[init] = (bucket.get(interval * 2).getT()).floatValue();
                        AdataY[init] = (bucket.get(interval * 2).getX()).floatValue();
                        init += 1;
                }
            }
            break;

            case 2:{
                switch (bucket.size()) {
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
                        AdataX[init] = (bucket.get(interval * 2).getT()).floatValue();
                        AdataY[init] = (bucket.get(interval * 2).getY()).floatValue();
                        init += 1;
                }
            }
            break;

            case 3:{
                switch (bucket.size()) {
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
                        AdataX[init] = (bucket.get(interval * 2).getT()).floatValue();
                        AdataY[init] = (bucket.get(interval * 2).getTheta()).floatValue();
                        init += 1;
                }
            }
            break;

            case 4:{
                switch (bucket.size()) {
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
                        AdataX[init] = (bucket.get(interval * 2).getT()).floatValue();
                        AdataY[init] = (bucket.get(interval * 2).getV()).floatValue();
                        init += 1;
                }
            }
            break;

            case 5:{
                switch (bucket.size()) {
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
                        AdataX[init] = (bucket.get(interval * 2).getT()).floatValue();
                        AdataY[init] = (bucket.get(interval * 2).getW()).floatValue();
                        init += 1;
                }
            }
            break;

            case 6: {
                switch (bucket.size()) {
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
                        AdataX[init] = (bucket.get(interval * 2).getT()).floatValue();
                        AdataY[init] = (bucket.get(interval * 2).getE()).floatValue();
                        init += 1;
                }
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

    private class DrawGraphic extends Thread {

        public DrawGraphic(){}

        public void run(){
            try {
                drawSemaphore.acquire();

                while(updateGraphicControl){
                    try {
                        Thread.sleep(1000);
                        init = 1;
                        scriptUpdatePlot();

                    } catch (InterruptedException e) {e.printStackTrace();}
                }

                drawSemaphore.release();
            } catch (InterruptedException e) {}
        }
    }

    @Override
    public void onDestroy() {
        LocalBroadcastManager.getInstance(getContext()).unregisterReceiver(mMessageReceiver);
        updateGraphicControl = false;
        super.onDestroy();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
