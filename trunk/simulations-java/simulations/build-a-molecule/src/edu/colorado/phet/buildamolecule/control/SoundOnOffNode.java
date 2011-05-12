//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.buildamolecule.BuildAMoleculeApplication;
import edu.colorado.phet.common.games.GameConstants;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Allows the user to turn the global sound for the sim on or off
 * <p/>
 * TODO: consider a "muted" icon where there are no "sound" waves coming from the speaker icon
 */
public class SoundOnOffNode extends PNode {
    public SoundOnOffNode() {
        // speaker image
        final JLabel soundLabel = new JLabel( new ImageIcon( GameConstants.SOUND_ICON ) );

        // ON radio button
        final PropertyRadioButton<Boolean> soundOnRadioButton = new PropertyRadioButton<Boolean>( GameConstants.RADIO_BUTTON_ON, BuildAMoleculeApplication.soundEnabled, true ) {{
            setOpaque( false );
        }};

        // OFF radio button
        final PropertyRadioButton<Boolean> soundOffRadioButton = new PropertyRadioButton<Boolean>( GameConstants.RADIO_BUTTON_OFF, BuildAMoleculeApplication.soundEnabled, false ) {{
            setOpaque( false );
        }};

        new ButtonGroup() {{
            add( soundOnRadioButton );
            add( soundOffRadioButton );
        }};

        addChild( new PSwing( new JPanel( new FlowLayout( FlowLayout.LEFT ) ) {{
            setOpaque( false );
            add( soundLabel );
            add( soundOnRadioButton );
            add( soundOffRadioButton );
        }} ) );
    }
}
