package br.com.bitmine.sttool.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.YAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import br.com.bitmine.sttool.MainActivity;
import br.com.bitmine.sttool.R;
import br.com.bitmine.sttool.entities.Magnitude;
import br.com.bitmine.sttool.entities.MeasureSet;
import br.com.bitmine.sttool.entities.Point;
import br.com.bitmine.sttool.utils.Constants;
import br.com.bitmine.sttool.utils.FormatUtils;
import br.com.bitmine.sttool.utils.StatisticUtils;

/**
 * This class represents the final data processing step fragment, that shows analysis results.
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class FinalStepFragment extends Fragment implements BackKeyHandleFragment, View.OnClickListener {

    private Magnitude measuredMag;
    private Magnitude fixedMag;

    private LineChart chart;
    private TextView correlationTv;
    private TextView finalReportTv;

    private Button newAnalysis;

    private TextView yLabelTx;
    private TextView xLabelTx;

    /**
     * Constructor method.
     * @param mag1 - Magnitude 1.
     * @param mag2 - Magnitude 2.
     * @return - Instance of final step fragment.
     */
    public static FinalStepFragment newInstance(Magnitude mag1, Magnitude mag2){
        FinalStepFragment result = new FinalStepFragment();

        if(mag1.isFixed()){
            result.fixedMag = mag1;
            result.measuredMag = mag2;
        }else{
            result.fixedMag = mag2;
            result.measuredMag = mag1;
        }

        if(result.fixedMag == null)
            result.fixedMag = new Magnitude();

        if(result.measuredMag == null)
            result.measuredMag = new Magnitude();

        return result;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_final, null);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));

        correlationTv = (TextView) rootView.findViewById(R.id.final_correlation_tv);
        finalReportTv = (TextView) rootView.findViewById(R.id.final_abstract);
        newAnalysis = (Button) rootView.findViewById(R.id.final_new);
        newAnalysis.setOnClickListener(this);

        xLabelTx = (TextView) rootView.findViewById(R.id.final_xlabel);
        yLabelTx = (TextView) rootView.findViewById(R.id.final_ylabel);

        xLabelTx.setText(fixedMag.getName());
        yLabelTx.setText(measuredMag.getName());

        chart = (LineChart) rootView.findViewById(R.id.final_chart);
        initChart();

        processData();

        return rootView;
    }

    @Override
    public boolean onBackPressed() {
        ((MainActivity)getActivity()).callFragment(Constants.THIRD_STEP_FRAGMENT, fixedMag, measuredMag);
        return true;
    }

    /**
     * Analysis correlation between the two magnitudes.
     */
    private void processData(){
        MeasureSet meanMeasureSet = new MeasureSet();
        meanMeasureSet.copyUncertainty(measuredMag);

        for(int i = 0; i < measuredMag.numberOfCompletedMeasures(); i++) {
            meanMeasureSet.add(measuredMag.getMeasureSet(i).getMean());
        }

        double r = StatisticUtils.calculateCorrelationCoefficient(meanMeasureSet, fixedMag.getMeasureSet(0));

        String correlationStatus = "<b>" + "</b>" + "<br>" + Double.toString(r);
        if(r >= 0.8){
            correlationStatus += " <b>(" + getString(R.string.final_strong_correlation) + ")</b>";

            Pair<Double, Double> regressionCoefficients = StatisticUtils.calculateRegressionCoefficients(meanMeasureSet, fixedMag.getMeasureSet(0));
            double a = regressionCoefficients.first;
            double b = regressionCoefficients.second;

            MeasureSet fixedSet = fixedMag.getMeasureSet(0);
            addPoint(new Point(0, (float) (a/b)));
            for(int i = 0; i < fixedSet.size(); i++){
                int x = (int) fixedSet.getValue(i);
                float y = (float) ((fixedSet.getValue(i) - a)/b);
                addPoint(new Point(x, y));
            }



            String finalReportDesc = "<b>" + getActivity().getString(R.string.final_regression_eq) + "</b><br><br><b>a</b> = " + FormatUtils.formatToScientific(a) + "<br>" +
                    "<b>b</b> = " + FormatUtils.formatToScientific(b) + "<br><br>" +
                    "<b>y = a + (b * x)</b>";
            finalReportTv.setText(Html.fromHtml(finalReportDesc));
        }else{
            correlationStatus += " <b>(" + getString(R.string.final_no_correlation) + ")</b>";
        }
        correlationTv.setText(Html.fromHtml(correlationStatus));
    }


    /**
     * Initializes the chart.
     */
    private void initChart(){
        chart.setDescription("");
        chart.setNoDataTextDescription(getString(R.string.final_no_correlation));

        chart.setTouchEnabled(false);
        chart.setDragEnabled(false);
        chart.setScaleEnabled(false);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(false);
        chart.setDoubleTapToZoomEnabled(false);
        chart.setClickable(false);

        LineData data = new LineData();
        chart.setData(data);

        Legend l = chart.getLegend();
        l.setEnabled(false);

        // - X Axis
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextSize(12f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextColor(getResources().getColor(R.color.gray));
        xAxis.setEnabled(true);
        xAxis.disableGridDashedLine();
        xAxis.setSpaceBetweenLabels(5);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);

        // - Y Axis
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.removeAllLimitLines();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTextColor(getResources().getColor(R.color.gray));
        leftAxis.setStartAtZero(false);
        leftAxis.enableGridDashedLine(10f, 10f, 0f);
        leftAxis.setDrawLimitLinesBehindData(true);
        leftAxis.setDrawGridLines(true);
        leftAxis.setValueFormatter(new YAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, YAxis yAxis) {
                return FormatUtils.formatToScientific(value);
            }
        });

        chart.getAxisRight().setEnabled(false);
    }

    /**
     * Adds a new point.
     * @param point - Point to add.
     */
    private void addPoint(Point point) {

        LineData data = chart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }else if(set.getEntryCount() == 60){
                set.removeFirst();
            }

            Log.w("FinalStep", "(" + point.getX() + ", " + point.getY() + ")");
            // add a new x-value first
            data.addXValue(point.getX() + "");
            data.addEntry(new Entry(point.getY(), point.getX()), 0);


            // let the chart know it's data has changed
            chart.notifyDataSetChanged();
            chart.setAutoScaleMinMaxEnabled(true);

            // limit the number of visible entries
            chart.setVisibleXRangeMaximum(12);
//            chart.invalidate();
            // mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
//            chart.moveViewToX(data.getXValCount() - 121);

            // this automatically refreshes the chart (calls invalidate())
            chart.moveViewTo(data.getXValCount() - 7, 55f, YAxis.AxisDependency.LEFT);
        }
    }

    /**
     * Creates the data set.
     * @return - Line data set.
     */
    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(getResources().getColor(R.color.colorAccent));
        set.setCircleColor(getResources().getColor(R.color.colorAccent));
        set.setLineWidth(2f);
        set.setCircleRadius(0f);
        set.setFillAlpha(65);
        set.setFillColor(Color.WHITE);
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.TRANSPARENT);
        set.setValueTextSize(0f);
        set.setDrawValues(false);
        return set;
    }

    @Override
    public void onClick(View v) {
        if(v == newAnalysis){
            ((MainActivity) getActivity()).callFragment(Constants.FIRST_STEP_FRAGMENT, null, null);
        }
    }
}
