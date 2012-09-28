// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view.graphtheline;

import edu.colorado.phet.common.games.GameAudioPlayer;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.LineManipulatorNode;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.common.view.X1Y1DragHandler;
import edu.colorado.phet.linegraphing.linegame.model.LineGameModel;
import edu.colorado.phet.linegraphing.linegame.model.graphtheline.GTL_Challenge;
import edu.colorado.phet.linegraphing.linegame.view.GameConstants;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * View component for a "Graph the Line" (GTL) challenge.
 * Given an equation in point-slope (PS) form, graph the line by manipulating the Point.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class GTL_PS_Point_ChallengeNode extends GTL_PS_ChallengeNode {

    public GTL_PS_Point_ChallengeNode( final LineGameModel model, GTL_Challenge challenge, final GameAudioPlayer audioPlayer, PDimension challengeSize ) {
        super( model, challenge, audioPlayer, challengeSize );
    }

    // Creates the graph portion of the view.
    @Override protected GTL_ChallengeGraphNode createGraphNode( Graph graph, Property<Line> guessLine, Line answerLine, ModelViewTransform mvt ) {
        return new ThisGraphNode( graph, guessLine, answerLine, mvt );
    }

    // Graph for this challenge
    private static class ThisGraphNode extends GTL_PS_ChallengeGraphNode {

        private final LineNode answerNode;
        private final LineManipulatorNode pointManipulatorNode;

        public ThisGraphNode( final Graph graph,
                              Property<Line> guessLine,
                              Line answerLine,
                              final ModelViewTransform mvt ) {
            super( graph, answerLine, mvt );

            // parent for the guess node, to maintain rendering order
            final PNode guessNodeParent = new PComposite();

            // the correct answer, initially hidden
            answerNode = createAnswerLineNode( answerLine, graph, mvt );
            answerNode.setEquationVisible( false );
            answerNode.setVisible( false );

            // point manipulator
            final double manipulatorDiameter = mvt.modelToViewDeltaX( GameConstants.MANIPULATOR_DIAMETER );
            pointManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.POINT_X1_Y1 );
            pointManipulatorNode.addInputEventListener( new X1Y1DragHandler( UserComponents.pointManipulator, UserComponentTypes.sprite,
                                                                             pointManipulatorNode, mvt, guessLine,
                                                                             new Property<DoubleRange>( new DoubleRange( graph.xRange ) ),
                                                                             new Property<DoubleRange>( new DoubleRange( graph.yRange ) ),
                                                                             true /* constantSlope */ ) );

            // Rendering order
            addChild( guessNodeParent );
            addChild( answerNode );
            addChild( pointManipulatorNode );

            // Show the user's current guess
            guessLine.addObserver( new VoidFunction1<Line>() {
                public void apply( Line line ) {

                    // draw the line
                    guessNodeParent.removeAllChildren();
                    LineNode guessNode = createGuessLineNode( line, graph, mvt );
                    guessNode.setEquationVisible( false );
                    guessNodeParent.addChild( guessNode );

                    // move the manipulator
                    pointManipulatorNode.setOffset( mvt.modelToView( line.x1, line.y1 ) );
                }
            } );
        }

        // Sets the visibility of the correct answer. When answer is visible, manipulators are hidden.
        public void setAnswerVisible( boolean visible ) {
            answerNode.setVisible( visible );
        }
    }
}
