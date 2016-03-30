package com.example.guilherme.tcc_1_4.Fragments;


import android.bluetooth.BluetoothDevice;
import android.graphics.DashPathEffect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.CatmullRomInterpolator;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.PointLabelFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;
import com.example.guilherme.tcc_1_4.Model.Position;
import com.example.guilherme.tcc_1_4.R;
import com.example.guilherme.tcc_1_4.Utils.Constants;

import java.util.Arrays;
import java.util.List;

public class GraphFragment extends Fragment{

    private BluetoothDevice mDevice;
    private List<Position> moviments;

    private Spinner spGraphType;
    private XYPlot plot;

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

        spGraphType = (Spinner) view.findViewById(R.id.spGraphs);
        spGraphType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            //Log.i(Constants.TAG, "Position: "+position + "id: "+id);
                switch (position){
                    case 0:
                        plot.setTitle("Percurso");
                        break;
                    case 1:
                        plot.setTitle("Posição X x Tempo");
                        break;
                    case 2:
                        plot.setTitle("Posição Y x Tempo");
                        break;
                    case 3:
                        plot.setTitle("Theta x Tempo");
                        break;
                    case 4:
                        plot.setTitle("Velocidade Linear x Tempo");
                        break;
                    case 5:
                        plot.setTitle("Velocidade Angular x Tempo");
                        break;
                    case 6:
                        plot.setTitle("Erro de posição x Tempo");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        plot = (XYPlot) view.findViewById(R.id.plot);
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
        series1Format.configure(getActivity().getApplicationContext(),
                R.xml.line_point_formatter_with_label);

        LineAndPointFormatter series2Format = new LineAndPointFormatter();
        series2Format.setPointLabelFormatter(new PointLabelFormatter());
        series2Format.configure(getActivity().getApplicationContext(),
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

        return view;
    }
}
