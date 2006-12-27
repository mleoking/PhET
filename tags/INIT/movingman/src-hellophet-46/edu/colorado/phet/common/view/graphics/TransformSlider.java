/*Copyright, Sam Reid, 2003.*/
package edu.colorado.phet.common.view.graphics;

import javax.swing.*;
import java.util.Hashtable;

/**
 * User: Sam Reid
 * Date: Jul 21, 2003
 * Time: 12:02:04 AM
 * Copyright (c) Jul 21, 2003 by Sam Reid
 */
public class TransformSlider {
    double modelMin;
    double modelMax;
    int sliderMin;
    int sliderMax;

    private JSlider slider;
    private Hashtable labelTable;

    public JSlider getSlider() {
        return slider;
    }

    public TransformSlider(double min, double max, int numSteps) {
        this.sliderMin = 0;
        this.sliderMax = numSteps;
        this.modelMin=min;
        this.modelMax=max;
        slider = new JSlider(sliderMin, sliderMax);
        labelTable=new Hashtable();
        slider.setLabelTable(labelTable);
    }

    public void setMajorTickSpacing(double spacing)
    {
        int sliderspacing=modelToSliderValue(spacing);
        slider.setMajorTickSpacing(sliderspacing);
    }

    public double getModelValue() {
        int sliderValue = slider.getValue();
        double modelValue = sliderToModelValue(sliderValue);
        return modelValue;
    }

    public double sliderToModelValue(int sliderValue) {
        double rise = (modelMax - modelMin);
        double run = (sliderMax - sliderMin);
        double m = rise / run;
        double out = (m * (sliderValue - sliderMin) + modelMin);
        return out;
    }

    public int modelToSliderValue(double value) {
        double rise = (sliderMax - sliderMin);
        double run = (modelMax - modelMin);
        double m = rise / run;
        int out = (int) (m * (value - modelMin) + sliderMin);
        return out;
    }

    public void addLabel(double minVelocity, JLabel label) {
        labelTable.put(new Integer(modelToSliderValue(minVelocity)),label);
        slider.setLabelTable(labelTable);
    }

    public void setModelValue(double value) {
        int sliderValue=modelToSliderValue(value);
        slider.setValue(sliderValue);
    }
}

