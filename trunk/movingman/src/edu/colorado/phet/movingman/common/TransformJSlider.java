/*PhET, 2004.*/
package edu.colorado.phet.movingman.common;

import javax.swing.*;
import java.util.Hashtable;

/**
 * User: Sam Reid
 * Date: Jul 21, 2003
 * Time: 12:02:04 AM
 * Copyright (c) Jul 21, 2003 by Sam Reid
 */
public class TransformJSlider extends JSlider {
    double modelMin;
    double modelMax;
    int sliderMin;
    int sliderMax;

    private Hashtable labelTable;

    public TransformJSlider( double min, double max, int numSteps ) {
        super( 0, numSteps );
        this.sliderMin = 0;
        this.sliderMax = numSteps;
        this.modelMin = min;
        this.modelMax = max;
        labelTable = new Hashtable();
        setLabelTable( labelTable );
    }

    public void setModelRange( double min, double max ) {
        this.modelMin = min;
        this.modelMax = max;
        setValue( getValue() );
    }

    public void setMajorTickSpacingModel( double spacing ) {
        int sliderspacing = modelToSliderValue( spacing );
        super.setMajorTickSpacing( sliderspacing );
    }

    public double getModelValue() {
        int sliderValue = getValue();
        double modelValue = sliderToModelValue( sliderValue );
        return modelValue;
    }

    public double sliderToModelValue( int sliderValue ) {
        double rise = ( modelMax - modelMin );
        double run = ( sliderMax - sliderMin );
        double m = rise / run;
        double out = ( m * ( sliderValue - sliderMin ) + modelMin );
        return out;
    }

    public int modelToSliderValue( double value ) {
        double rise = ( sliderMax - sliderMin );
        double run = ( modelMax - modelMin );
        double m = rise / run;
        int out = (int)( m * ( value - modelMin ) + sliderMin );
        return out;
    }

    public void addLabel( double minVelocity, JLabel label ) {
        labelTable.put( new Integer( modelToSliderValue( minVelocity ) ), label );
        setLabelTable( labelTable );
    }

    public void setModelValue( double value ) {
        int sliderValue = modelToSliderValue( value );
        setValue( sliderValue );
    }
}

