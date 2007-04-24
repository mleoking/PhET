/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control.valuecontrol;

/**
 * ISliderStrategy is an interface that handles the integer-double conversions
 * needed for mapping between slider and model coordinate systems.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public interface ISliderStrategy {
    
    /**
     * Converts from slider to model coordinates.
     * 
     * @param sliderValue slider value
     * @return model value
     */
    public double sliderToModel( int sliderValue );
    
    /**
     * Converts from model to slider coordinates.
     * 
     * @param modelValue model value
     * @return slider value
     */
    public int modelToSlider( double modelValue );
}
