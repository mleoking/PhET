/* Copyright 2007, University of Colorado */

package edu.colorado.phet.common.phetcommon.view.controls.valuecontrol;

/**
 * LogarithmicValueControl is for controlling values that have a logarithmic scale.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class LogarithmicValueControl extends AbstractValueControl {

    /**
     * Constructor that provides a default layout for the control.
     * 
     * @param min
     * @param max
     * @param label
     * @param textFieldPattern
     * @param units
     */
    public LogarithmicValueControl( double min, double max, String label, String textFieldPattern, String units ) {
        this( min, max , label, textFieldPattern, units, new DefaultLayoutStrategy() );
    }
    
    /**
     * Constructor that allows you to specify a layout for the control.
     * 
     * @param min
     * @param max
     * @param label
     * @param textFieldPattern
     * @param units
     * @param layoutStrategy
     */
    public LogarithmicValueControl( double min, double max, String label, String textFieldPattern, String units, ILayoutStrategy layoutStrategy ) {
        super( new LogarithmicSlider( min, max ), label, textFieldPattern, units, layoutStrategy );
    }
    
}
