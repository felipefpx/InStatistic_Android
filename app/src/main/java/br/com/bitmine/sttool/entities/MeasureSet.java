package br.com.bitmine.sttool.entities;

import java.util.ArrayList;

import br.com.bitmine.sttool.utils.StatisticUtils;

/**
 * This class represents a set of measures.
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class MeasureSet {

    public static final int TYPE_MEASURE_DEPENDENT = 0;
    public static final int TYPE_SCALE_DEPENDENT = 1;
    public static final int TYPE_CONSTANT = 3;

    private ArrayList<Double> measuredValues = new ArrayList<Double>();
    private ArrayList<Double> cnstUncertainty = new ArrayList<Double>();
    private int uncertaintyType = TYPE_CONSTANT;
    private double uncertaintyOffset = 0;
    private double uncertaintyPercentage = 0;
    private double uncertaintyParam = 0;


    /**
     * Constructor method.
     */
    public MeasureSet(){}

    /**
     * Constructor method.
     *
     * Uncertainty format:
     * - Measure dependent: (+/-) (offset + (percentage * measuredValue)
     * - Scale dependent: (+/-) (offset + (percentage * param)
     *
     * @param uncertaintyType - Uncertainty type (Measure dependent or scale dependent.
     * @param uncertaintyOffset - Uncertainty offset.
     * @param uncertaintyPercentage - Uncertainty percentage.
     * @param uncertaintyParam - Uncertainty equipment parameter.
     */
    public MeasureSet(int uncertaintyType, double uncertaintyOffset, double uncertaintyPercentage, double uncertaintyParam){
        this.uncertaintyOffset = uncertaintyOffset;
        this.uncertaintyPercentage = uncertaintyPercentage;
        this.uncertaintyParam = uncertaintyParam;
        this.uncertaintyType = uncertaintyType;
    }

    /**
     * Adds a new measured value.
     * @param value - Measured value.
     */
    public void add(double value){
        measuredValues.add(value);
    }

    /**
     * Adds a new measured value.
     * @param value - Measured value.
     * @param uncertainty - Uncertainty value.
     */
    public void add(double value, double uncertainty){
        measuredValues.add(value);
        cnstUncertainty.add(uncertainty);
    }


    /**
     * Gets measure set mean.
     * @return - Mean.
     */
    public double getMean(){
        return StatisticUtils.mean(measuredValues);
    }

    /**
     * Gets measure set standard deviation.
     * @return - Standard deviation.
     */
    public double getStdDeviation(){
        return StatisticUtils.stDeviation(measuredValues);
    }

    /**
     * Changes uncertainty value.
     *
     * Uncertainty format:
     * - Measure dependent: (+/-) (offset + (percentage * measuredValue)
     * - Scale dependent: (+/-) (offset + (percentage * param)
     *
     * @param uncertaintyType - Uncertainty type (Measure dependent or scale dependent.
     * @param uncertaintyOffset - Uncertainty offset.
     * @param uncertaintyPercentage - Uncertainty percentage. %
     * @param uncertaintyParam - Uncertainty equipment parameter.
     */
    public void setUncertainty(int uncertaintyType, double uncertaintyOffset, double uncertaintyPercentage, double uncertaintyParam){
        this.uncertaintyOffset = uncertaintyOffset;
        this.uncertaintyPercentage = uncertaintyPercentage;
        this.uncertaintyParam = uncertaintyParam;
        this.uncertaintyType = uncertaintyType;
    }

    /**
     * Gets measure uncertainty.
     * @return - Measure uncertainty.
     */
    public double getUncertainty(int measuredPosition){

        if(measuredPosition >= measuredValues.size())
            return 0;

        if(uncertaintyType == TYPE_SCALE_DEPENDENT)
            return uncertaintyOffset + ((uncertaintyPercentage/100) * uncertaintyParam);

        if(uncertaintyType == TYPE_CONSTANT)
            return cnstUncertainty.get(measuredPosition);

        return  uncertaintyOffset + ((uncertaintyPercentage/100) * measuredValues.get(measuredPosition));
    }

    /**
     * Gets sample count.
     * @return - Sample count.
     */
    public int size(){
        return measuredValues.size();
    }

    /**
     * Gets measured sample value.
     * @param position - Position.
     * @return - Value.
     */
    public double getValue(int position){
        return measuredValues.get(position);
    }

    /**
     * Calculates expanded uncertainty.
     * @return - Expanded uncertainty.
     */
    public double getExpandedUncertainty(){
        return getStdDeviation() * StatisticUtils.getStudentCoef_95(size());
    }

    /**
     * Removes a measured value.
     * @param position - Measure position.
     */
    public void removeMeasure(int position){
        if(position >= 0 && position < measuredValues.size())
            measuredValues.remove(position);
    }

    /**
     * Gets margin of error.
     * @return - Margin of error.
     */
    public double getMarginOfError(){
        return StatisticUtils.getDistributionMultiplier(measuredValues.size()) * getStdDeviation();
    }

    /**
     * Copies uncertainty values from another set.
     * @param anotherSet - Another set to use.
     */
    public void copyUncertainty(MeasureSet anotherSet){
        uncertaintyOffset = anotherSet.uncertaintyOffset;
        uncertaintyParam = anotherSet.uncertaintyParam;
        uncertaintyPercentage = anotherSet.uncertaintyPercentage;
        uncertaintyType = anotherSet.uncertaintyType;
    }

    /**
     * Copies uncertainty values from another set.
     * @param magnitude - Magnitude to get uncertainty parameters.
     */
    public void copyUncertainty(Magnitude magnitude){
        uncertaintyOffset = magnitude.getUncertaintyOffset();
        uncertaintyParam = magnitude.getUncertaintyVar();
        uncertaintyPercentage = magnitude.getUncertaintyPercent();
        uncertaintyType = magnitude.getUncertaintyType();
    }
}
