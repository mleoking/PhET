/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.view.controls.valuecontrol;

/**
 * LinearSlider is a JSlider with that provides a linear mapping to model values.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LinearSlider extends AbstractSlider {

    private static final int DEFAULT_SLIDER_RESOLUTION = 1000;

    public LinearSlider( double min, double max ) {
        this( min, max, DEFAULT_SLIDER_RESOLUTION );
    }

    public LinearSlider( double min, double max, int resolution ) {
        super( new LinearMappingStrategy( min, max, 0, resolution ) );
    }
}
