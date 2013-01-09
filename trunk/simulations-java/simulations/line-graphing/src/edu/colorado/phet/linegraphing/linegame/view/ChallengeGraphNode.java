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

    /**
     * Graph that initially shows the line to be matched, but not the user's guess.
     * This is used in "Make the Equation" challenges.
     */
    public static class LineToMatch extends ChallengeGraphNode {

        public LineToMatch( Challenge challenge ) {
            super( challenge, true /* slopeToolEnabled */ );
            setAnswerVisible( true );
            setGuessVisible( false );
        }
    }

    /**
     * Graph with manipulators for slope and y-intercept.
     */
    public static class SlopeIntercept extends ChallengeGraphNode {

        public SlopeIntercept( final Challenge challenge ) {
            super( challenge, true /* slopeToolEnabled */ );

            setAnswerVisible( false );
            setGuessVisible( true );

            // dynamic ranges
            SlopeInterceptParameterRange parameterRange = new SlopeInterceptParameterRange();
            final Property<DoubleRange> riseRange = new Property<DoubleRange>( parameterRange.rise( challenge.guess.get(), challenge.graph ) );
            final Property<DoubleRange> runRange = new Property<DoubleRange>( parameterRange.run( challenge.guess.get(), challenge.graph ) );
            final Property<DoubleRange> y1Range = new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) );

            final double manipulatorDiameter = challenge.mvt.modelToViewDeltaX( LineGameConstants.MANIPULATOR_DIAMETER );

            // slope manipulator
            final LineManipulatorNode slopeManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.SLOPE );
            slopeManipulatorNode.addInputEventListener( new SlopeDragHandler( UserComponents.slopeManipulator, UserComponentTypes.sprite,
                                                                              slopeManipulatorNode, challenge.mvt, challenge.guess,
                                                                              riseRange, runRange ) );

            // y-intercept manipulator
            final LineManipulatorNode interceptManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.INTERCEPT );
            interceptManipulatorNode.addInputEventListener( new X1Y1DragHandler( UserComponents.pointManipulator, UserComponentTypes.sprite,
                                                                                 interceptManipulatorNode, challenge.mvt, challenge.guess,
                                                                                 new Property<DoubleRange>( new DoubleRange( 0, 0 ) ), /* x1 is fixed */
                                                                                 y1Range,
                                                                                 true /* constantSlope */ ) );
            // plotted y-intercept
            final double pointDiameter = challenge.mvt.modelToViewDeltaX( LineGameConstants.POINT_DIAMETER );
            final PNode interceptNode = new PlottedPointNode( pointDiameter, LGColors.PLOTTED_POINT );

            // Rendering order
            if ( challenge.manipulationMode == ManipulationMode.INTERCEPT || challenge.manipulationMode == ManipulationMode.SLOPE_INTERCEPT ) {
                addChild( interceptManipulatorNode );
            }
            else {
                addChild( interceptNode );
            }
            if ( challenge.manipulationMode == ManipulationMode.SLOPE || challenge.manipulationMode == ManipulationMode.SLOPE_INTERCEPT ) {
                addChild( slopeManipulatorNode );
            }

            // Sync with the guess
            challenge.guess.addObserver( new VoidFunction1<Line>() {
                public void apply( Line line ) {

                    // move the manipulators
                    slopeManipulatorNode.setOffset( challenge.mvt.modelToView( line.x2, line.y2 ) );
                    interceptManipulatorNode.setOffset( challenge.mvt.modelToView( line.x1, line.y1 ) );
                    interceptNode.setOffset( interceptManipulatorNode.getOffset() );

                    // adjust ranges
                    if ( challenge.manipulationMode == ManipulationMode.SLOPE_INTERCEPT ) {
                        SlopeInterceptParameterRange parameterRange = new SlopeInterceptParameterRange();
                        riseRange.set( parameterRange.rise( line, challenge.graph ) );
                        y1Range.set( parameterRange.y1( line, challenge.graph ) );
                    }
                }
            } );
        }
    }

    /**
     * Graph with manipulators for point (x1,y1) and slope.
     */
    public static class PointSlope extends ChallengeGraphNode {

        public PointSlope( final Challenge challenge ) {
            super( challenge, true /* slopeToolEnabled */ );

            setAnswerVisible( false );
            setGuessVisible( true );

            // dynamic ranges
            final PointSlopeParameterRange pointSlopeParameterRange = new PointSlopeParameterRange();
            final Property<DoubleRange> x1Range = new Property<DoubleRange>( new DoubleRange( challenge.graph.xRange ) );
            final Property<DoubleRange> y1Range = new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) );
            final Property<DoubleRange> riseRange = new Property<DoubleRange>( pointSlopeParameterRange.rise( challenge.guess.get(), challenge.graph ) );
            final Property<DoubleRange> runRange = new Property<DoubleRange>( pointSlopeParameterRange.run( challenge.guess.get(), challenge.graph ) );

            final double manipulatorDiameter = challenge.mvt.modelToViewDeltaX( LineGameConstants.MANIPULATOR_DIAMETER );

            // point (x1,y1) manipulator
            final LineManipulatorNode pointManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.POINT_X1_Y1 );
            pointManipulatorNode.addInputEventListener( new X1Y1DragHandler( UserComponents.pointManipulator, UserComponentTypes.sprite,
                                                                             pointManipulatorNode, challenge.mvt, challenge.guess, x1Range, y1Range,
                                                                             true /* constantSlope */ ) );
            // plotted point (x1,y1)
            final double pointDiameter = challenge.mvt.modelToViewDeltaX( LineGameConstants.POINT_DIAMETER );
            final PNode pointNode = new PlottedPointNode( pointDiameter, LGColors.PLOTTED_POINT );
            pointNode.setOffset( challenge.mvt.modelToView( new Point2D.Double( challenge.guess.get().x1, challenge.guess.get().y1 ) ) );

            // slope manipulator
            final LineManipulatorNode slopeManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.SLOPE );
            slopeManipulatorNode.addInputEventListener( new SlopeDragHandler( UserComponents.slopeManipulator, UserComponentTypes.sprite,
                                                                              slopeManipulatorNode, challenge.mvt, challenge.guess,
                                                                              riseRange, runRange ) );

            // Rendering order
            if ( challenge.manipulationMode == ManipulationMode.POINT || challenge.manipulationMode == ManipulationMode.POINT_SLOPE ) {
                addChild( pointManipulatorNode );
            }
            else {
                addChild( pointNode );
            }
            if ( challenge.manipulationMode == ManipulationMode.SLOPE || challenge.manipulationMode == ManipulationMode.POINT_SLOPE ) {
                addChild( slopeManipulatorNode );
            }

            // Sync with the guess
            challenge.guess.addObserver( new VoidFunction1<Line>() {
                public void apply( Line line ) {

                    // move the manipulators
                    pointManipulatorNode.setOffset( challenge.mvt.modelToView( line.x1, line.y1 ) );
                    pointNode.setOffset( pointManipulatorNode.getOffset() );
                    slopeManipulatorNode.setOffset( challenge.mvt.modelToView( line.x2, line.y2 ) );

                    // adjust ranges
                    if ( challenge.manipulationMode == ManipulationMode.POINT_SLOPE ) {
                        final PointSlopeParameterRange pointSlopeParameterRange = new PointSlopeParameterRange();
                        x1Range.set( pointSlopeParameterRange.x1( line, challenge.graph ) );
                        y1Range.set( pointSlopeParameterRange.y1( line, challenge.graph ) );
                        riseRange.set( pointSlopeParameterRange.rise( line, challenge.graph ) );
                        runRange.set( pointSlopeParameterRange.run( line, challenge.graph ) );
                    }
                }
            } );
        }
    }

    /**
     * Graph with manipulators for 2 points, (x1,y1) and (x2,y2).
     * Note that this graph has no dynamic ranges, because there are no dependencies between the 2 points.
     */
    public static class TwoPoints extends ChallengeGraphNode {

        public TwoPoints( final Challenge challenge ) {
            super( challenge, true /* slopeToolEnabled */ );

            setAnswerVisible( false );
            setGuessVisible( true );

            final double manipulatorDiameter = challenge.mvt.modelToViewDeltaX( LineGameConstants.MANIPULATOR_DIAMETER );

            // (x1,y1) manipulator
            final LineManipulatorNode x1y1ManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.POINT_X1_Y1 );
            x1y1ManipulatorNode.addInputEventListener( new X1Y1DragHandler( UserComponents.x1y1Manipulator, UserComponentTypes.sprite,
                                                                            x1y1ManipulatorNode, challenge.mvt, challenge.guess,
                                                                            new Property<DoubleRange>( new DoubleRange( challenge.graph.xRange ) ),
                                                                            new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) ),
                                                                            false /* constantSlope */ ) );

            // (x2,y2) manipulator
            final LineManipulatorNode x2y2ManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.POINT_X2_Y2 );
            x2y2ManipulatorNode.addInputEventListener( new X2Y2DragHandler( UserComponents.x2y2Manipulator, UserComponentTypes.sprite,
                                                                            x2y2ManipulatorNode, challenge.mvt, challenge.guess,
                                                                            new Property<DoubleRange>( new DoubleRange( challenge.graph.xRange ) ),
                                                                            new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) ) ) );

            // Rendering order
            addChild( x1y1ManipulatorNode );
            addChild( x2y2ManipulatorNode );

            // Sync with the guess
            challenge.guess.addObserver( new VoidFunction1<Line>() {
                public void apply( Line line ) {
                    // move the manipulators
                    x1y1ManipulatorNode.setOffset( challenge.mvt.modelToView( line.x1, line.y1 ) );
                    x2y2ManipulatorNode.setOffset( challenge.mvt.modelToView( line.x2, line.y2 ) );
                }
            } );
        }
    }

    /**
     * Graph with manipulators for 3 arbitrary points, which may or may not form a line.
     * This graph requires a PTP (Place the Points) challenge.
     */
    public static class ThreePoints extends ChallengeGraphNode {

        public ThreePoints( final PTP_Challenge challenge ) {
            super( challenge, false /* slopeToolEnabled */ );

            setAnswerVisible( false );
            setGuessVisible( true );

            final double manipulatorDiameter = challenge.mvt.modelToViewDeltaX( LineGameConstants.MANIPULATOR_DIAMETER );

            // p1 manipulator
            final LineManipulatorNode p1ManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.POINT_1 );
            p1ManipulatorNode.addInputEventListener( new PointDragHandler( UserComponents.p1Manipulator, UserComponentTypes.sprite,
                                                                           p1ManipulatorNode, challenge.mvt,
                                                                           challenge.p1, new Property[] { challenge.p2, challenge.p3 },
                                                                           new Property<DoubleRange>( new DoubleRange( challenge.graph.xRange ) ),
                                                                           new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) ) ) );

            // p1 manipulator
            final LineManipulatorNode p2ManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.POINT_2 );
            p2ManipulatorNode.addInputEventListener( new PointDragHandler( UserComponents.p2Manipulator, UserComponentTypes.sprite,
                                                                           p2ManipulatorNode, challenge.mvt,
                                                                           challenge.p2, new Property[] { challenge.p1, challenge.p3 },
                                                                           new Property<DoubleRange>( new DoubleRange( challenge.graph.xRange ) ),
                                                                           new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) ) ) );

            // p3 manipulator
            final LineManipulatorNode p3ManipulatorNode = new LineManipulatorNode( manipulatorDiameter, LGColors.POINT_3 );
            p3ManipulatorNode.addInputEventListener( new PointDragHandler( UserComponents.p3Manipulator, UserComponentTypes.sprite,
                                                                           p3ManipulatorNode, challenge.mvt,
                                                                           challenge.p3, new Property[] { challenge.p1, challenge.p2 },
                                                                           new Property<DoubleRange>( new DoubleRange( challenge.graph.xRange ) ),
                                                                           new Property<DoubleRange>( new DoubleRange( challenge.graph.yRange ) ) ) );

            // Rendering order
            addChild( p1ManipulatorNode );
            addChild( p2ManipulatorNode );
            addChild( p3ManipulatorNode );

            // Sync with points
            final RichSimpleObserver pointsObserver = new RichSimpleObserver() {
                public void update() {
                    // move the manipulators
                    p1ManipulatorNode.setOffset( challenge.mvt.modelToView( challenge.p1.get().x, challenge.p1.get().y ) );
                    p2ManipulatorNode.setOffset( challenge.mvt.modelToView( challenge.p2.get().x, challenge.p2.get().y ) );
                    p3ManipulatorNode.setOffset( challenge.mvt.modelToView( challenge.p3.get().x, challenge.p3.get().y ) );
                }
            };
            pointsObserver.observe( challenge.p1, challenge.p2, challenge.p3 );
        }
    }
}
