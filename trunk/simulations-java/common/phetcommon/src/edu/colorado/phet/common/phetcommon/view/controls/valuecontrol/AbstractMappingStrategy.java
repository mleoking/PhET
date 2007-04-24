/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.view.controls.valuecontrol;


/**
 * AbstractMappingStrategy is the base class for all classes that provide a mapping
 * between slider values (integer precision) and model values (double precision).. 
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractMappingStrategy {
    
    private double _modelMin, _modelMax;
    private int _sliderMin, _sliderMax;
    
    /**
     * Constructor.
     * 
     * @param modelMin
     * @param modelMax
     * @param sliderMin
     * @param sliderMax
     */
    public AbstractMappingStrategy( double modelMin, double modelMax, int sliderMin, int sliderMax ) {
        _modelMin = modelMin;
        _modelMax = modelMax;
        _sliderMin = sliderMin;
        _sliderMax = sliderMax;
    }

    public double getModelMin() {
        return _modelMin;
    }
    
    public double getModelMax() {
        return _modelMax;
    }
    
    public int getSliderMin() {
        return _sliderMin;
    }
    
    public int getSliderMax() {
        return _sliderMax;
    }
    
    public String toString() {
        return getClass().getName() + 
            " modelMin=" + _modelMin + " modelMax=" + _modelMax +
            " sliderMin=" + _sliderMin + " sliderMax=" + _sliderMax; 
    }
    
    /**
     * Converts from slider value to model value.
     * 
     * @param sliderValue slider value
     * @return model value
     */
    public abstract double sliderToModel( int sliderValue );
    
    
    /**
     * Converts from model value to slider value.
     * 
     * @param modelValue model value
     * @return slider value
     */
    public abstract int modelToSlider( double modelValue );
}
