/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control.slider;


/**
 * LogarithmicSliderStrategy performs a logarithmic (base 10) conversion 
 * between slider and model coordinates.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LogarithmicSliderStrategy extends AbstractSliderStrategy {
    
    private double _logMin, _logMax, _logRange;
    
    /**
     * Constructor.
     * 
     * @param sliderMin
     * @param sliderMax
     * @param modelMin
     * @param modelMax
     * @throws IllegalArgumentException if modelMin and modelMax have different signs
     */
    public LogarithmicSliderStrategy( int sliderMin, int sliderMax, double modelMin, double modelMax ) {
        super( sliderMin, sliderMax, modelMin, modelMax );
        if ( modelMin < 0 && modelMax > 0 || modelMin > 0 && modelMax < 0 ) {
            throw new IllegalArgumentException( "modelMin and modelMax must have the same sign" );
        }
        _logMin = adjustedLog10( modelMin );
        _logMax = adjustedLog10( modelMax );
        _logRange = _logMax - _logMin;
    }

    /**
     * Converts from slider to model coordinates.
     * 
     * @param sliderValue slider value
     * @return model value
     */
    public double sliderToModel( int sliderValue ) {
        double modelValue = 0;
        int resolution = getSliderMax() - getSliderMin();
        double ratio = _logRange / (double)resolution;
        double pos = (double)( sliderValue - getSliderMin() ) * ratio;
        double adjustedPos = _logMin + pos;
        if ( adjustedPos >= 0 ) {
            modelValue = Math.pow( 10.0, adjustedPos );
        }
        else {
            modelValue = -Math.pow( 10.0, -adjustedPos );
        }
        if ( modelValue < getModelMin() ) {
            modelValue = getModelMin();
        }
        else if ( modelValue > getModelMax() ) {
            modelValue = getModelMax();
        }
        return modelValue;
    }

    /**
     * Converts from model to slider coordinates.
     * 
     * @param modelValue model value
     * @return slider value
     */
    public int modelToSlider( double modelValue ) {
        int sliderValue = 0;
        int resolution = getSliderMax() - getSliderMin();
        double logModelValue = adjustedLog10( modelValue );
        sliderValue = getSliderMin() + (int)( resolution * ( logModelValue - _logMin ) / _logRange );
        if ( sliderValue < getSliderMin() ) {
            sliderValue = getSliderMin();
        }
        else if ( sliderValue > getSliderMax() ) {
            sliderValue = getSliderMax();
        }
        return sliderValue;
    }
    
    /* Handles log base 10 of 0 and negative values. */
    private static double adjustedLog10( double d ) {
        double value = 0;
        if ( d > 0 ) {
            value = log10( d );
        }
        else if ( d < 0 ) {
            value = -log10( -d );
        }
        return value;
    }
    
    /* Log base 10 */
    private static double log10( double d ) {
        return Math.log( d ) / Math.log(  10.0  );
    }
}