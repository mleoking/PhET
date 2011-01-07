// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.mvcexample.control;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.mvcexample.MVCExampleApplication;

/**
 * OrientationControl is a control for orientation, in degrees.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class OrientationControl extends LinearValueControl {

    private static final double MIN = 0;
    private static final double MAX = 360;
    private static final String LABEL = MVCExampleApplication.RESOURCE_LOADER.getLocalizedString( "OrientationControl.label" );
    private static final String VALUE_PATTERN = "##0";
    private static final String UNITS = MVCExampleApplication.RESOURCE_LOADER.getLocalizedString( "OrientationControl.units" );
    
    public OrientationControl() {
        super( MIN, MAX, LABEL, VALUE_PATTERN, UNITS );
        setTextFieldEditable( true );
        setUpDownArrowDelta( 1 );
        setTickPattern( "0" );
        setMajorTickSpacing( 90 );
        setMinorTickSpacing( 45 );
    }
}
