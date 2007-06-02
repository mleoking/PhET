package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.energyskatepark.EnergySkateParkClock;

import javax.swing.*;
import java.util.Hashtable;

/**
 * Author: Sam Reid
* Jun 1, 2007, 2:27:44 PM
*/
public class TimeSpeedSlider extends LinearValueControl {
    private EnergySkateParkClock energySkateParkClock;

    public TimeSpeedSlider( double min, double max, String textFieldPattern, final EnergySkateParkClock energySkateParkClock ) {
        super( min, max, "", textFieldPattern, "" );
        this.energySkateParkClock = energySkateParkClock;
        setTextFieldVisible( false );
        Hashtable table = new Hashtable();
        table.put( new Double( min ), new JLabel( "slow" ) );
        table.put( new Double( max ), new JLabel( "normal" ) );
        setTickLabels( table );
        setValue( max );
        energySkateParkClock.addListener( new EnergySkateParkClock.Listener() {
            public void changed() {
                update( energySkateParkClock );
            }
        } );
        update( energySkateParkClock );
    }

    private void update( EnergySkateParkClock energySkateParkClock ) {
        setValue( energySkateParkClock.getTimingStrategy().getSimulationTimeChangeForPausedClock());
    }
}
