package edu.colorado.phet.energyskatepark.view.swing;

import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.model.clock.TimingStrategy;
import edu.colorado.phet.common.phetcommon.view.ClockControlPanel;
import edu.colorado.phet.common.phetcommon.view.TimeControlPanelListener.TimeControlPanelAdapter;
import edu.colorado.phet.common.timeseries.ui.TimeSpeedSlider;
import edu.colorado.phet.energyskatepark.EnergySkateParkApplication;
import edu.colorado.phet.energyskatepark.EnergySkateParkModule;

/**
 * Author: Sam Reid
 * Jun 1, 2007, 1:39:39 PM
 */
public class EnergySkateParkTimePanel extends JPanel {
    private EnergySkateParkModule module;

    public EnergySkateParkTimePanel( final EnergySkateParkModule module, final Clock clock ) {
        this.module = module;
        final TimeSpeedSlider timeSpeedSlider = new TimeSpeedSlider( EnergySkateParkApplication.SIMULATION_TIME_DT / 4.0, EnergySkateParkApplication.SIMULATION_TIME_DT, "0.00", (ConstantDtClock)clock );
        timeSpeedSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                clock.setTimingStrategy( new TimingStrategy.Constant( timeSpeedSlider.getValue() ) );
            }
        } );
//        add( new JLabel( new ImageIcon( PhetCommonResources.getInstance().getImage( PhetCommonResources.IMAGE_CLOCK ) ) ) );
        add( timeSpeedSlider );
        ClockControlPanel controlPanel = new ClockControlPanel( clock );
        controlPanel.addTimeControlListener( new TimeControlPanelAdapter() {
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
        add( controlPanel );
    }

}
