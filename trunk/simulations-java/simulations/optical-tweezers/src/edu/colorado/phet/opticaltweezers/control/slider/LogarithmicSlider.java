/* Copyright 2007, University of Colorado */

package edu.colorado.phet.opticaltweezers.control.slider;


public class LogarithmicSlider extends AbstractSlider {

    private static final int DEFAULT_RESOLUTION = 1000;

    public LogarithmicSlider( double min, double max ) {
        this( min, max, DEFAULT_RESOLUTION );
    }
    
    public LogarithmicSlider( double min, double max, int resolution ) {
        super( new LogarithmicSliderStrategy( min, max, 0, resolution ) );
    }
}
