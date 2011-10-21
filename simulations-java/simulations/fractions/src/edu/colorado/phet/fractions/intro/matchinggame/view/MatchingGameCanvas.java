// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fractions.intro.matchinggame.view;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.model.clock.Clock;
import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractions.intro.common.view.AbstractFractionsCanvas;
import edu.colorado.phet.fractions.intro.matchinggame.model.MatchingGameModel;
import edu.colorado.phet.fractions.intro.matchinggame.model.Representation;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * @author Sam Reid
 */
public class MatchingGameCanvas extends AbstractFractionsCanvas {
    private final PNode representationNodes = new PNode();
    public final BalanceNode balanceNode;
    double insetY = -10;

    public MatchingGameCanvas( MatchingGameModel model ) {
        for ( Representation representation : model.fractionRepresentations ) {
            representationNodes.addChild( representation.node );
        }

        balanceNode = new BalanceNode();
        final ZeroOffsetNode zeroOffsetBalanceNode = new ZeroOffsetNode( balanceNode ) {{
            setOffset( STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2, STAGE_SIZE.getHeight() - getFullBounds().getHeight() );
        }};
        addChild( zeroOffsetBalanceNode );

        addChild( representationNodes );

        Clock clock = new ConstantDtClock( 30 );
        clock.addClockListener( new ClockAdapter() {
            @Override public void simulationTimeChanged( ClockEvent clockEvent ) {
                for ( Object fractionRepresentation : representationNodes.getChildrenReference() ) {
                    RepresentationNode node = (RepresentationNode) fractionRepresentation;

                    double dt = clockEvent.getSimulationTimeChange();

                    if ( node.representation.dropped.get() && !node.representation.dragging.get() && isOverBalancePlatform( node ) ) {
                        ImmutableVector2D acceleration = node.representation.force.times( 1.0 / node.representation.mass );
                        node.representation.velocity.set( node.representation.velocity.get().plus( acceleration.times( dt ) ) );
                        node.representation.setOffset( node.representation.getOffset().plus( node.representation.velocity.get().times( dt ) ) );
                    }

                    if ( !node.representation.dragging.get() && node.representation.dropped.get() ) {
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
                double leftWeight = getWeight( balanceNode.leftPlatform );
                double rightWeight = getWeight( balanceNode.rightPlatform );
                double deltaWeight = leftWeight - rightWeight;
                balanceNode.update( deltaWeight * 5 );
            }
        } );
        clock.start();
    }

    private void updateOnPlatform( RepresentationNode node, double insetY ) {
        if ( node.representation.getOverPlatform() != null ) {
            double deltaY = node.representation.getOverPlatform().getGlobalFullBounds().getMinY() - node.getGlobalFullBounds().getMaxY() - insetY;
            node.representation.setOffset( new ImmutableVector2D( node.representation.getOffset().getX(), node.getOffset().getY() + deltaY ) );
        }
    }

    private double getWeight( PImage platform ) {
        double leftWeight = 0.0;
        for ( Object fractionRepresentation : representationNodes.getChildrenReference() ) {
            RepresentationNode node = (RepresentationNode) fractionRepresentation;
            if ( node.representation.getOverPlatform() == platform ) {
                leftWeight += node.representation.getWeight();
            }
        }
        return leftWeight;
    }

    private boolean isOverBalancePlatform( RepresentationNode node ) {
        return isOverPlatform( node, balanceNode.leftPlatform ) || isOverPlatform( node, balanceNode.rightPlatform );
    }

    private boolean isOverPlatform( RepresentationNode node, PImage platform ) {
        double nodeCenter = node.getGlobalFullBounds().getCenterX();
        return platform.getGlobalFullBounds().getMinX() < nodeCenter && platform.getGlobalFullBounds().getMaxX() > nodeCenter;
    }
}