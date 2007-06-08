package edu.colorado.phet.energyskatepark.view.swing;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.TimingStrategy;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.energyskatepark.EnergySkateParkApplication;
import edu.colorado.phet.energyskatepark.EnergySkateParkClock;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * Author: Sam Reid
 * Jun 1, 2007, 1:39:39 PM
 */
public class EnergySkateParkTimePanel extends JPanel {
    private EnergySkateParkModule module;

    public EnergySkateParkTimePanel( EnergySkateParkModule module, final Clock clock ) {
        this.module = module;
        final TimeSpeedSlider timeSpeedSlider = new TimeSpeedSlider( EnergySkateParkApplication.SIMULATION_TIME_DT / 4.0, EnergySkateParkApplication.SIMULATION_TIME_DT, "0.00", (EnergySkateParkClock)clock );
        timeSpeedSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                clock.setTimingStrategy( new TimingStrategy.Constant( timeSpeedSlider.getValue() ) );
            }
        } );
//        add( new JLabel( new ImageIcon( PhetCommonResources.getInstance().getImage( PhetCommonResources.IMAGE_CLOCK ) ) ) );
        add( timeSpeedSlider );
        ClockControlPanel controlPanel = new ESPCCP( clock );
        add( controlPanel );
    }

    class ESPCCP extends ClockControlPanel {
        public ESPCCP( IClock clock ) {
            super( clock );
            addTimeControlListener( new TimeControlAdapter() {
                public void stepPressed() {
                    module.setRecordOrLiveMode();
                }

                public void playPressed() {
                    module.setRecordOrLiveMode();
                }

                public void pausePressed() {
                    module.setRecordOrLiveMode();
                }
            } );
        }
    }
}
