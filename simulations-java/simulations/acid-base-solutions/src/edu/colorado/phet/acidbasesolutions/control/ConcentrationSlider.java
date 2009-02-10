package edu.colorado.phet.acidbasesolutions.control;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LogarithmicValueControl;


public class ConcentrationSlider extends LogarithmicValueControl {
    
    public ConcentrationSlider() {
        super( ABSConstants.MIN_CONCENTRATION, ABSConstants.MAX_CONCENTRATION, "concentration", "0.0", "mol/L" );
    }

}
