/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control.slider;


/**
 * AbstractSliderStrategy provides functionality that is useful for implementing ISliderStrategy. 
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class AbstractSliderStrategy implements ISliderStrategy {
    
    private double _modelMin, _modelMax;
    private int _sliderMin, _sliderMax;
    
    /**
     * Constructor.
     * 
     * @param sliderMin
     * @param sliderMax
     * @param modelMin
     * @param modelMax
     */
    public AbstractSliderStrategy( double modelMin, double modelMax, int sliderMin, int sliderMax ) {
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
}
