// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.energyskatepark.view.piccolo;

import java.awt.Color;

import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.TimeControlListener.TimeControlAdapter;
import edu.colorado.phet.common.phetcommon.view.clock.SimSpeedControl;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkApplication;

/**
 * Retrieved from SVN history in a hurry--got it done fast but it lost svn history.
 * Author: Sam Reid
 * Jun 1, 2007, 1:39:39 PM
 */
public class ESPSimSpeedSlider extends JPanel {

    private final Color BLANK = new Color( 0, 0, 0, 0 );

    public ESPSimSpeedSlider( final AbstractEnergySkateParkModule module, final Clock clock ) {

        //Make backgrounds blank so it will work as a floating clock control panel
        setBackground( BLANK );
        final SimSpeedControl timeSpeedSlider = new SimSpeedControl( EnergySkateParkApplication.SIMULATION_TIME_DT / 4.0, EnergySkateParkApplication.SIMULATION_TIME_DT, (ConstantDtClock) clock, PhetCommonResources.getString( "Common.sim.speed" ), Color.white );
        timeSpeedSlider.setBackground( BLANK );
        timeSpeedSlider.getLinearSlider().getSlider().setBackground( BLANK );
        timeSpeedSlider.getLinearSlider().setBackground( BLANK );
        add( timeSpeedSlider );
        PiccoloClockControlPanel controlPanel = new PiccoloClockControlPanel( clock );
        controlPanel.setBackground( BLANK );
        controlPanel.getButtonCanvas().setBackground( BLANK );
        controlPanel.getBackgroundNode().setVisible( false );
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
