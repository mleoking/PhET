package edu.colorado.phet.acidbasesolutions.control;

import edu.colorado.phet.acidbasesolutions.ABSConstants;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LogarithmicValueControl;


public class WeakStrengthSlider extends LogarithmicValueControl {

    public WeakStrengthSlider() {
        super( ABSConstants.MIN_WEAK_STRENGTH, ABSConstants.MAX_WEAK_STRENGTH, "strength", "0.0", "" );
    }
}
