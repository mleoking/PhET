//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.buildamolecule.control;

import java.awt.*;

import javax.swing.*;

import edu.colorado.phet.buildamolecule.BuildAMoleculeApplication;
import edu.colorado.phet.common.games.GamesResources;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.controls.PropertyRadioButton;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.pswing.PSwing;

/**
 * Allows the user to turn the global sound for the sim on or off
 * <p/>
 * TODO: consider a "muted" icon where there are no "sound" waves coming from the speaker icon
 * TODO: discuss about methods to share code, phetcommon sound on/off panel?
 */
public class SoundOnOffNode extends PNode {
    public SoundOnOffNode() {
        // speaker image
        final JLabel soundLabel = new JLabel( new ImageIcon( GamesResources.getImage( "sound-icon.png" ) ) ) {{
//            setFont( LABEL_FONT );
        }};

        // ON radio button
        final PropertyRadioButton<Boolean> soundOnRadioButton = new PropertyRadioButton<Boolean>( PhetCommonResources.getString( "Games.radioButton.on" ), BuildAMoleculeApplication.soundEnabled, true ) {{
            setOpaque( false );
//            setFont( CONTROL_FONT );
        }};

        // OFF radio button
        final PropertyRadioButton<Boolean> soundOffRadioButton = new PropertyRadioButton<Boolean>( PhetCommonResources.getString( "Games.radioButton.off" ), BuildAMoleculeApplication.soundEnabled, false ) {{
            setOpaque( false );
//            setFont( CONTROL_FONT );
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
