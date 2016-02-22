package br.com.bitmine.sttool.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import br.com.bitmine.sttool.MainActivity;
import br.com.bitmine.sttool.R;
import br.com.bitmine.sttool.entities.Magnitude;
import br.com.bitmine.sttool.entities.MeasureSet;
import br.com.bitmine.sttool.utils.Constants;

/**
 * This class represents the seconds data processing step, where the user can inform all fixed values.
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class SecondStepFragment extends Fragment implements BackKeyHandleFragment {

    private Magnitude mag1 = new Magnitude();
    private Magnitude mag2 = new Magnitude();
    private int count = 0;
    private LinearLayout list;

    /**
     * Constructor method.
     * @param mag1 - Magnitude 1.
     * @param mag2 - Magnitude 2.
     * @return - Instance of the second data processing step fragment.
     */
    public static SecondStepFragment newInstance(Magnitude mag1, Magnitude mag2){
        SecondStepFragment result = new SecondStepFragment();
        result.mag1 = (mag1 == null? new Magnitude() : mag1);
        result.mag2 = (mag2 == null? new Magnitude() : mag2);
        return result;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_second, null);

        if(mag1.isFixed()) {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.second_samples_of) + " " + mag1.getName());
            count = mag1.getNumberOfSamples();
        }else {
            ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.second_samples_of) + " " + mag2.getName());
            count = mag2.getNumberOfSamples();
        }

        list = (LinearLayout) rootView.findViewById(R.id.second_list);
        list.removeAllViews();

        for(int i = 0; i < count; i++){
            LinearLayout item = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.second_list_item, null);
            item.setId(10001 + i);
            String name = (mag1.isFixed()? mag1.getName() : mag2.getName());
            ((TextView) item.findViewById(R.id.second_list_desc)).setText(name + " " + (i + 1) + ": ");

            try {
                double value = (mag1.isFixed() ? mag1.getMeasureSet(0).getValue(i) : mag2.getMeasureSet(0).getValue(i));
                if (value != 0)
                    ((EditText) item.findViewById(R.id.second_list_value)).setText(Double.toString(value));
            }catch(Exception e){} // Verify if exists previous values.

            list.addView(item);
        }

        rootView.findViewById(R.id.second_insert_next_sample_set).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    MeasureSet fixedSet = new MeasureSet();
                    for (int i = 0; i < count; i++) {
                        String value = ((EditText) list.findViewById(10001 + i).findViewById(R.id.second_list_value)).getText().toString();
                        fixedSet.add(Double.parseDouble(value));
                    }
                    if (mag1.isFixed()) {
                        mag1.addMeasureSet(fixedSet);
                    } else {
                        mag2.addMeasureSet(fixedSet);
                    }
                    ((MainActivity) getActivity()).callFragment(Constants.THIRD_STEP_FRAGMENT, mag1, mag2);
                } catch (Exception e) {
                    Toast.makeText(getActivity(), getString(R.string.second_you_need_all_samples), Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }

    @Override
    public boolean onBackPressed() {
        ((MainActivity)getActivity()).callFragment(Constants.FIRST_STEP_FRAGMENT, mag1, mag2);
        return true;
    }
}
