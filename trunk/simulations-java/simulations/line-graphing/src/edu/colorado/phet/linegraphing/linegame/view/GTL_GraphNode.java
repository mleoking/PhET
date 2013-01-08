// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.GraphNode;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.common.view.SlopeToolNode;
import edu.colorado.phet.linegraphing.linegame.model.GTL_Challenge;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for the graph node in all "Graph the Line" (GTL) challenges.
 * Renders the answer line, guess line, and slope tool.
 * Manipulators are provided by subclasses.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class GTL_GraphNode extends GraphNode {

    private final PNode answerNode, slopeToolNode;

    public GTL_GraphNode( final GTL_Challenge challenge, boolean slopeToolEnabled ) {
        super( challenge.graph, challenge.mvt );

        // To reduce brain damage during development, show the answer as a translucent gray line.
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            addChild( new LineNode( challenge.answer.withColor( new Color( 0, 0, 0, 25 ) ), challenge.graph, challenge.mvt ) );
        }

        // the correct answer, initially hidden
        answerNode = new LineNode( challenge.answer, challenge.graph, challenge.mvt );
        answerNode.setVisible( false );

        // parent for the guess node, to maintain rendering order
        final PNode guessNodeParent = new PComposite();

        // Slope tool
        if ( slopeToolEnabled ) {
            slopeToolNode = new SlopeToolNode( challenge.guess, challenge.mvt );
        }
        else {
            slopeToolNode = new PNode();
        }

        // rendering order
        addChild( guessNodeParent );
        addChild( answerNode );
        addChild( slopeToolNode );

        // Sync with the guess
        challenge.guess.addObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {
                guessNodeParent.removeAllChildren();
                if ( line != null ) {
                    guessNodeParent.addChild( new LineNode( line, challenge.graph, challenge.mvt ) );
                }
            }
        } );
    }

    // Sets the visibility of the slope tool.
    public void setSlopeToolVisible( boolean visible ) {
        slopeToolNode.setVisible( visible );
    }

    // Sets the visibility of the correct answer.
    public void setAnswerVisible( boolean visible ) {
        answerNode.setVisible( visible );
    }
}
