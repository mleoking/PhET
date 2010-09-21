/* Copyright 2010, University of Colorado */

package edu.colorado.phet.capacitorlab.control;

import edu.colorado.phet.capacitorlab.CLConstants;
import edu.colorado.phet.capacitorlab.CLStrings;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;


public class DielectricConstantControl extends LinearValueControl {

    private static final double min = CLConstants.DIELECTRIC_CONSTANT_RANGE.getMin();
    private static final  double max = CLConstants.DIELECTRIC_CONSTANT_RANGE.getMax();
    private static final String label = CLStrings.LABEL_DIELECTRIC_CONSTANT;
    private static final String textFieldPattern = "0.000";
    private static final String units = "";
    
    public DielectricConstantControl( double value ) {
        super( min, max, label, textFieldPattern, units );
        setValue( value );
        setTickPattern( "0" );
        setTextFieldColumns( 4 );
    }
}
