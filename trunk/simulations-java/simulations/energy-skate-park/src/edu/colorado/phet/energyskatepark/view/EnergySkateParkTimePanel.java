package edu.colorado.phet.energyskatepark.view;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.TimingStrategy;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.energyskatepark.EnergySkateParkApplication;
import edu.colorado.phet.energyskatepark.EnergySkateParkClock;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Author: Sam Reid
 * Jun 1, 2007, 1:39:39 PM
 */
public class EnergySkateParkTimePanel extends JPanel {

    public EnergySkateParkTimePanel( final Clock clock ) {
        final TimeSpeedSlider timeSpeedSlider = new TimeSpeedSlider( EnergySkateParkApplication.SIMULATION_TIME_DT / 4.0, EnergySkateParkApplication.SIMULATION_TIME_DT, "0.00", (EnergySkateParkClock)clock );
        timeSpeedSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                clock.setTimingStrategy( new TimingStrategy.Constant( timeSpeedSlider.getValue() ) );
            }
        } );
        add( timeSpeedSlider );
        add( new ClockControlPanel( clock ) );
    }

}
