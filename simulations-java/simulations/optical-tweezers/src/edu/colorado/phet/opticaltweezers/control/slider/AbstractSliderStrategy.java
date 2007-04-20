/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control.slider;


/**
 * AbstractSliderStrategy provides some functionality that is useful to all SliderStrategy's. 
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractSliderStrategy implements ISliderStrategy {
    
    private int _sliderMin, _sliderMax;
    private double _modelMin, _modelMax;
    
    public AbstractSliderStrategy( int sliderMin, int sliderMax, double modelMin, double modelMax ) {
        _sliderMin = sliderMin;
        _sliderMax = sliderMax;
        _modelMin = modelMin;
        _modelMax = modelMax;
    }
    
    public int getSliderMin() {
        return _sliderMin;
    }
    
    public int getSliderMax() {
        return _sliderMax;
    }

    public double getModelMin() {
        return _modelMin;
    }
    
    public double getModelMax() {
        return _modelMax;
    }
    
    public String toString() {
        return getClass().getName() + " slider=[" + _sliderMin + "," + _sliderMax + "] model=[" + _modelMin + "," + _modelMax + "]";
    }
}
