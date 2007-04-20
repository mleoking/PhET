/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control.slider;


/**
 * LinearSliderStrategy performs a linear conversion between slider and model coordinates.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LinearSliderStrategy extends AbstractSliderStrategy {
    
    /**
     * Constructor.
     * 
     * @param sliderMin
     * @param sliderMax
     * @param modelMin
     * @param modelMax
     */
    public LinearSliderStrategy( int sliderMin, int sliderMax, double modelMin, double modelMax ) {
        super( sliderMin, sliderMax, modelMin, modelMax );
    }

    /**
     * Converts from slider to model coordinates.
     * 
     * @param sliderValue slider value
     * @return model value
     */
    public double sliderToModel( int sliderValue ) {
        double ratio = ( sliderValue - getSliderMin() ) / (double)( getSliderMax() - getSliderMin() );
        return getModelMin() + ( ratio * ( getModelMax() - getModelMin() ) );
    }

    /**
     * Converts from model to slider coordinates.
     * 
     * @param modelValue model value
     * @return slider value
     */
    public int modelToSlider( double modelValue ) {
        double ratio = ( modelValue - getModelMin() ) / ( getModelMax() - getModelMin() );
        return getSliderMin() + (int)( ratio * ( getSliderMax() - getSliderMin() ) );
    }
}
