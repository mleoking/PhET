// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.capacitorlab.control;

import java.text.MessageFormat;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;

/**
 * Control for the dielectric constant.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DielectricConstantControl extends LinearValueControl {

    private static final double MIN = CLConstants.DIELECTRIC_CONSTANT_RANGE.getMin();
    private static final  double MAX = CLConstants.DIELECTRIC_CONSTANT_RANGE.getMax();
    private static final String LABEL = MessageFormat.format( CLStrings.PATTERN_LABEL, CLStrings.DIELECTRIC_CONSTANT );
    private static final String TEXTFIELD_PATTERN = "0.000";
    private static final String UNITS = "";

    public DielectricConstantControl( double value ) {
        super( MIN, MAX, LABEL, TEXTFIELD_PATTERN, UNITS );
        setValue( value );
        setTickPattern( "0" );
        setTextFieldColumns( 4 );
        setMinorTickSpacing( 1 );
    }
}
