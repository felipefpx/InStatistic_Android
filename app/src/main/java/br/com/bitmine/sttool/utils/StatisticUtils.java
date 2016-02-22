package br.com.bitmine.sttool.utils;

import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

import br.com.bitmine.sttool.R;
import br.com.bitmine.sttool.entities.MeasureSet;

/**
 * This class stores some statistical treatment methods.
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class StatisticUtils {

    public static final double MILI = 0.001;
    public static final double MICRO = 0.000001;
    public static final double PICO = 0.000000001;
    public static final double NANO = 0.000000000001;

    /**
     * Calculates the mean of a set of values.
     * @param values - Set of values.
     * @return - Mean value.
     */
    public static double mean(List<Double> values){
        double result = 0;

        if(values == null || values.size() == 0)
            return 0;

        for(Double value : values){
            result += value;
        }

        return result/values.size();
    }

    /**
     * Calculates the standard deviation of a set of values.
     */
    public static double stDeviation(List<Double> values){

        if(values == null || values.size() == 0)
            return 0;

        double quadSum = 0; // Sum of squares.
        for(Double value : values){
            quadSum += Math.pow(value, 2);
        }

        double sumQuadByN = 0; // Sum to the second, divided by N.
        for(Double value : values){
            sumQuadByN += value;
        }
        sumQuadByN = Math.pow(sumQuadByN, 2)/values.size();

        return Math.sqrt((quadSum - sumQuadByN)/(values.size() - 1));
    }

    /**
     * Gets student coefficient based in number of samples for 95% confidence.
     * @param numberOfSamples - Number of samples.
     * @return - Student coefficient.
     */
    public static double getStudentCoef_95(int numberOfSamples){
        numberOfSamples--; // Vef = number_of_samples - 1

        if(numberOfSamples <= 0)
            return Double.MAX_VALUE;
        else if (numberOfSamples > 100)
            return 2.00;
        else if(numberOfSamples > 60 && numberOfSamples <= 80)
            return 2.03;
        else if(numberOfSamples > 50 && numberOfSamples <= 60)
            return 2.04;
        else if(numberOfSamples > 45 && numberOfSamples <= 50)
            return 2.05;
        else if(numberOfSamples > 35 && numberOfSamples <= 45)
            return 2.06;
        else if(numberOfSamples > 30 && numberOfSamples <= 35)
            return 2.07;
        else if(numberOfSamples > 25 && numberOfSamples <= 30)
            return 2.09;
        else if(numberOfSamples > 20 && numberOfSamples <= 25)
            return 2.11;
        else if(numberOfSamples > 18 && numberOfSamples <= 20)
            return 2.13;
        else if(numberOfSamples > 16 && numberOfSamples <= 18)
            return 2.15;
        else if(numberOfSamples > 14 && numberOfSamples <= 16)
            return 2.17;
        else if(numberOfSamples > 12 && numberOfSamples <= 14)
            return 2.20;
        else if(numberOfSamples > 10 && numberOfSamples <= 12)
            return 2.23;
        else if(numberOfSamples > 8 && numberOfSamples <= 10)
            return 2.28;
        else
            return new double[]{13.97, 4.53, 3.31, 2.87, 2.65, 2.52, 2.43, 2.37}[numberOfSamples];
    }

    /**
     * Gets Chauvenet Critical DS values for 95% of confidence.
     * @param numberOfSamples - Number of samples.
     * @return - Critical d/s.
     */
    public static double getCriticalDS_95(int numberOfSamples){
        double CRITICAL_DS_95[] = { 0, 0, 0, 1.54, 1.65, 1.73, 1.80, 1.85, 1.91, 1.96,
            1.99, 2.03, 2.06, 2.10, 2.13, 2.16, 2.18, 2.20, 2.22, 2.24, 2.26, 2.28, 2.30, 2.31, 2.33, 2.35, 2.36, 2.37, 2.38, 2.39};

        if(numberOfSamples <= 3)
            return 0;
        else if(numberOfSamples >= 30)
            return CRITICAL_DS_95[29];
        else
            return CRITICAL_DS_95[numberOfSamples - 1];
    }

    /**
     * Applies Chauvenet method to reject problematic samples.
     * @param measureSet - Measure set.
     * @return - Result set.
     */
    public static boolean applyChauvenet(MeasureSet measureSet, ArrayList<View> views){
        double criticalDS = getCriticalDS_95(measureSet.size());
        boolean rejectedSomething = false;

        double mean = measureSet.getMean();
        double stdDeviation = measureSet.getStdDeviation();
        Log.w("StatisticUtils", "applyChauvenet: mean: " + mean + "   stddev: " + stdDeviation);

        if(measureSet.size() < 3)
            return false;

        if(stdDeviation == 0)
            return true;

        for(int i = 0; i < measureSet.size(); i++){
            double r = Math.abs(measureSet.getValue(i) - mean)/stdDeviation;

            Log.w("StatisticUtils", "applyChauvenet: Index: " + i + "   r: " + r + "    critical d/s: " + criticalDS);
            if(r > criticalDS) {
                Log.w("StatisticUtils", "applyChauvenet: Sample removed! Index: " + i);
                measureSet.removeMeasure(i);
                views.get(i).setBackgroundResource(R.drawable.red_shape);
                views.remove(i);
                rejectedSomething = true;
                break;
            }
        }

        if(rejectedSomething)
            return applyChauvenet(measureSet, views);

        return true;
    }

    /**
     * Gets distribution multiplier to calculate confidence interval.
     * @param numberOfSamples - Number of samples.
     * @return - T distribution.
     */
    public static double getDistributionMultiplier(int numberOfSamples){
        double tMultipliers[] = {12.7, 3.05, 1.84, 1.39, 1.15, 1.00, 0.890, 0.816, 0.770, 0.706, 0.550, 0.466, 0.412};

        if(numberOfSamples == 0)
            return 0;
        else if(numberOfSamples > 10 && numberOfSamples <= 15)
            return tMultipliers[10];
        else if(numberOfSamples > 15 && numberOfSamples <= 20)
            return tMultipliers[11];
        else if(numberOfSamples > 0 && numberOfSamples <= 25)
            return tMultipliers[12];
        else if(numberOfSamples > 25)
            return 0;

        return tMultipliers[numberOfSamples-1];
    }


    /**
     * Calculates the product between magnitudes indirectly and propagating uncertainties.
     * @param firstMagMeasures - First magnitude measured values.
     * @param secondMagMeasures - Second magnitude measured values.
     * @return - Calculated values.
     */
    public static MeasureSet calculateProduct(MeasureSet firstMagMeasures, MeasureSet secondMagMeasures){

        MeasureSet calculatedVoltage = new MeasureSet();

        if(firstMagMeasures == null || firstMagMeasures.size() == 0 || secondMagMeasures == null || secondMagMeasures.size() == 0)
            return calculatedVoltage;

        int length = Math.min(firstMagMeasures.size(), secondMagMeasures.size());
        for(int i = 0; i < length; i++){
            double voltage = firstMagMeasures.getValue(i) * secondMagMeasures.getValue(i);
            double combinedUncertainty =
                    Math.pow(secondMagMeasures.getValue(i) * firstMagMeasures.getUncertainty(i), 2) +
                            Math.pow(firstMagMeasures.getValue(i) * secondMagMeasures.getUncertainty(i), 2);

            calculatedVoltage.add(voltage, combinedUncertainty);
        }

        return calculatedVoltage;
    }

    /**
     * Calculates the correlation coefficient between 2 magnitudes.
     * @param firstMagMeasures - First magnitude.
     * @param secondMagMeasures - Second magnitude.
     * @return - Correlation coefficient (r). { r >= 0.8: Complete correlation; 0.8 < r < 0.2: Partial correlation; r <= 0.2: No correlation }
     */
    public static double calculateCorrelationCoefficient(MeasureSet firstMagMeasures, MeasureSet secondMagMeasures){

        if(firstMagMeasures == null || secondMagMeasures == null || firstMagMeasures.size() == 0 || secondMagMeasures.size() == 0)
            return 0;

        double firstMean = firstMagMeasures.getMean();
        double secondMean = secondMagMeasures.getMean();

        double numerator = 0;
        double denominatorPart1 = 0;
        double denominatorPart2 = 0;

        int length = Math.min(firstMagMeasures.size(), secondMagMeasures.size());
        for(int i = 0; i < length; i++){
            numerator += (firstMagMeasures.getValue(i) - firstMean)*(secondMagMeasures.getValue(i) - secondMean);
            denominatorPart1 += Math.pow((firstMagMeasures.getValue(i) - firstMean), 2);
            denominatorPart2 += Math.pow((secondMagMeasures.getValue(i) - secondMean), 2);
        }

        double denominator = Math.sqrt(denominatorPart1 * denominatorPart2);
        if(denominator == 0)
            return 0;

        return numerator / denominator;
    }

    /**
     * Calculates regression coefficients.
     * @param firstMagMeasures - (X)
     * @param secondMagMeasures - (Y)
     * @return - Regression coefficients (a and b).
     */
    public static Pair<Double, Double> calculateRegressionCoefficients(MeasureSet firstMagMeasures, MeasureSet secondMagMeasures){

        if(firstMagMeasures == null || secondMagMeasures == null || firstMagMeasures.size() == 0 || secondMagMeasures.size() == 0)
            return new Pair<Double, Double>(0.0, 0.0);

        double firstMean = firstMagMeasures.getMean();
        double secondMean = secondMagMeasures.getMean();

        double numerator = 0;
        double denominator = 0;

        int length = Math.min(firstMagMeasures.size(), secondMagMeasures.size());
        for(int i = 0; i < length; i++){
            numerator += secondMagMeasures.getValue(i) * (firstMagMeasures.getValue(i) - firstMean);
            denominator += Math.pow((firstMagMeasures.getValue(i) - firstMean), 2);
        }

        if(denominator == 0)
            return new Pair<Double, Double>(0.0, 0.0);

        double b = numerator/denominator;
        double a = secondMean - (b * firstMean);
        return new Pair<Double, Double>(a, b);
    }

}

