// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyskatepark.view.piccolo;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.mediabuttons.PiccoloClockControlPanel;
import edu.umd.cs.piccolox.pswing.PSwing;

import static edu.colorado.phet.energyskatepark.EnergySkateParkResources.getString;

/**
 * Clock control panel that shows "slow motion" and "normal" as radio buttons with a play/pause and step button.
 *
 * @author Sam Reid
 */
public class SlowMotionNormalTimeControlPanel extends RichPNode {

    private final Color BLANK = new Color( 0, 0, 0, 0 );
    private final PhetFont RADIO_BUTTON_FONT = new PhetFont( 16 );
    protected final PiccoloClockControlPanel piccoloClockControlPanel;

    public SlowMotionNormalTimeControlPanel( IUserComponent slowMotionRadioButton, IUserComponent normalSpeedRadioButton, SettableProperty<Boolean> normalSpeed, IClock clock ) {
        piccoloClockControlPanel = new PiccoloClockControlPanel( clock ) {{
            setBackground( BLANK );
            getButtonCanvas().setBackground( BLANK );
            getBackgroundNode().setVisible( false );
        }};

        addChild( new HBox( new PSwing( new PropertyRadioButton<Boolean>( slowMotionRadioButton, getString( "slow.motion" ), normalSpeed, false ) {{
            setBackground( BLANK );
            setFont( RADIO_BUTTON_FONT );
        }} ), new PSwing( new PropertyRadioButton<Boolean>( normalSpeedRadioButton, getString( "normal" ), normalSpeed, true ) {{
            setBackground( BLANK );
            setFont( RADIO_BUTTON_FONT );
        }} ), new PSwing( piccoloClockControlPanel ) ) );
    }
}