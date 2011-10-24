// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.view;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.function.Function2;
import edu.colorado.phet.common.piccolophet.nodes.PhetPText;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractions.intro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractions.intro.matchinggame.model.MatchingGameModel;
import edu.colorado.phet.fractions.intro.matchinggame.model.Representation;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * @author Sam Reid
 */
public class MatchingGameCanvas extends AbstractFractionsCanvas {
    private final PNode representationNodes = new PNode();
    public final BalanceNode balanceNode;
    double insetY = -10;
    private int numFramesBalanced = 0;
    private double scoreY = 0;
    private final ZeroOffsetNode zeroOffsetBalanceNode;

    public MatchingGameCanvas( MatchingGameModel model ) {
        for ( Representation representation : model.fractionRepresentations ) {
            representationNodes.addChild( representation.node );
        }

        balanceNode = new BalanceNode();
        zeroOffsetBalanceNode = new ZeroOffsetNode( balanceNode ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }};
        addChild( zeroOffsetBalanceNode );

        addChild( representationNodes );

        Clock clock = new ConstantDtClock( 30 );
        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
                stepInTime( clockEvent.getSimulationTimeChange() );
            }
        } );
        clock.start();
    }

    private void stepInTime( double dt ) {
        for ( Object fractionRepresentation : representationNodes.getChildrenReference() ) {
            RepresentationNode node = (RepresentationNode) fractionRepresentation;

            if ( node.representation.dropped.get() && !node.representation.dragging.get() && isOverBalancePlatform( node ) && !node.representation.scored.get() ) {
                ImmutableVector2D acceleration = node.representation.force.times( 1.0 / node.representation.mass );
                node.representation.velocity.set( node.representation.velocity.get().plus( acceleration.times( dt ) ) );
                node.representation.setOffset( node.representation.getOffset().plus( node.representation.velocity.get().times( dt ) ) );
            }

            if ( !node.representation.dragging.get() && node.representation.dropped.get() && !node.representation.scored.get() ) {
                if ( node.getGlobalFullBounds().getMaxY() > zeroOffsetBalanceNode.getGlobalFullBounds().getMinY() + insetY ) {
                    node.representation.dropped.set( false );
                    if ( isOverPlatform( node, balanceNode.leftPlatform ) ) {
                        node.representation.setOverPlatform( balanceNode.leftPlatform );
                    }
                    else if ( isOverPlatform( node, balanceNode.rightPlatform ) ) {
                        node.representation.setOverPlatform( balanceNode.rightPlatform );
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
            final RepresentationNode left = getNode( balanceNode.leftPlatform );
            final RepresentationNode right = getNode( balanceNode.rightPlatform );
            left.solved();
            right.solved();
            double scaleFactor = 0.5;

            final PNode equalsSign = new PhetPText( "=" );

            double maxHeight = Math.max( left.getFullBounds().getHeight(), right.getFullBounds().getHeight() ) * scaleFactor;

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
        moveMultipleWeightsOff( balanceNode.leftPlatform );
        moveMultipleWeightsOff( balanceNode.rightPlatform );
    }

    private void moveMultipleWeightsOff( PImage platform ) {
        ObservableList<RepresentationNode> nodes = getNodesOnPlatform( platform );
    }

    private ObservableList<RepresentationNode> getNodesOnPlatform( final PImage platform ) {
        return new ObservableList<RepresentationNode>() {{
            for ( Object fractionRepresentation : representationNodes.getChildrenReference() ) {
                RepresentationNode node = (RepresentationNode) fractionRepresentation;
                if ( node.representation.getOverPlatform() == platform ) {
                    add( node );
                }
            }
        }};
    }

    private void updateOnPlatform( RepresentationNode node, double insetY ) {
        if ( node.representation.getOverPlatform() != null ) {
            double deltaY = node.representation.getOverPlatform().getGlobalFullBounds().getMinY() - node.getGlobalFullBounds().getMaxY() - insetY;
            node.representation.setOffset( new ImmutableVector2D( node.representation.getOffset().getX(), node.getOffset().getY() + deltaY ) );
        }
    }

    private double getWeight( PImage platform ) {
        return getNodesOnPlatform( platform ).foldLeft( 0.0, new Function2<RepresentationNode, Double, Double>() {
            public Double apply( RepresentationNode node, Double sum ) {
                return node.representation.getWeight() + sum;
            }
        } );
    }

    private RepresentationNode getNode( PImage platform ) {
        for ( Object fractionRepresentation : representationNodes.getChildrenReference() ) {
            RepresentationNode node = (RepresentationNode) fractionRepresentation;
            if ( node.representation.getOverPlatform() == platform ) {
                return node;
            }
        }
        return null;
    }

    private boolean isOverBalancePlatform( RepresentationNode node ) {
        return isOverPlatform( node, balanceNode.leftPlatform ) || isOverPlatform( node, balanceNode.rightPlatform );
    }

    private boolean isOverPlatform( RepresentationNode node, PImage platform ) {
        double nodeCenter = node.getGlobalFullBounds().getCenterX();
        return platform.getGlobalFullBounds().getMinX() < nodeCenter && platform.getGlobalFullBounds().getMaxX() > nodeCenter;
    }
}