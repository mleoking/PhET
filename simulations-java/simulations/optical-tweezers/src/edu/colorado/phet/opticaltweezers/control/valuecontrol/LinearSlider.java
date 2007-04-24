/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control.valuecontrol;


public class LinearSlider extends AbstractSlider {
    
    private static final int DEFAULT_RESOLUTION = 1000;

    public LinearSlider( double min, double max ) {
        this( min, max, DEFAULT_RESOLUTION );
    }
    
    public LinearSlider( double min, double max, int resolution ) {
        super( new LinearSliderStrategy( min, max, 0, resolution ) );
    }
}
