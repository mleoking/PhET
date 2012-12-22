// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.linegraphing.linegame.view.placepoints;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.util.DoubleRange;
import edu.colorado.phet.common.phetcommon.util.RichSimpleObserver;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.linegraphing.common.LGColors;
import edu.colorado.phet.linegraphing.common.LGSimSharing.UserComponents;
import edu.colorado.phet.linegraphing.common.model.Graph;
import edu.colorado.phet.linegraphing.common.model.Line;
import edu.colorado.phet.linegraphing.common.view.GraphNode;
import edu.colorado.phet.linegraphing.common.view.LineNode;
import edu.colorado.phet.linegraphing.common.view.manipulator.LineManipulatorNode;
import edu.colorado.phet.linegraphing.common.view.manipulator.PointDragHandler;
import edu.colorado.phet.linegraphing.linegame.LineGameConstants;
import edu.colorado.phet.linegraphing.linegame.model.LineForm;
import edu.colorado.phet.linegraphing.linegame.model.P3P_Challenge;
import edu.colorado.phet.linegraphing.pointslope.view.PointSlopeLineNode;
import edu.colorado.phet.linegraphing.slopeintercept.view.SlopeInterceptLineNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.nodes.PComposite;

/**
 * Graph node for "Place 3 Points" (P3P) challenges.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class P3P_GraphNode extends GraphNode {

    private final LineNode answerNode;
    private final LineManipulatorNode p1ManipulatorNode, p2ManipulatorNode, p3ManipulatorNode;

    public P3P_GraphNode( final P3P_Challenge challenge ) {
        super( challenge.graph, challenge.mvt );

        // To reduce brain damage during development, show the answer as a translucent gray line.
        if ( PhetApplication.getInstance().isDeveloperControlsEnabled() ) {
            LineNode answerNode = createLineNode( challenge.lineForm, challenge.answer.withColor( new Color( 0, 0, 0, 25 ) ), challenge.graph, challenge.mvt );
            addChild( answerNode );
        }

        // parent for the guess node, to maintain rendering order
        final PNode guessNodeParent = new PComposite();

        // the correct answer, initially hidden
        answerNode = createLineNode( challenge.lineForm, challenge.answer, challenge.graph, challenge.mvt );
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
        addChild( guessNodeParent );
        addChild( answerNode );
        addChild( p1ManipulatorNode );
        addChild( p2ManipulatorNode );
        addChild( p3ManipulatorNode );

        // Show the user's current guess
        final RichSimpleObserver pointsObserver = new RichSimpleObserver() {
            public void update() {

                // draw the line
                guessNodeParent.removeAllChildren();
                if ( challenge.guess.get() != null ) {
                    LineNode guessNode = createLineNode( challenge.lineForm, challenge.guess.get(), challenge.graph, challenge.mvt );
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

    // Sets the visibility of the correct answer.
    public void setAnswerVisible( boolean visible ) {
        answerNode.setVisible( visible );
    }

    // Creates the node for a line.
    private LineNode createLineNode( LineForm lineForm, Line line, Graph graph, ModelViewTransform mvt ) {
        if ( lineForm == LineForm.SLOPE_INTERCEPT ) {
            return new SlopeInterceptLineNode( line, graph, mvt ) {{
                setEquationVisible( false );
            }};
        }
        else {
            return new PointSlopeLineNode( line, graph, mvt ) {{
                setEquationVisible( false );
            }};
        }
    }
}
