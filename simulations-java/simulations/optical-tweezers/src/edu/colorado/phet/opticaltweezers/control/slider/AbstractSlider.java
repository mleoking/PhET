/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control.slider;

import javax.swing.JSlider;


public abstract class AbstractSlider extends JSlider {
    
    private AbstractSliderStrategy _strategy;
    
    public AbstractSlider( AbstractSliderStrategy strategy ) {
        super();
        _strategy = strategy;
        setMinimum( strategy.getSliderMin() );
        setMaximum( strategy.getSliderMax() );
        setValue( strategy.getSliderMin() );
    }
    
    public AbstractSliderStrategy getStrategy() {
        return _strategy;
    }
    
    public void setModelValue( double modelValue ) {
        int sliderValue = _strategy.modelToSlider( modelValue );
        setValue( sliderValue );
    }
    
    public double getModelValue() {
        int sliderValue = getValue();
        double modelValue = _strategy.sliderToModel( sliderValue );
        System.out.println( "sliderValue=" + sliderValue + " modelValue=" + modelValue );//XXX
        return modelValue;
    }
}
