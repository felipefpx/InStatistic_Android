package br.com.bitmine.sttool.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import br.com.bitmine.sttool.MainActivity;
import br.com.bitmine.sttool.R;
import br.com.bitmine.sttool.entities.Magnitude;
import br.com.bitmine.sttool.entities.MeasureSet;
import br.com.bitmine.sttool.utils.Constants;
import br.com.bitmine.sttool.utils.StatisticUtils;

/**
 * This class represents the third data processing step, where the user informs all measured values.
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class ThirdStepFragment extends Fragment implements BackKeyHandleFragment, View.OnClickListener {

    private Magnitude mag1 = new Magnitude();
    private Magnitude mag2 = new Magnitude();
    private GridLayout table;
    private Button analyse;

    private int rowCount = 0;
    private int columnCount = 0;

    private View rootView;

    private Magnitude measuredMag;
    private Magnitude fixedMag;

    public static ThirdStepFragment newInstance(Magnitude mag1, Magnitude mag2){
        ThirdStepFragment result = new ThirdStepFragment();
        result.mag1 = (mag1 == null? new Magnitude() : mag1);
        result.mag2 = (mag2 == null? new Magnitude() : mag2);
        return result;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_third, null);

        if(mag1.isFixed()){
            fixedMag = mag1;
            measuredMag = mag2;
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.second_samples_of) + " " + mag2.getName());
        }else{
            fixedMag = mag2;
            measuredMag = mag1;
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.second_samples_of) + " " + mag1.getName());
        }


        table = (GridLayout) rootView.findViewById(R.id.third_table);
        rowCount = (mag1.isFixed()? mag1.getNumberOfSamples(): mag2.getNumberOfSamples());
        columnCount = (!mag1.isFixed()? mag1.getNumberOfSamples(): mag2.getNumberOfSamples());

        analyse = (Button) rootView.findViewById(R.id.third_analyse);
        analyse.setOnClickListener(this);

        table.removeAllViews();
        table.setColumnCount(columnCount + 1);

        MeasureSet fixedSet = fixedMag.getMeasureSet(0);
        MeasureSet measureSet;

        for(int y = 0; y <= rowCount; y++){

            measureSet = null;
            if(measuredMag.numberOfCompletedMeasures() > 0 && y > 0){
                measureSet = measuredMag.getMeasureSet(y-1);
            }

            for(int x = 0; x <= columnCount; x++){
                FrameLayout item = (FrameLayout) LayoutInflater.from(getActivity()).inflate(R.layout.third_list_item, null);
                item.setId(Integer.parseInt("10" + x + "00" + y));

                if(x == 0 && y == 0){
                    TextView desc = (TextView) item.findViewById(R.id.third_list_desc);
                    desc.setText(fixedMag.getName().charAt(0) + "\\" + measuredMag.getName().charAt(0));
                    desc.setTextSize(14);
                    item.findViewById(R.id.third_list_value).setVisibility(View.GONE);
                }else if(y == 0 && x > 0){
                    ((TextView) item.findViewById(R.id.third_list_desc)).setText(Integer.toString(x));
                    item.findViewById(R.id.third_list_value).setVisibility(View.GONE);
                }else if(x == 0 && y > 0){
                    ((TextView) item.findViewById(R.id.third_list_desc)).setText(Double.toString(fixedSet.getValue(y-1)));
                    item.findViewById(R.id.third_list_value).setVisibility(View.GONE);
                }else if(measureSet != null && measureSet.size() > (x - 1)){
                    ((EditText) item.findViewById(R.id.third_list_value)).setText(Double.toString(measureSet.getValue(x-1)));
                }
                table.addView(item);
            }

        }

        return rootView;
    }

    @Override
    public boolean onBackPressed() {
        ((MainActivity)getActivity()).callFragment(Constants.SECOND_STEP_FRAGMENT, mag1, mag2);
        return true;
    }

    @Override
    public void onClick(View v) {
        if(v == analyse){
            analyseSamples();
        }
    }

    /**
     * Resets background style to remove red blocks.
     */
    private void resetBackgrounds(){
        for(int y = 1; y <= rowCount; y++){
            for(int x = 1; x <= columnCount; x++){
                rootView.findViewById(Integer.parseInt("10" + x + "00" + y)).findViewById(R.id.third_list_value).setBackgroundResource(R.drawable.white_off_shape);
            }
        }
    }

    /**
     * Analyses the sample set.
     */
    private void analyseSamples(){
        EditText editTx;

        resetBackgrounds();

        MeasureSet measureSet;
        ArrayList<View> views = new ArrayList<View>();

        for(int y = 1; y <= rowCount; y++) {
            measureSet = new MeasureSet();
            views.clear();
            measureSet.copyUncertainty(measuredMag);
            for (int x = 1; x <= columnCount; x++) {
                editTx = (EditText)  rootView.findViewById(Integer.parseInt("10" + x + "00" + y)).findViewById(R.id.third_list_value);
                views.add(editTx);

                try {
                    measureSet.add(Double.parseDouble(editTx.getText().toString()));
                } catch (Exception e) {
                    editTx.setBackgroundResource(R.drawable.red_shape);
                    Toast.makeText(getActivity(), getString(R.string.third_invalid_value), Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if (!StatisticUtils.applyChauvenet(measureSet, views)) {
                Toast.makeText(getActivity(), getString(R.string.third_chauvenet_invalid_samples), Toast.LENGTH_SHORT).show();
                return;
            }

            measuredMag.addMeasureSet(measureSet);
        }

        ((MainActivity) getActivity()).callFragment(Constants.FINAL_STEP_FRAGMENT, mag1, mag2);
    }
}
