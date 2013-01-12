// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.GraphNode;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.common.view.PlottedPointNode;
import edu.colorado.phet.linegraphing.common.view.SlopeToolNode;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.Challenge;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Base class for graph node in game challenges.
 * Renders the answer line, guess line, and slope tool.
 * Optional manipulators are provided by subclasses.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class ChallengeGraphNode extends GraphNode {

    private final PNode answerParentNode, guessParentNode, slopeToolNode, answerPointNode;
    private PNode guessPointNode;
    private boolean guessPointVisible = true;

    public ChallengeGraphNode( final Challenge challenge, boolean slopeToolEnabled ) {
        super( challenge.graph, challenge.mvt );

        // To reduce brain damage during development, show the answer as a translucent gray line.
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            addChild( new LineNode( challenge.answer.withColor( new Color( 0, 0, 0, 25 ) ), challenge.graph, challenge.mvt ) );
        }

        final double pointDiameter = challenge.mvt.modelToViewDeltaX( LineGameConstants.POINT_DIAMETER );

        // answer
        {
            answerParentNode = new PComposite();
            PNode answerNode = new LineNode( challenge.answer, challenge.graph, challenge.mvt );
            answerParentNode.addChild( answerNode );

            // point (x1,y1) for answer
            answerPointNode = new PlottedPointNode( pointDiameter, challenge.answer.color );
            answerParentNode.addChild( answerPointNode );
            answerPointNode.setOffset( challenge.mvt.modelToView( new Point2D.Double( challenge.answer.x1, challenge.answer.y1 ) ) );
        }

        // guess
        guessParentNode = new PComposite();

        // slope tool
        slopeToolNode = slopeToolEnabled ? new SlopeToolNode( challenge.guess, challenge.mvt ) : new PNode();

        // rendering order
        addChild( guessParentNode );
        addChild( answerParentNode );
        addChild( slopeToolNode );

        // Sync with the guess
        challenge.guess.addObserver( new VoidFunction1<Line>() {
            public void apply( Line line ) {

                guessParentNode.removeAllChildren();
                guessPointNode = null;
                if ( line != null ) {

                    // draw the line
                    guessParentNode.addChild( new LineNode( line, challenge.graph, challenge.mvt ) );

                    // plot (x1,y1)
                    guessPointNode = new PlottedPointNode( pointDiameter, line.color );
                    guessPointNode.setVisible( guessPointVisible );
                    guessParentNode.addChild( guessPointNode );
                    guessPointNode.setOffset( challenge.mvt.modelToView( line.x1, line.y1 ) );
                }
            }
        } );
    }

    // Sets the visibility of the answer.
    public void setAnswerVisible( boolean visible ) {
        answerParentNode.setVisible( visible );
    }

    // Sets the visibility of the guess.
    public void setGuessVisible( boolean visible ) {
        guessParentNode.setVisible( visible );
    }

    // Sets the visibility of the slope tool for the guess.
    public void setSlopeToolVisible( boolean visible ) {
        slopeToolNode.setVisible( visible );
    }

    // Sets the visibility of (x1,y1) for the answer.
    public void setAnswerPointVisible( boolean visible ) {
        answerPointNode.setVisible( visible );
    }

    // Sets the visibility of (x1,y1) for the guess.
    public void setGuessPointVisible( boolean visible ) {
        guessPointVisible = visible;
        if ( guessPointNode != null ) {
            guessPointNode.setVisible( guessPointVisible );
        }
    }
}
