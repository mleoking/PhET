// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.view.controls.valuecontrol;

/**
 * LogarithmicValueControl is for controlling values that have a logarithmic scale.
 * <p/>
 * Setting tick marks for this control can be challenging and (unfortunately)
 * requires an understanding of the underlying slider implementation.
 * The underlying slider is based on JSlider, which only supports
 * linearly-spaced tick marks.  So you are limited to using tick marks
 * and labels that are linearly spaced.
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
        this( min, max, label, textFieldPattern, units, new DefaultLayoutStrategy() );
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
