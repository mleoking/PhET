package edu.colorado.phet.energyskatepark.view.piccolo;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.TimeControlListener;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.colorado.phet.energyskatepark.AbstractEnergySkateParkModule;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.energyskatepark.EnergySkateParkApplication.SIMULATION_TIME_DT;
import static edu.colorado.phet.energyskatepark.EnergySkateParkResources.getString;
import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.UserComponents.normalSpeedRadioButton;
import static edu.colorado.phet.energyskatepark.EnergySkateParkSimSharing.UserComponents.slowMotionRadioButton;

/**
 * Clock control panel that shows "slow motion" and "normal" as radio buttons with a play/pause and step button.
 *
 * @author Sam Reid
 */
public class EnergySkateParkTimeControlPanel extends RichPNode {

    private final Color BLANK = new Color( 0, 0, 0, 0 );
    private final PhetFont RADIO_BUTTON_FONT = new PhetFont( 16 );

    public EnergySkateParkTimeControlPanel( final AbstractEnergySkateParkModule module, final ConstantDtClock clock ) {
        module.normalSpeed.addObserver( new VoidFunction1<Boolean>() {
            public void apply( Boolean normalSpeed ) {
                clock.setDt( normalSpeed ? SIMULATION_TIME_DT : SIMULATION_TIME_DT / 4.0 );
            }
        } );

        PiccoloClockControlPanel controlPanel = new PiccoloClockControlPanel( clock ) {{
            setBackground( BLANK );
            getButtonCanvas().setBackground( BLANK );
            getBackgroundNode().setVisible( false );
            addTimeControlListener( new TimeControlListener.TimeControlAdapter() {
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
        }};

        addChild( new HBox( new PSwing( new PropertyRadioButton<Boolean>( slowMotionRadioButton, getString( "slow.motion" ), module.normalSpeed, false ) {{
            setBackground( BLANK );
            setFont( RADIO_BUTTON_FONT );
        }} ), new PSwing( new PropertyRadioButton<Boolean>( normalSpeedRadioButton, getString( "normal" ), module.normalSpeed, true ) {{
            setBackground( BLANK );
            setFont( RADIO_BUTTON_FONT );
        }} ), new PSwing( controlPanel ) ) );
    }
}