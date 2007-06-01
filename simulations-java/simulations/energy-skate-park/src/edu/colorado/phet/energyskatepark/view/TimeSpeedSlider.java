package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;

import javax.swing.*;
import java.util.Hashtable;

/**
 * Author: Sam Reid
* Jun 1, 2007, 2:27:44 PM
*/
public class TimeSpeedSlider extends LinearValueControl {
    public TimeSpeedSlider( double min, double max, String textFieldPattern ) {
        super( min, max, "", textFieldPattern, "" );
        setTextFieldVisible( false );
        Hashtable table = new Hashtable();
        table.put( new Double( min ), new JLabel( "slow" ) );
        table.put( new Double( max ), new JLabel( "normal" ) );
        setTickLabels( table );
        setValue( max );
    }
}
