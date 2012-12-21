// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view.placepoints;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.common.view.manipulator.LineManipulatorNode;
import edu.colorado.phet.linegraphing.common.view.manipulator.PointDragHandler;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.placepoints.P3P_Challenge;
import edu.colorado.phet.linegraphing.pointslope.view.PointSlopeLineNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Graph for all "Place 3 Point" (P3P) challenges that use point-slope (PS) form.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class P3P_PS_GraphNode extends P3P_GraphNode {

    private final LineNode answerNode;
    private final LineManipulatorNode p1ManipulatorNode, p2ManipulatorNode, p3ManipulatorNode;

    public P3P_PS_GraphNode( final P3P_Challenge challenge ) {
        super( challenge );

        // parent for the guess node, to maintain rendering order
        final PNode guessNodeParent = new PComposite();

        // the correct answer, initially hidden
        answerNode = createAnswerLineNode( challenge.answer, challenge.graph, challenge.mvt );
        answerNode.setEquationVisible( false );
        answerNode.setVisible( false );

        final double manipulatorDiameter = challenge.mvt.modelToViewDeltaX( LineGameConstants.MANIPULATOR_DIAMETER );

        // p1 manipulator
        p1ManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.POINT_1 );
        p1ManipulatorNode.addInputEventListener( new PointDragHandler( UserComponents.p1Manipulator, UserComponentTypes.sprite,
                                                                       p1ManipulatorNode, challenge.mvt,
                                                                       challenge.p1, new Property[] { challenge.p2, challenge.p3 },
                                                                       new Property<DoubleRange>( new DoubleRange( challenge.graph.xRange ) ),
                                                                       new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) ) ) );

         // p1 manipulator
        p2ManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.POINT_2 );
        p2ManipulatorNode.addInputEventListener( new PointDragHandler( UserComponents.p2Manipulator, UserComponentTypes.sprite,
                                                                       p2ManipulatorNode, challenge.mvt,
                                                                       challenge.p2, new Property[] { challenge.p1, challenge.p3 },
                                                                       new Property<DoubleRange>( new DoubleRange( challenge.graph.xRange ) ),
                                                                       new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) ) ) );

         // p3 manipulator
        p3ManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.POINT_3 );
        p3ManipulatorNode.addInputEventListener( new PointDragHandler( UserComponents.p3Manipulator, UserComponentTypes.sprite,
                                                                       p3ManipulatorNode, challenge.mvt,
                                                                       challenge.p3, new Property[] { challenge.p1, challenge.p2 },
                                                                       new Property<DoubleRange>( new DoubleRange( challenge.graph.xRange ) ),
                                                                       new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) ) ) );

        // Rendering order
        addLineNode( guessNodeParent );
        addLineNode( answerNode );
        addManipulatorNode( p1ManipulatorNode );
        addManipulatorNode( p2ManipulatorNode );
        addManipulatorNode( p3ManipulatorNode );

        // Show the user's current guess
        final RichSimpleObserver pointsObserver = new RichSimpleObserver() {
            public void update() {

                // draw the line
                guessNodeParent.removeAllChildren();
                if ( challenge.guess.get() != null ) {
                    LineNode guessNode = createGuessLineNode( challenge.guess.get(), challenge.graph, challenge.mvt );
                    guessNode.setEquationVisible( false );
                    guessNodeParent.addChild( guessNode );
                }

                // move the manipulators
                p1ManipulatorNode.setOffset( challenge.mvt.modelToView( challenge.p1.get().x, challenge.p1.get().y ) );
                p2ManipulatorNode.setOffset( challenge.mvt.modelToView( challenge.p2.get().x, challenge.p2.get().y ) );
                p3ManipulatorNode.setOffset( challenge.mvt.modelToView( challenge.p3.get().x, challenge.p3.get().y ) );
            }
        };
        pointsObserver.observe( challenge.p1, challenge.p2, challenge.p3 );
    }


    // Sets the visibility of the correct answer. When answer is visible, manipulators are hidden.
    public void setAnswerVisible( boolean visible ) {
        answerNode.setVisible( visible );
    }

    @Override protected LineNode createAnswerLineNode( Line line, Graph graph, ModelViewTransform mvt ) {
        return new PointSlopeLineNode( line, graph, mvt );
    }

    @Override protected LineNode createGuessLineNode( Line line, Graph graph, ModelViewTransform mvt ) {
        return new PointSlopeLineNode( line, graph, mvt );
    }
}
