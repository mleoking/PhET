package edu.colorado.phet.energyskatepark.view.piccolo;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.TimeControlListener;
import edu.colorado.phet.common.phetcommon.view.controls.simsharing.SimSharingPropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.colorado.phet.energyskatepark.EnergySkateParkResources;
import edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.energyskatepark.EnergySkateParkApplication.SIMULATION_TIME_DT;

/**
 * @author Sam Reid
 */
public class EnergySkateParkTimeControlPanel extends RichPNode {

    private final Color BLANK = new Color( 0, 0, 0, 0 );

    public EnergySkateParkTimeControlPanel( final AbstractEnergySkateParkModule module, final ConstantDtClock clock ) {
        module.normalSpeed.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean normalSpeed ) {
                clock.setDt( normalSpeed ? SIMULATION_TIME_DT : SIMULATION_TIME_DT / 4.0 );
            }
        } );

        PiccoloClockControlPanel controlPanel = new PiccoloClockControlPanel( clock );
        controlPanel.setBackground( BLANK );
        controlPanel.getButtonCanvas().setBackground( BLANK );
        controlPanel.getBackgroundNode().setVisible( false );
        controlPanel.addTimeControlListener( new TimeControlListener.TimeControlAdapter() {
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

        addChild( new HBox( new PSwing( new SimSharingPropertyRadioButton<Boolean>( EnergySkateParkSimSharing.UserComponents.slowMotionRadioButton, EnergySkateParkResources.getString( "slow.motion" ), module.normalSpeed, false ) {{
            setBackground( new Color( 0, 0, 0, 0 ) );
            setFont( new PhetFont( 16 ) );
        }} ), new PSwing( new SimSharingPropertyRadioButton<Boolean>( EnergySkateParkSimSharing.UserComponents.normalSpeedRadioButton, EnergySkateParkResources.getString( "normal" ), module.normalSpeed, true ) {{
            setBackground( new Color( 0, 0, 0, 0 ) );
            setFont( new PhetFont( 16 ) );
        }} ), new PSwing( controlPanel ) ) );
    }
}