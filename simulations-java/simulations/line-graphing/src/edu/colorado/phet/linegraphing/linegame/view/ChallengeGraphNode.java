// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view;

import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.GraphNode;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.common.view.PlottedPointNode;
import edu.colorado.phet.linegraphing.common.view.SlopeToolNode;
import edu.colorado.phet.linegraphing.common.view.manipulator.LineManipulatorNode;
import edu.colorado.phet.linegraphing.common.view.manipulator.PointDragHandler;
import edu.colorado.phet.linegraphing.common.view.manipulator.SlopeDragHandler;
import edu.colorado.phet.linegraphing.common.view.manipulator.X1Y1DragHandler;
import edu.colorado.phet.linegraphing.common.view.manipulator.X2Y2DragHandler;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.Challenge;
import edu.colorado.phet.linegraphing.linegame.model.ManipulationMode;
import edu.colorado.phet.linegraphing.linegame.model.PTP_Challenge;
import edu.colorado.phet.linegraphing.pointslope.model.PointSlopeParameterRange;
import edu.colorado.phet.linegraphing.slopeintercept.model.SlopeInterceptParameterRange;
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

    private final PNode answerNode, guessNodeParent, slopeToolNode;

    public ChallengeGraphNode( final Challenge challenge, boolean slopeToolEnabled ) {
        super( challenge.graph, challenge.mvt );

        // To reduce brain damage during development, show the answer as a translucent gray line.
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            addChild( new LineNode( challenge.answer.withColor( new Color( 0, 0, 0, 25 ) ), challenge.graph, challenge.mvt ) );
        }

        // the correct answer
        answerNode = new LineNode( challenge.answer, challenge.graph, challenge.mvt );

        // parent for the guess node, to maintain rendering order
        guessNodeParent = new PComposite();

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

    // Sets the visibility of the answer.
    public void setAnswerVisible( boolean visible ) {
        answerNode.setVisible( visible );
    }

    // Sets the visibility of the guess.
    public void setGuessVisible( boolean visible ) {
        guessNodeParent.setVisible( visible );
    }

    // Sets the visibility of the slope tool.
    public void setSlopeToolVisible( boolean visible ) {
        slopeToolNode.setVisible( visible );
    }

}
