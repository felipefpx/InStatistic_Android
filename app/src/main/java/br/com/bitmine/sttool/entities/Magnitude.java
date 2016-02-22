package br.com.bitmine.sttool.entities;

import java.util.ArrayList;

/**
 * This class represents a magnitude.
 * @author Felipe Porge Xavier - http://www.felipeporge.com
 */
public class Magnitude {

    private ArrayList<MeasureSet> measures = new ArrayList<MeasureSet>();
    private String name;
    private int numberOfSamples = 0;
    private boolean isFixed = false;
    private double uncertaintyOffset = 0.0;
    private double uncertaintyPercent = 0.0;
    private double uncertaintyVar = 0.0;
    private int uncertaintyType = MeasureSet.TYPE_SCALE_DEPENDENT;


    // Getters and Setters - BEGIN

    public double getUncertaintyOffset() {
        return uncertaintyOffset;
    }

    public void setUncertaintyOffset(double uncertaintyOffset) {
        this.uncertaintyOffset = uncertaintyOffset;
    }

    public double getUncertaintyPercent() {
        return uncertaintyPercent;
    }

    public void setUncertaintyPercent(double uncertaintyPercent) {
        this.uncertaintyPercent = uncertaintyPercent;
    }

    public double getUncertaintyVar() {
        return uncertaintyVar;
    }

    public void setUncertaintyVar(double uncertaintyVar) {
        this.uncertaintyVar = uncertaintyVar;
    }

    public int getUncertaintyType() {
        return uncertaintyType;
    }

    public void setUncertaintyType(int uncertaintyType) {
        this.uncertaintyType = uncertaintyType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getNumberOfSamples() {
        return numberOfSamples;
    }

    public void setNumberOfSamples(int numberOfSamples) {
        this.numberOfSamples = numberOfSamples;
    }

    public boolean isFixed() {
        return isFixed;
    }

    public void setIsFixed(boolean isFixed) {
        this.isFixed = isFixed;
    }

    public void addMeasureSet(MeasureSet set){
        measures.add(set);
    }

    public MeasureSet getMeasureSet(int index){
        return measures.get(index);
    }

    // Getters and Setters - END


    /**
     * Returns the number of completed measures.
     * @return - Number of completed measures.
     */
    public int numberOfCompletedMeasures(){
        return measures.size();
    }

    /**
     * Erases all measure sets.
     */
    public void clearMeasureSets(){
        measures.clear();
    }
}
