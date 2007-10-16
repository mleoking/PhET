/* Copyright 2007, University of Colorado */

package edu.colorado.phet.mvcexample.control;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.mvcexample.MVCApplication;

/**
 * OrientationControl is a control for orientation, in degrees.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OrientationControl extends LinearValueControl {

    private static final double MIN = 0;
    private static final double MAX = 360;
    private static final String LABEL = MVCApplication.RESOURCE_LOADER.getLocalizedString( "OrientationControl.label" );
    private static final String VALUE_PATTERN = "##0";
    private static final String UNITS = MVCApplication.RESOURCE_LOADER.getLocalizedString( "OrientationControl.units" );
    
    public OrientationControl() {
        super( MIN, MAX, LABEL, VALUE_PATTERN, UNITS );
        setTextFieldEditable( true );
        setUpDownArrowDelta( 1 );
        setTickPattern( "0" );
        setMajorTickSpacing( 90 );
        setMinorTickSpacing( 45 );
    }
}
