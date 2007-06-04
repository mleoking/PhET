package edu.colorado.phet.energyskatepark.view.swing;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.clock.TimingStrategy;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.common.timeseries.model.TimeSeriesModel;
import edu.colorado.phet.energyskatepark.EnergySkateParkApplication;
import edu.colorado.phet.energyskatepark.EnergySkateParkClock;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        add( new JLabel( new ImageIcon( PhetCommonResources.getInstance().getImage( PhetCommonResources.IMAGE_CLOCK ) ) ) );
        add( timeSpeedSlider );
        ClockControlPanel controlPanel = new ESPCCP( clock );
        add( controlPanel );
    }

    class ESPCCP extends ClockControlPanel {
        public ESPCCP( IClock clock ) {
            super( clock );
            addPlayPauseActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    module.setRecordOrLiveMode();
                }
            } );
        }
    }
}
