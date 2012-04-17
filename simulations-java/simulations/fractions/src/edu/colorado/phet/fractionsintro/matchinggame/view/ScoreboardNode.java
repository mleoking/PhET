package edu.colorado.phet.fractionsintro.matchinggame.view;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows the score, timer, menu button, etc.  Cannot use the phetcommon one since this layout is vertical instead of horizontal.
 *
 * @author Sam Reid
 */
public class ScoreboardNode extends PNode {
    public ScoreboardNode( final SettableProperty<MatchingGameState> model ) {
        addChild( new VBox( new PhetPText( "Score", new PhetFont( 16, true ) ),
                            new PhetPText( model.get().info.score + "", new PhetFont( 16, true ) ) ) );
    }
}