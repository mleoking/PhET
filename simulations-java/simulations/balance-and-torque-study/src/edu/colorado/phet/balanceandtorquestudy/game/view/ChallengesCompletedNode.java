// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.game.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;

import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.ControlPanelNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLNode;
import edu.umd.cs.piccolo.PNode;

/**
 * This class presents a message to the user indicating that they have
 * completed all of the challenges.
 *
 * @author John Blanco
 */
public class ChallengesCompletedNode extends PNode {

    private static final Font TEXT_FONT = new PhetFont( 36 );
    private static final Color BACKGROUND_COLOR = new Color( 255, 255, 122 );

    /**
     * Constructor.
     */
    public ChallengesCompletedNode() {
        addChild( new ControlPanelNode( new HTMLNode( "<center>You have successfully<br>completed all challenges.<br><br>Nice job!</center>", Color.BLACK, TEXT_FONT ),
                                        BACKGROUND_COLOR,
                                        new BasicStroke( 1 ),
                                        Color.BLACK,
                                        30 ) );
    }
}
