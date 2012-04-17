package edu.colorado.phet.fractionsintro.matchinggame.view;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.Spacer;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameState;
import edu.colorado.phet.fractionsintro.matchinggame.model.Mode;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows the score, timer, menu button, etc.  Cannot use the phetcommon one since this layout is vertical instead of horizontal.
 *
 * @author Sam Reid
 */
public class ScoreboardNode extends PNode {
    public ScoreboardNode( final SettableProperty<MatchingGameState> model ) {
        final PhetFont font = new PhetFont( 16, true );
        addChild( new VBox( 0, new PhetPText( "Level", font ),
                            new PhetPText( model.get().info.level + "", font ),
                            new Spacer( 0, 0, 5, 10 ),
                            new PhetPText( "Score", font ),
                            new PhetPText( model.get().info.score + "", font ),
                            new Spacer( 0, 0, 5, 10 ),
                            new Button( Components.menuButton, "menu", Color.yellow, ImmutableVector2D.ZERO, new ActionListener() {
                                @Override public void actionPerformed( final ActionEvent e ) {
                                    model.set( model.get().withMode( Mode.CHOOSING_SETTINGS ) );
                                }
                            } ) ) );
    }
}