// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.swing;

import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.TimeControlListener.TimeControlAdapter;
import edu.colorado.phet.common.phetcommon.view.clock.SimSpeedControl;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
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
        final SimSpeedControl timeSpeedSlider = new SimSpeedControl( EnergySkateParkApplication.SIMULATION_TIME_DT / 4.0, EnergySkateParkApplication.SIMULATION_TIME_DT, (ConstantDtClock) clock );
        add( timeSpeedSlider );
        PiccoloClockControlPanel controlPanel = new PiccoloClockControlPanel( clock );
        controlPanel.addTimeControlListener( new TimeControlAdapter() {
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
