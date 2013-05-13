// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.game.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.TextButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;

/**
 * This class presents a dialog to the user that informs them that the need to
 * return to the lab mode, and sends them there when the button is pressed.
 *
 * @author John Blanco
 */
public class ReturnToLabDialog extends PNode {

    private static final Font TEXT_FONT = new PhetFont( 18 );
    private static final Color BACKGROUND_COLOR = new Color( 234, 234, 174 );

    private TextButtonNode returnToLabButton;

    /**
     * Constructor.
     */
    public ReturnToLabDialog( ActionListener buttonActionListener ) {

        // Add the textual prompt.
        PText prompt = new PText( "That wasn't the right answer." );
        prompt.setFont( TEXT_FONT );

        // Add a handler for the case where the user presses the Enter key.
        // TODO - add this.

        // Create the button.
        returnToLabButton = new TextButtonNode( "Back to practice screen", new PhetFont( 20 ), Color.YELLOW );
        returnToLabButton.addActionListener( buttonActionListener );

        // Lay out the node.
        addChild( new ControlPanelNode( new VBox( 10, prompt, returnToLabButton ), BACKGROUND_COLOR, new BasicStroke( 1 ), Color.BLACK, 30 ) );
    }
}
