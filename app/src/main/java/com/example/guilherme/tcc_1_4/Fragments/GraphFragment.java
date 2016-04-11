package com.example.guilherme.tcc_1_4.Fragments;


import android.bluetooth.BluetoothDevice;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class GraphFragment extends Fragment{

    private List<Position> moviments;

    private Spinner spGraphType;

    private static XYPlot xyPlot;
    private XYSeriesShimmer series;
    private LineAndPointFormatter series1Format;
    private ArrayList<Number> ALdataX, ALdataY;
    private float AdataX[], AdataY[];
    private int init = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.graph_fragment_layout, container, false);

        moviments = this.getArguments().getParcelableArrayList("moviments");

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

                init = 1;
                switch (position) {
                    case 0:
                        xyPlot.setDomainLabel("x (m)");
                        xyPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 0.05);
                        xyPlot.setDomainValueFormat(new DecimalFormat("#.##"));

                        xyPlot.setRangeLabel("y (m)");
                        xyPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 0.025);
                        xyPlot.setRangeValueFormat(new DecimalFormat("#.##"));

                        scriptUpdatePlot(0);
                        xyPlot.redraw();
                        break;

                    case 1:
                        xyPlot.setDomainLabel("tempo (s)");
                        xyPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 4.0);
                        xyPlot.setDomainValueFormat(new DecimalFormat("#"));

                        xyPlot.setRangeLabel("x (m)");
                        xyPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 0.025);
                        xyPlot.setRangeValueFormat(new DecimalFormat("#.##"));
                        scriptUpdatePlot(1);
                        xyPlot.redraw();
                        break;

                    case 2:
                        xyPlot.setDomainLabel("tempo (s)");
                        xyPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 4.0);
                        xyPlot.setDomainValueFormat(new DecimalFormat("#"));

                        xyPlot.setRangeLabel("y (m)");
                        xyPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 0.025);
                        xyPlot.setRangeValueFormat(new DecimalFormat("#.##"));
                        scriptUpdatePlot(2);
                        xyPlot.redraw();
                        break;

                    case 3:
                        xyPlot.setDomainLabel("tempo (s)");
                        xyPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 4.0);
                        xyPlot.setDomainValueFormat(new DecimalFormat("#"));

                        xyPlot.setRangeLabel("Angulo (graus)");
                        xyPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 0.5);
                        xyPlot.setRangeValueFormat(new DecimalFormat("#.##"));
                        scriptUpdatePlot(3);
                        xyPlot.redraw();
                        break;

                    case 4:
                        xyPlot.setDomainLabel("tempo (s)");
                        xyPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 4.0);
                        xyPlot.setDomainValueFormat(new DecimalFormat("#"));

                        xyPlot.setRangeLabel("v (cm/s)");
                        xyPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 0.005);
                        xyPlot.setRangeValueFormat(new DecimalFormat("#.##"));
                        scriptUpdatePlot(4);
                        xyPlot.redraw();
                        break;

                    case 5:
                        xyPlot.setDomainLabel("tempo (s)");
                        xyPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 4.0);
                        xyPlot.setDomainValueFormat(new DecimalFormat("#"));

                        xyPlot.setRangeLabel("w (cm/s)");
                        xyPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 0.5);
                        xyPlot.setRangeValueFormat(new DecimalFormat("#.##"));
                        scriptUpdatePlot(5);
                        xyPlot.redraw();
                        break;
                    case 6:
                        xyPlot.setDomainLabel("tempo (s)");
                        xyPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 4.0);
                        xyPlot.setDomainValueFormat(new DecimalFormat("#"));

                        xyPlot.setRangeLabel("erro");
                        xyPlot.setRangeStep(XYStepMode.INCREMENT_BY_VAL, 0.025);
                        xyPlot.setRangeValueFormat(new DecimalFormat("#.##"));
                        scriptUpdatePlot(6);
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

        // configure interpolation on the formatter:
        series1Format = new LineAndPointFormatter();
        series1Format.setPointLabelFormatter(new PointLabelFormatter(Color.TRANSPARENT));
        series1Format.configure(getActivity().getApplicationContext(),
                R.xml.line_point_formatter_with_label);
        series1Format.setInterpolationParams(
                new CatmullRomInterpolator.Params(20, CatmullRomInterpolator.Type.Centripetal));

        // reduce the number of range labels
        xyPlot.setTicksPerRangeLabel(3);

        // rotate domain labels 45 degrees to make them more compact horizontally:
        xyPlot.getGraphWidget().setDomainLabelOrientation(-45);

        return view;
    }

    private void scriptUpdatePlot(int option){
        xyPlot.removeSeries(series);
        xyPlot.clear();

        ALdataX.clear();
        ALdataY.clear();

        AdataX = new float[Constants.COUNT_POINTS];
        AdataY = new float[Constants.COUNT_POINTS];
        getDataSource(option);

        float[] bounderies = getBoundaries(option);
        Log.i(Constants.TAG, "BOUNDERIES: "+bounderies[0]+" - "+bounderies[1]+" - "+bounderies[2]+" - "+bounderies[3]);
        xyPlot.setDomainBoundaries(bounderies[0], bounderies[1], BoundaryMode.AUTO);
        xyPlot.setRangeBoundaries(bounderies[2], bounderies[3], BoundaryMode.AUTO);

        switch (option){
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

    private float[] getBoundaries(int option) {
        float[] bound = new float[4];

        Float minValueX;
        Float maxValueX;
        Float minValueY;
        Float maxValueY;

        switch (option){
            case 0:
                minValueX = (moviments.get(0).getX()).floatValue();
                maxValueX = (moviments.get(0).getX()).floatValue();
                for (int i = 1; i < moviments.size(); i++) {
                    if (moviments.get(i).getX() < minValueX) {
                        minValueX = (moviments.get(i).getX()).floatValue();
                    } else if (moviments.get(i).getX() > maxValueX) {
                        maxValueX = (moviments.get(i).getX()).floatValue();
                    }
                }
                bound[0] = minValueX;
                bound[1] = maxValueX;

                minValueY = (moviments.get(0).getY()).floatValue();
                maxValueY = (moviments.get(0).getY()).floatValue();
                for (int i = 1; i < moviments.size(); i++) {
                    if (moviments.get(i).getY() < minValueY) {
                        minValueY = (moviments.get(i).getY()).floatValue();
                    } else if (moviments.get(i).getY() > maxValueY) {
                        maxValueY = (moviments.get(i).getY()).floatValue();
                    }
                }
                bound[2] = minValueY;
                bound[3] = maxValueY;

                break;

            case 1:
                minValueX = moviments.get(0).getT();
                maxValueX = moviments.get(0).getT();
                for (int i = 1; i < moviments.size(); i++) {
                    if (moviments.get(i).getT() < minValueX) {
                        minValueX = moviments.get(i).getT();
                    } else if (moviments.get(i).getT() > maxValueX) {
                        maxValueX = moviments.get(i).getT();
                    }
                }
                bound[0] = minValueX;
                bound[1] = maxValueX;

                minValueY = moviments.get(0).getX().floatValue();
                maxValueY = moviments.get(0).getX().floatValue();
                for (int i = 1; i < moviments.size(); i++) {
                    if (moviments.get(i).getX() < minValueY) {
                        minValueY = moviments.get(i).getX().floatValue();
                    } else if (moviments.get(i).getX() > maxValueY) {
                        maxValueY = moviments.get(i).getX().floatValue();
                    }
                }
                bound[2] = minValueY;
                bound[3] = maxValueY;

                break;
            case 2:
                minValueX = moviments.get(0).getT();
                maxValueX = moviments.get(0).getT();
                for (int i = 1; i < moviments.size(); i++) {
                    if (moviments.get(i).getT() < minValueX) {
                        minValueY = moviments.get(i).getT();
                    } else if (moviments.get(i).getT() > maxValueX) {
                        maxValueY = moviments.get(i).getT();
                    }
                }
                bound[0] = minValueX;
                bound[1] = maxValueX;

                minValueY = moviments.get(0).getY().floatValue();
                maxValueY = moviments.get(0).getY().floatValue();
                for (int i = 1; i < moviments.size(); i++) {
                    if (moviments.get(i).getY() < minValueY) {
                        minValueY = moviments.get(i).getY().floatValue();
                    } else if (moviments.get(i).getY() > maxValueY) {
                        maxValueY = moviments.get(i).getY().floatValue();
                    }
                }
                bound[2] = minValueY;
                bound[3] = maxValueY;

                break;
            case 3:
                minValueX = moviments.get(0).getT();
                maxValueX = moviments.get(0).getT();
                for (int i = 1; i < moviments.size(); i++) {
                    if (moviments.get(i).getT() < minValueX) {
                        minValueX = moviments.get(i).getT();
                    } else if (moviments.get(i).getT() > maxValueX) {
                        maxValueX = moviments.get(i).getT();
                    }
                }
                bound[0] = minValueX;
                bound[1] = maxValueX;

                minValueY = moviments.get(0).getTheta().floatValue();
                maxValueY = moviments.get(0).getTheta().floatValue();
                for (int i = 1; i < moviments.size(); i++) {
                    if (moviments.get(i).getTheta() < minValueY) {
                        minValueY = moviments.get(i).getTheta().floatValue();
                    } else if (moviments.get(i).getTheta() > maxValueY) {
                        maxValueY = moviments.get(i).getTheta().floatValue();
                    }
                }
                bound[2] = minValueY;
                bound[3] = maxValueY;

                break;
            case 4:
                minValueX = moviments.get(0).getT();
                maxValueX = moviments.get(0).getT();
                for (int i = 1; i < moviments.size(); i++) {
                    if (moviments.get(i).getT() < minValueX) {
                        minValueX = moviments.get(i).getT();
                    } else if (moviments.get(i).getT() > maxValueX) {
                        maxValueX = moviments.get(i).getT();
                    }
                }
                bound[0] = minValueX;
                bound[1] = maxValueX;

                minValueY = moviments.get(0).getV().floatValue();
                maxValueY = moviments.get(0).getV().floatValue();
                for (int i = 1; i < moviments.size(); i++) {
                    if (moviments.get(i).getV() < minValueY) {
                        minValueY = moviments.get(i).getV().floatValue();
                    } else if (moviments.get(i).getV() > maxValueY) {
                        maxValueY = moviments.get(i).getV().floatValue();
                    }
                }
                bound[2] = minValueY;
                bound[3] = maxValueY;

                break;
            case 5:
                minValueX = moviments.get(0).getT();
                maxValueX = moviments.get(0).getT();
                for (int i = 1; i < moviments.size(); i++) {
                    if (moviments.get(i).getT() < minValueX) {
                        minValueX = moviments.get(i).getT();
                    } else if (moviments.get(i).getT() > maxValueX) {
                        maxValueX = moviments.get(i).getT();
                    }
                }
                bound[0] = minValueX;
                bound[1] = maxValueX;

                minValueY = moviments.get(0).getW().floatValue();
                maxValueY = moviments.get(0).getW().floatValue();
                for (int i = 1; i < moviments.size(); i++) {
                    if (moviments.get(i).getW() < minValueY) {
                        minValueY = moviments.get(i).getW().floatValue();
                    } else if (moviments.get(i).getW() > maxValueY) {
                        maxValueY = moviments.get(i).getW().floatValue();
                    }
                }
                bound[2] = minValueY;
                bound[3] = maxValueY;

                break;
            case 6:
                minValueX = moviments.get(0).getT();
                maxValueX = moviments.get(0).getT();
                for (int i = 1; i < moviments.size(); i++) {
                    if (moviments.get(i).getT() < minValueX) {
                        minValueX = moviments.get(i).getT();
                    } else if (moviments.get(i).getT() > maxValueX) {
                        maxValueX = moviments.get(i).getT();
                    }
                }
                bound[0] = minValueX;
                bound[1] = maxValueX;

                minValueY = moviments.get(0).getE().floatValue();
                maxValueY = moviments.get(0).getE().floatValue();
                for (int i = 1; i < moviments.size(); i++) {
                    if (moviments.get(i).getE() < minValueY) {
                        minValueY = moviments.get(i).getE().floatValue();
                    } else if (moviments.get(i).getE() > maxValueY) {
                        maxValueY = moviments.get(i).getE().floatValue();
                    }
                }
                bound[2] = minValueY;
                bound[3] = maxValueY;

                break;
        }

        return bound;
    }

    private void getDataSource(int option){
        if(moviments.size() <= 1) return;

        float high = Constants.FINAL_SECOND;
        float low = Constants.INITIAL_SECOND;
        double interval = (double) (high - low + 1)/Constants.BUCKET_COUNT;

        ArrayList<Position> buckets[] = new ArrayList[Constants.BUCKET_COUNT];
        for(int i = 0; i < Constants.BUCKET_COUNT; i++){
            buckets[i] =  new ArrayList<>();
        }

        for (int i = 0; i < moviments.size(); i++) {
            buckets[(int)((moviments.get(i).getT() - low)/interval)].add(moviments.get(i));
        }

        fillFirsAndLastValue(option);

        for(int i = 0; i < buckets.length; i++){
            Log.i(Constants.TAG, "BUCKET: " + i);
            getValuesXY(buckets[i], option);
            for(int j = 0; j < buckets[i].size(); j++){
                Log.i(Constants.TAG, "Y: "+buckets[i].get(j).getY());
            }
        }
    }

    private void fillFirsAndLastValue(int option) {
        switch (option){
            case 0:
                AdataX[0] = (moviments.get(0).getX().floatValue());
                AdataY[0] = (moviments.get(0).getY().floatValue());

                AdataX[19] = (moviments.get(moviments.size() - 1).getX().floatValue());
                AdataY[19] = (moviments.get(moviments.size() - 1).getY().floatValue());
                break;

            case 1:
                AdataX[0] = (moviments.get(0).getT().floatValue());
                AdataY[0] = (moviments.get(0).getX().floatValue());

                AdataX[19] = (moviments.get(moviments.size() - 1).getT().floatValue());
                AdataY[19] = (moviments.get(moviments.size() - 1).getX().floatValue());
                break;

            case 2:
                AdataX[0] = (moviments.get(0).getT().floatValue());
                AdataY[0] = (moviments.get(0).getY().floatValue());

                AdataX[19] = (moviments.get(moviments.size() - 1).getT().floatValue());
                AdataY[19] = (moviments.get(moviments.size() - 1).getY().floatValue());
                break;

            case 3:
                AdataX[0] = (moviments.get(0).getT().floatValue());
                AdataY[0] = (moviments.get(0).getTheta().floatValue());

                AdataX[19] = (moviments.get(moviments.size() - 1).getT().floatValue());
                AdataY[19] = (moviments.get(moviments.size() - 1).getTheta().floatValue());
                break;

            case 4:
                AdataX[0] = (moviments.get(0).getT().floatValue());
                AdataY[0] = (moviments.get(0).getV().floatValue());

                AdataX[19] = (moviments.get(moviments.size() - 1).getT().floatValue());
                AdataY[19] = (moviments.get(moviments.size() - 1).getV().floatValue());
                break;

            case 5:
                AdataX[0] = (moviments.get(0).getT().floatValue());
                AdataY[0] = (moviments.get(0).getW().floatValue());

                AdataX[19] = (moviments.get(moviments.size() - 1).getT().floatValue());
                AdataY[19] = (moviments.get(moviments.size() - 1).getW().floatValue());
                break;

            case 6:
                AdataX[0] = (moviments.get(0).getT().floatValue());
                AdataY[0] = (moviments.get(0).getE().floatValue());

                AdataX[19] = (moviments.get(moviments.size() - 1).getT().floatValue());
                AdataY[19] = (moviments.get(moviments.size() - 1).getE().floatValue());
                break;
        }
    }

    private void getValuesXY(ArrayList<Position> bucket, int option) {
        int interval = (int) ((bucket.size()-1) - 0 + 1)/Constants.COUNT_VALUES;

        switch (option){
            case 0:
                AdataX[init] = (bucket.get(interval).getX()).floatValue();
                AdataY[init] = (bucket.get(interval).getY()).floatValue();
                init += 1;
                AdataX[init] = (bucket.get(interval*2).getX()).floatValue();
                AdataY[init] = (bucket.get(interval*2).getY()).floatValue();
                init += 1;
                break;

            case 1:
                AdataX[init] = (bucket.get(interval).getT()).floatValue();
                AdataY[init] = (bucket.get(interval).getX()).floatValue();
                init += 1;
                AdataX[init] = (bucket.get(interval*2).getT()).floatValue();
                AdataY[init] = (bucket.get(interval*2).getX()).floatValue();
                init += 1;
                break;

            case 2:
                AdataX[init] = (bucket.get(interval).getT()).floatValue();
                AdataY[init] = (bucket.get(interval).getY()).floatValue();
                init += 1;
                AdataX[init] = (bucket.get(interval*2).getT()).floatValue();
                AdataY[init] = (bucket.get(interval*2).getY()).floatValue();
                init += 1;
                break;

            case 3:
                AdataX[init] = (bucket.get(interval).getT()).floatValue();
                AdataY[init] = (bucket.get(interval).getTheta()).floatValue();
                init += 1;
                AdataX[init] = (bucket.get(interval*2).getT()).floatValue();
                AdataY[init] = (bucket.get(interval*2).getTheta()).floatValue();
                init += 1;
                break;

            case 4:
                AdataX[init] = (bucket.get(interval).getT()).floatValue();
                AdataY[init] = (bucket.get(interval).getV()).floatValue();
                init += 1;
                AdataX[init] = (bucket.get(interval*2).getT()).floatValue();
                AdataY[init] = (bucket.get(interval*2).getV()).floatValue();
                init += 1;
                break;

            case 5:
                AdataX[init] = (bucket.get(interval).getT()).floatValue();
                AdataY[init] = (bucket.get(interval).getW()).floatValue();
                init += 1;
                AdataX[init] = (bucket.get(interval*2).getT()).floatValue();
                AdataY[init] = (bucket.get(interval*2).getW()).floatValue();
                init += 1;
                break;

            case 6:
                AdataX[init] = (bucket.get(interval).getT()).floatValue();
                AdataY[init] = (bucket.get(interval).getE()).floatValue();
                init += 1;
                AdataX[init] = (bucket.get(interval*2).getT()).floatValue();
                AdataY[init] = (bucket.get(interval*2).getE()).floatValue();
                init += 1;
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
}
