package br.com.bitmine.sttool.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import br.com.bitmine.sttool.MainActivity;
import br.com.bitmine.sttool.R;
import br.com.bitmine.sttool.entities.Magnitude;
import br.com.bitmine.sttool.entities.MeasureSet;
import br.com.bitmine.sttool.utils.Constants;

/**
 * This class represents the first data processing step, where the user can put all data.
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class FirstStepFragment extends Fragment {

    private EditText magnitudeName1;
    private EditText numberOfSamples1;
    private EditText magnitudeName2;
    private EditText numberOfSamples2;

    private RadioGroup uncertaintyTypeGroup1;
    private EditText equipOffset1;
    private EditText equipPercent1;
    private EditText equipVar1;

    private RadioGroup uncertaintyTypeGroup2;
    private EditText equipOffset2;
    private EditText equipPercent2;
    private EditText equipVar2;

    private RadioGroup fixedGroup;

    private Magnitude mag1 = new Magnitude();
    private Magnitude mag2 = new Magnitude();

    /**
     * Constructor method.
     * @return - Returns a new instance of first step fragment.
     */
    public static FirstStepFragment newInstance(){
        FirstStepFragment result = new FirstStepFragment();

        return result;
    }

    /**
     * Constructor method.
     * @param mag1 - Magnitude 1.
     * @param mag2 - Magnitude 2.
     * @return - Returns a new instance of first step fragment.
     */
    public static FirstStepFragment newInstance(Magnitude mag1, Magnitude mag2){
        FirstStepFragment result = new FirstStepFragment();
        result.mag1 = (mag1 == null? new Magnitude() : mag1);
        result.mag2 = (mag2 == null? new Magnitude() : mag2);
        return result;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_first, null);

        ((MainActivity) getActivity()).getSupportActionBar().setTitle(getString(R.string.app_name));

        magnitudeName1 = (EditText) rootView.findViewById(R.id.first_magnitude_1);
        numberOfSamples1 = (EditText) rootView.findViewById(R.id.first_number_of_samples_mg1);
        magnitudeName2 = (EditText) rootView.findViewById(R.id.first_magnitude_2);
        numberOfSamples2 = (EditText) rootView.findViewById(R.id.first_number_of_samples_mg2);

        fixedGroup = (RadioGroup) rootView.findViewById(R.id.first_fixed_magnitudes);

        equipOffset1 = (EditText) rootView.findViewById(R.id.first_measurement_equip_offset_1);
        equipPercent1 = (EditText) rootView.findViewById(R.id.first_measurement_equip_percent_1);
        equipVar1 = (EditText) rootView.findViewById(R.id.first_measurement_equip_var_1);
        uncertaintyTypeGroup1 = (RadioGroup) rootView.findViewById(R.id.first_uncertainty_type_1);

        equipOffset2 = (EditText) rootView.findViewById(R.id.first_measurement_equip_offset_2);
        equipPercent2 = (EditText) rootView.findViewById(R.id.first_measurement_equip_percent_2);
        equipVar2 = (EditText) rootView.findViewById(R.id.first_measurement_equip_var_2);
        uncertaintyTypeGroup2 = (RadioGroup) rootView.findViewById(R.id.first_uncertainty_type_2);

        loadPreviousValues(rootView);

        uncertaintyTypeGroup1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.first_type_constant_1:
                        equipPercent1.setText("0");
                        equipPercent1.setEnabled(false);
                        equipVar1.setText("0");
                        equipVar1.setEnabled(false);
                        break;
                    case R.id.first_type_measurement_dp_1:
                        equipVar1.setText("0");
                        equipVar1.setEnabled(false);
                        break;
                    case R.id.first_type_scale_dp_1:
                        equipPercent1.setEnabled(true);
                        equipVar1.setEnabled(true);
                        break;
                }
            }
        });

        uncertaintyTypeGroup2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.first_type_constant_2:
                        equipPercent2.setText("0");
                        equipPercent2.setEnabled(false);
                        equipVar2.setText("0");
                        equipVar2.setEnabled(false);
                        break;
                    case R.id.first_type_measurement_dp_2:
                        equipVar2.setText("0");
                        equipVar2.setEnabled(false);
                        break;
                    case R.id.first_type_scale_dp_2:
                        equipPercent2.setEnabled(true);
                        equipVar2.setEnabled(true);
                        break;
                }
            }
        });

        rootView.findViewById(R.id.first_next_step).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyAndSetValues();
            }
        });
        return rootView;
    }

    /**
     * Loads previous magnitude values.
     * @param rootView - The root view of this fragment.
     */
    private void loadPreviousValues(View rootView) {

        magnitudeName1.setText(mag1.getName());
        if (mag1 != null && mag1.getNumberOfSamples() > 0)
            numberOfSamples1.setText(Integer.toString(mag1.getNumberOfSamples()));
        magnitudeName2.setText(mag2.getName());

        if (mag2 != null && mag2.getNumberOfSamples() > 0)
            numberOfSamples2.setText(Integer.toString(mag2.getNumberOfSamples()));
        if (mag2 != null && mag2.getName() != null)
            ((RadioButton) rootView.findViewById(mag2.isFixed() ? R.id.first_measured_magnitude_1 : R.id.first_measured_magnitude_2)).setChecked(true);


        double eqOffset = mag1.getUncertaintyOffset();
        equipOffset1.setText(eqOffset == 0 ? "" : Double.toString(eqOffset));

        double eqPercent = mag1.getUncertaintyPercent();
        equipPercent1.setText(eqPercent == 0 ? "" : Double.toString(eqPercent));

        double eqVar = mag1.getUncertaintyVar();
        equipVar1.setText(eqVar == 0 ? "" : Double.toString(eqVar));


        eqOffset = mag2.getUncertaintyOffset();
        equipOffset2.setText(eqOffset == 0 ? "" : Double.toString(eqOffset));

        eqPercent = mag2.getUncertaintyPercent();
        equipPercent2.setText(eqPercent == 0 ? "" : Double.toString(eqPercent));

        eqVar = mag2.getUncertaintyVar();
        equipVar2.setText(eqVar == 0 ? "" : Double.toString(eqVar));


        int type = mag1.getUncertaintyType();
        int typeId;
        switch (type) {
            default:
                typeId = R.id.first_type_constant_1;
                break;
            case MeasureSet.TYPE_MEASURE_DEPENDENT:
                typeId = R.id.first_type_measurement_dp_1;
                break;
            case MeasureSet.TYPE_SCALE_DEPENDENT:
                typeId = R.id.first_type_scale_dp_1;
                break;
        }
        ((RadioButton) rootView.findViewById(typeId)).setChecked(true);

        type = mag2.getUncertaintyType();
        switch (type) {
            default:
                typeId = R.id.first_type_constant_2;
                break;
            case MeasureSet.TYPE_MEASURE_DEPENDENT:
                typeId = R.id.first_type_measurement_dp_2;
                break;
            case MeasureSet.TYPE_SCALE_DEPENDENT:
                typeId = R.id.first_type_scale_dp_2;
                break;
        }
        ((RadioButton) rootView.findViewById(typeId)).setChecked(true);
    }

    /**
     * Verifies and set values. After, this proceed to the next step.
     */
    private void verifyAndSetValues(){

        String name1 = magnitudeName1.getText().toString();
        if (name1 == null || name1.length() == 0) {
            Toast.makeText(getActivity(), getString(R.string.first_invalid_name), Toast.LENGTH_SHORT).show();
            return;
        }
        mag1.setName(name1);

        try {
            int number1 = Integer.parseInt(numberOfSamples1.getText().toString());
            if (number1 > Constants.MAX_NUMBER_OF_SAMPLES || number1 < Constants.MIN_NUMBER_OF_SAMPLES) {
                throw new Exception();
            }
            mag1.setNumberOfSamples(number1);
        } catch (Exception e) {
            Toast.makeText(getActivity(), getString(R.string.first_number_of_samples_greater_than_max), Toast.LENGTH_SHORT).show();
            return;
        }

        String name2 = magnitudeName2.getText().toString();
        if (name2 == null || name2.length() == 0) {
            Toast.makeText(getActivity(), getString(R.string.first_invalid_name), Toast.LENGTH_SHORT).show();
            return;
        }
        mag2.setName(name2);

        try {
            int number2 = Integer.parseInt(numberOfSamples2.getText().toString());
            if (number2 > Constants.MAX_NUMBER_OF_SAMPLES || number2 < Constants.MIN_NUMBER_OF_SAMPLES) {
                throw new Exception();
            }
            mag2.setNumberOfSamples(number2);
        } catch (Exception e) {
            Toast.makeText(getActivity(), getString(R.string.first_number_of_samples_greater_than_max), Toast.LENGTH_SHORT).show();
            return;
        }

        int fixedId = fixedGroup.getCheckedRadioButtonId();
        switch (fixedId) {
            case R.id.first_measured_magnitude_1:
                mag1.setIsFixed(false);
                mag2.setIsFixed(true);
                break;
            case R.id.first_measured_magnitude_2:
                mag1.setIsFixed(true);
                mag2.setIsFixed(false);
                break;
        }

        try {
            int type;
            switch (uncertaintyTypeGroup1.getCheckedRadioButtonId()) {
                default:
                case R.id.first_type_constant_1:
                    type = MeasureSet.TYPE_CONSTANT;
                    break;
                case R.id.first_type_measurement_dp_1:
                    type = MeasureSet.TYPE_MEASURE_DEPENDENT;
                    break;
                case R.id.first_type_scale_dp_1:
                    type = MeasureSet.TYPE_SCALE_DEPENDENT;
                    break;
            }

            double offset = Double.parseDouble(equipOffset1.getText().toString());

            double percent;
            if (type == MeasureSet.TYPE_CONSTANT) {
                percent = 0;
            } else {
                percent = Double.parseDouble(equipPercent1.getText().toString());
            }

            double var;
            if (type == MeasureSet.TYPE_CONSTANT) {
                var = 0;
            } else {
                var = Double.parseDouble(equipVar1.getText().toString());
            }


            mag1.setUncertaintyOffset(offset);
            mag1.setUncertaintyPercent(percent);
            mag1.setUncertaintyVar(var);
            mag1.setUncertaintyType(type);


            switch (uncertaintyTypeGroup2.getCheckedRadioButtonId()) {
                default:
                case R.id.first_type_constant_2:
                    type = MeasureSet.TYPE_CONSTANT;
                    break;
                case R.id.first_type_measurement_dp_2:
                    type = MeasureSet.TYPE_MEASURE_DEPENDENT;
                    break;
                case R.id.first_type_scale_dp_2:
                    type = MeasureSet.TYPE_SCALE_DEPENDENT;
                    break;
            }

            offset = Double.parseDouble(equipOffset2.getText().toString());

            if (type == MeasureSet.TYPE_CONSTANT) {
                percent = 0;
            } else {
                percent = Double.parseDouble(equipPercent2.getText().toString());
            }

            if (type == MeasureSet.TYPE_CONSTANT) {
                var = 0;
            } else {
                var = Double.parseDouble(equipVar2.getText().toString());
            }

            mag2.setUncertaintyOffset(offset);
            mag2.setUncertaintyPercent(percent);
            mag2.setUncertaintyVar(var);
            mag2.setUncertaintyType(type);

            ((MainActivity) getActivity()).callFragment(Constants.SECOND_STEP_FRAGMENT, mag1, mag2);
        } catch (Exception e) {
            Toast.makeText(getActivity(), getString(R.string.first_invalid_uncertainty_values), Toast.LENGTH_SHORT).show();
            return;
        }
    }
}