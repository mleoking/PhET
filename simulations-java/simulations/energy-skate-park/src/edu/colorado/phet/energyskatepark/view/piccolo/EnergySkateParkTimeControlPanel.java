package edu.colorado.phet.energyskatepark.view.piccolo;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.TimeControlListener;
import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.UserComponents;

import static edu.colorado.phet.energyskatepark.EnergySkateParkApplication.SIMULATION_TIME_DT;

/**
 * Adapter that wires up energy skate park module + clock to the reusable slow motion normal time control panel.
 *
 * @author Sam Reid
 */
public class EnergySkateParkTimeControlPanel extends SlowMotionNormalTimeControlPanel {
    public EnergySkateParkTimeControlPanel( final AbstractEnergySkateParkModule module, final ConstantDtClock clock ) {
        super( UserComponents.slowMotionRadioButton, UserComponents.normalSpeedRadioButton, module.normalSpeed, clock );
        module.normalSpeed.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean normalSpeed ) {
                clock.setDt( normalSpeed ? SIMULATION_TIME_DT : SIMULATION_TIME_DT / 4.0 );
            }
        } );
        piccoloClockControlPanel.addTimeControlListener( new TimeControlListener.TimeControlAdapter() {
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