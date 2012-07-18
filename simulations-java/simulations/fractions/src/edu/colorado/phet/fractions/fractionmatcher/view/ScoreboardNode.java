// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionmatcher.view;

import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.model.property.SettableProperty;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.fractionmatcher.model.MatchingGameState;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows the score, timer, menu button, etc.  Cannot use the phetcommon one since this layout is vertical instead of horizontal.
 *
 * @author Sam Reid
 */
class ScoreboardNode extends PNode {

    //Have to reuse buttons since they are animated outside of our model, and if they got reconstructed on each step, they would never animate nor press
    private static final PhetFont font = new PhetFont( 16, true );

    public ScoreboardNode( final SettableProperty<MatchingGameState> model ) {

        final PNode optionalTimerValue = model.get().info.timerVisible ? text( MessageFormat.format( Strings.TIME_READOUT__PATTERN, model.get().info.time / 1000L ) ) : new PNode();
        final PNode optionalTimerText = model.get().info.timerVisible ? text( Strings.TIME ) : new PNode();

        final VBox textBox = new VBox( 3, VBox.LEFT_ALIGNED, text( Strings.LEVEL ),
                                       text( Strings.SCORE ),
                                       optionalTimerText );
        final VBox valueBox = new VBox( 3, VBox.LEFT_ALIGNED,
                                        text( model.get().info.level + "" ),
                                        text( model.get().info.score + "" ),
                                        optionalTimerValue );

        addChild( new VBox( new HBox( textBox, valueBox ) ) );
    }

    private static PhetPText text( String text ) { return new PhetPText( text, font ); }
}