// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractionsintro.matchinggame.view;

import fj.F2;
import fj.Ord;
import fj.Ordering;
import fj.data.List;

import java.util.ArrayList;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractionsintro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractionsintro.matchinggame.model.MatchingGameModel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.nodes.PImage;

import static fj.Function.curry;
import static fj.Ord.ord;

/**
 * @author Sam Reid
 */
public class MatchingGameCanvas extends AbstractFractionsCanvas {
    private final PNode representationLayer = new PNode();
    public final BalanceNode balanceNode;
    double insetY = -10;
    private int numFramesBalanced = 0;
    private double scoreY = 0;
    private final ZeroOffsetNode zeroOffsetBalanceNode;
    public static final Random random = new Random();

    public MatchingGameCanvas( MatchingGameModel model ) {
        for ( RepNode representation : model.nodes ) {
            representationLayer.addChild( representation );
        }

        balanceNode = new BalanceNode();
        zeroOffsetBalanceNode = new ZeroOffsetNode( balanceNode ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }};
        addChild( zeroOffsetBalanceNode );

        addChild( representationLayer );

        Clock clock = new ConstantDtClock( 30 );
        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );
        clock.start();
    }

    private void stepInTime( double dt ) {
        for ( Object fractionRepresentation : representationLayer.getChildrenReference() ) {
            RepNode node = (RepNode) fractionRepresentation;

            if ( node.dropped.get() && !node.dragging.get() && isOverBalancePlatform( node ) && !node.scored.get() ) {
                ImmutableVector2D acceleration = node.force.times( 1.0 / node.mass );
                node.velocity.set( node.velocity.get().plus( acceleration.times( dt ) ) );
                node.setOffset( new ImmutableVector2D( node.getOffset() ).plus( node.velocity.get().times( dt ) ) );
            }

            if ( !node.dragging.get() && node.dropped.get() && !node.scored.get() ) {
                if ( node.getGlobalFullBounds().getMaxY() > zeroOffsetBalanceNode.getGlobalFullBounds().getMinY() + insetY ) {
                    node.dropped.set( false );
                    if ( isOverPlatform( node, balanceNode.leftPlatform ) ) {
                        node.setOverPlatform( balanceNode.leftPlatform );
                    }
                    else if ( isOverPlatform( node, balanceNode.rightPlatform ) ) {
                        node.setOverPlatform( balanceNode.rightPlatform );
                    }
                    updateOnPlatform( node, insetY );
                }
            }

            updateOnPlatform( node, insetY );

        }

        //If there are multiple weights on one platform, move off the one that arrived there first
        moveMultipleWeightsOff();

        double leftWeight = getWeight( balanceNode.leftPlatform );
        double rightWeight = getWeight( balanceNode.rightPlatform );
        double deltaWeight = leftWeight - rightWeight;
        balanceNode.update( deltaWeight * 5 );

        if ( leftWeight == rightWeight && leftWeight > 0 ) {
            numFramesBalanced++;
        }
        else {
            numFramesBalanced = 0;
        }

        if ( numFramesBalanced > 30 ) {
            final RepNode left = getNode( balanceNode.leftPlatform );
            final RepNode right = getNode( balanceNode.rightPlatform );
            left.solved();
            right.solved();
            double scaleFactor = 0.5;

            final PNode equalsSign = new PhetPText( "=" );

            double maxHeight = Math.max( left.getFullBounds().getHeight(), right.getFullBounds().getHeight() ) * scaleFactor;

            //Make it so the nodes can't be dragged any more
            left.setPickable( false );
            left.setChildrenPickable( false );
            right.setPickable( false );
            right.setChildrenPickable( false );

            //Make them fly to the summary board
            right.animateToPositionScaleRotation( left.getFullBounds().getWidth() * scaleFactor + 5 + equalsSign.getFullBounds().getWidth() + 5, scoreY + maxHeight / 2 - right.getFullBounds().getHeight() * scaleFactor / 2, right.getScale() * scaleFactor, 0, 1000 );
            PTransformActivity transformLeft = left.animateToPositionScaleRotation( 0, scoreY + maxHeight / 2 - left.getFullBounds().getHeight() * scaleFactor / 2, left.getScale() * scaleFactor, 0, 1000 );
            transformLeft.setDelegate( new PActivity.PActivityDelegate() {
                public void activityStarted( PActivity activity ) {
                }

                public void activityStepped( PActivity activity ) {
                }

                public void activityFinished( PActivity activity ) {
                    equalsSign.setOffset( left.getFullBounds().getMaxX() + 5, left.getFullBounds().getCenterY() - equalsSign.getFullBounds().getHeight() / 2 );
                    addChild( equalsSign );
                    scoreY = Math.max( left.getFullBounds().getMaxY(), right.getFullBounds().getMaxY() );
                }
            } );
        }
    }

    private void moveMultipleWeightsOff() {
        moveMultipleWeightsOff( balanceNode.leftPlatform, -150 );
        moveMultipleWeightsOff( balanceNode.rightPlatform, 150 );
    }

    //Prevent weights from sitting on the same platform
    private void moveMultipleWeightsOff( PImage platform, double dx ) {
        List<RepNode> sorted = getNodesOnPlatform( platform ).sort( ord( curry( new F2<RepNode, RepNode, Ordering>() {
            public Ordering f( final RepNode u1, final RepNode u2 ) {
                return Ord.<Comparable>comparableOrd().compare( u1.getTimeArrivedOnPlatform(), u2.getTimeArrivedOnPlatform() );
            }
        } ) ) );

        //Move off the object that has been there for the longest time
        if ( sorted.length() > 1 ) {
            sorted.head().setOverPlatform( null );
            sorted.head().animateToPositionScaleRotation( sorted.head().getXOffset() + dx, sorted.head().getYOffset() - 100 - random.nextDouble() * 300, sorted.head().getScale(), 0, 1000 );
        }
    }

    private List<RepNode> getNodesOnPlatform( final PImage platform ) {
        return List.iterableList( new ArrayList<RepNode>() {{
            for ( Object fractionRepresentation : representationLayer.getChildrenReference() ) {
                RepNode node = (RepNode) fractionRepresentation;
                if ( node.getOverPlatform() == platform ) {
                    add( node );
                }
            }
        }} );
    }

    private void updateOnPlatform( RepNode node, double insetY ) {
        if ( node.getOverPlatform() != null ) {
            double deltaY = node.getOverPlatform().getGlobalFullBounds().getMinY() - node.getGlobalFullBounds().getMaxY() - insetY;
            node.setOffset( new ImmutableVector2D( node.getOffset().getX(), node.getOffset().getY() + deltaY ) );
        }
    }

    private double getWeight( PImage platform ) {
        return getNodesOnPlatform( platform ).foldLeft( new F2<Double, RepNode, Double>() {
            @Override public Double f( Double sum, RepNode node ) {
                return sum + node.getWeight();
            }
        }, 0.0 );
    }

    private RepNode getNode( PImage platform ) {
        for ( Object fractionRepresentation : representationLayer.getChildrenReference() ) {
            RepNode node = (RepNode) fractionRepresentation;
            if ( node.getOverPlatform() == platform ) {
                return node;
            }
        }
        return null;
    }

    private boolean isOverBalancePlatform( RepNode node ) {
        return isOverPlatform( node, balanceNode.leftPlatform ) || isOverPlatform( node, balanceNode.rightPlatform );
    }

    private boolean isOverPlatform( RepNode node, PImage platform ) {
        double nodeCenter = node.getGlobalFullBounds().getCenterX();
        return platform.getGlobalFullBounds().getMinX() < nodeCenter && platform.getGlobalFullBounds().getMaxX() > nodeCenter;
    }
}