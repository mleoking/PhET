/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.view.controls.valuecontrol;

/**
 * LogarithmicSlider is a JSlider with that provides a logarithmic mapping to model values.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LogarithmicSlider extends AbstractSlider {

    private static final int DEFAULT_SLIDER_RESOLUTION = 1000;

    public LogarithmicSlider( double min, double max ) {
        this( min, max, DEFAULT_SLIDER_RESOLUTION );
    }
    
    public LogarithmicSlider( double min, double max, int resolution ) {
        super( new LogarithmicMappingStrategy( min, max, 0, resolution ) );
    }
}
