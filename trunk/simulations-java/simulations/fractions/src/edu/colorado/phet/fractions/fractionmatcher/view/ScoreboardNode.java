// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.fractionmatcher.view;

import java.text.MessageFormat;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction0;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.fractions.FractionsResources.Strings;
import edu.colorado.phet.fractions.buildafraction.view.pictures.RefreshButtonNode;
import edu.colorado.phet.fractions.fractionmatcher.model.MatchingGameModel;
import edu.colorado.phet.fractions.fractionmatcher.model.MatchingGameState;
import edu.umd.cs.piccolo.PNode;

/**
 * Shows the score, timer, menu button, etc.  Cannot use the phetcommon one since this layout is vertical instead of horizontal.
 *
 * @author Sam Reid
 */
class ScoreboardNode extends PNode {

    //Have to reuse buttons since they are animated outside of our model, and if they got reconstructed on each step, they would never animate nor press
    private static final PhetFont font = new PhetFont( 18, true );

    public ScoreboardNode( final MatchingGameModel matchingGameModel ) {
        Property<MatchingGameState> model = matchingGameModel.state;

        final PNode level = text( MessageFormat.format( Strings.LEVEL__PATTERN, model.get().info.level ) );
        final PNode score = text( MessageFormat.format( Strings.SCORE__PATTERN, model.get().info.score ) );
        final PNode optionalTimerValue = model.get().info.timerVisible ? text( MessageFormat.format( Strings.TIME__PATTERN, model.get().info.time / 1000L ) ) : new PNode();

        addChild( new VBox( 20, new VBox( VBox.RIGHT_ALIGNED, level, score, optionalTimerValue ), new RefreshButtonNode( new VoidFunction0() {
            public void apply() {
                matchingGameModel.refresh();
            }
        } ) ) );
    }

    private static PhetPText text( String text ) { return new PhetPText( text, font ); }
}