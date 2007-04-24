/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.view.controls.valuecontrol;

/**
 * LogarithmicValueControl is for controlling values that have a logarithmic scale.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LogarithmicValueControl extends AbstractValueControl {

    public LogarithmicValueControl( double min, double max, String label, String textFieldPattern, String units ) {
        super( new LogarithmicSlider( min, max ), label, textFieldPattern, units );
    }
    
}
