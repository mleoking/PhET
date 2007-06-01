package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.TimingStrategy;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.common.phetcommon.view.controls.valuecontrol.LinearValueControl;
import edu.colorado.phet.energyskatepark.EnergySkateParkApplication;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.util.Hashtable;

/**
 * Author: Sam Reid
 * Jun 1, 2007, 1:39:39 PM
 */
public class EnergySkateParkClockControlPanel extends JPanel {

    public EnergySkateParkClockControlPanel( Clock clock ) {
        add( new TimeSpeedSlider( clock, EnergySkateParkApplication.SIMULATION_TIME_DT / 4.0, EnergySkateParkApplication.SIMULATION_TIME_DT, "0.00" ) );
        add( new ClockControlPanel( clock ) );
    }

    static class TimeSpeedSlider extends LinearValueControl {
        public TimeSpeedSlider( final Clock clock, double min, double max, String textFieldPattern ) {
            super( min, max, "", textFieldPattern, "" );
            setTextFieldVisible( false );
            Hashtable table = new Hashtable();
            table.put( new Double( min ), new JLabel( "slow" ) );
            table.put( new Double( max ), new JLabel( "normal" ) );
            setTickLabels( table );
            setValue( max );
            addChangeListener( new ChangeListener() {
                public void stateChanged( ChangeEvent e ) {
                    clock.setTimingStrategy( new TimingStrategy.Constant( getValue() ) );
                }
            } );
        }
    }

}
