/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.view.controls.valuecontrol;

import javax.swing.*;

/**
 * AbstractSlider is the base class for all extensions of JSlider that provide
 * a mapping between slider values (integer precision) and model values (double precision).
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractSlider extends JSlider {

    private AbstractMappingStrategy _strategy;

    protected AbstractSlider( AbstractMappingStrategy strategy ) {
        super();
        _strategy = strategy;
        setMinimum( _strategy.getSliderMin() );
        setMaximum( _strategy.getSliderMax() );
    }

    public void setModelRange( double min, double max ) {
        _strategy.setModelRange( min, max );
        setMinimum( _strategy.getSliderMin() );
        setMaximum( _strategy.getSliderMax() );
    }

    public void setModelValue( double modelValue ) {
        int sliderValue = _strategy.modelToSlider( modelValue );
        setValue( sliderValue );
    }

    public double getModelValue() {
        int sliderValue = getValue();
        return sliderToModel( sliderValue );
    }

    public double getModelMin() {
        return _strategy.getModelMin();
    }

    public double getModelMax() {
        return _strategy.getModelMax();
    }

    public double getModelRange() {
        return _strategy.getModelRange();
    }

    public int getSliderMin() {
        return _strategy.getSliderMin();
    }

    public int getSliderMax() {
        return _strategy.getSliderMax();
    }

    public int getSliderRange() {
        return _strategy.getSliderRange();
    }

    public double sliderToModel( int sliderValue ) {
        return _strategy.sliderToModel( sliderValue );
    }

    public int modelToSlider( double modelValue ) {
        return _strategy.modelToSlider( modelValue );
    }
}
