// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import fj.Equal;
import fj.F;
import fj.data.List;
import fj.data.Option;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.util.functionaljava.FJUtils;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.DragEvent;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.SimSharingCanvasBoundedDragHandler;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractions.buildafraction.view.DisablePickingWhileAnimating;
import edu.colorado.phet.fractions.common.math.Fraction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain.chain;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components.fractionCard;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.ParameterKeys.denominator;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.ParameterKeys.numerator;

/**
 * Shows a FractionNode on a card and makes it draggable.
 *
 * @author Sam Reid
 */
class FractionCardNode extends RichPNode {
    public final FractionNode fractionNode;
    private final PNode fractionNodeParent;

    public FractionCardNode( final FractionNode fractionNode, final List<NumberSceneCollectionBoxPair> pairList, final NumberSceneNode numberSceneNode ) {
        this.fractionNode = fractionNode;
        this.fractionNode.setCardNode( this );
        this.fractionNodeParent = fractionNode.getParent();

        //Put fraction node in our coordinate frame so the rest of the geometry calculation is easy
        addChild( fractionNode );

        //create an overlay that allows dragging all parts together
        PBounds topBounds = fractionNode.getTopNumberNode().getGlobalFullBounds();
        PBounds bottomBounds = fractionNode.getBottomNumberNode().getGlobalFullBounds();
        Rectangle2D divisorBounds = fractionNode.divisorLine.getGlobalFullBounds();

        //Compute the bounds, different depending on whether it is for a mixed number or (im)proper fraction
        final Rectangle2D fractionPartBounds = topBounds.createUnion( bottomBounds ).createUnion( divisorBounds );
        Rectangle2D borderShape = fractionNode.mixedNumber ?
                                  fractionPartBounds.createUnion( fractionNode.getWholeNumberNode().getGlobalFullBounds() ) :
                                  fractionPartBounds;

        borderShape = globalToLocal( borderShape );
        borderShape = RectangleUtils.expand( borderShape, 10, 2 );

        final PhetPPath cardShapeNode = new PhetPPath( new RoundRectangle2D.Double( borderShape.getX(), borderShape.getY(), borderShape.getWidth(), borderShape.getHeight(), 10, 10 ),
                                                       Color.white, new BasicStroke( 1 ), Color.black );
        cardShapeNode.addInputEventListener( new CursorHandler() );

        cardShapeNode.addInputEventListener( new SimSharingCanvasBoundedDragHandler( chain( fractionCard, FractionCardNode.this.hashCode() ), FractionCardNode.this ) {
            @Override protected ParameterSet getParametersForAllEvents( final PInputEvent event ) {
                return super.getParametersForAllEvents( event ).with( numerator, fractionNode.getTopNumberNode().number ).with( denominator, fractionNode.getBottomNumberNode().number );
            }

            @Override protected void dragNode( final DragEvent event ) {
                moveToFront();
                translate( event.delta.width, event.delta.height );
            }

            @Override public void mouseReleased( final PInputEvent event ) {
                super.mouseReleased( event );

                //Snap to a scoring cell or go back to the play area.
                //If dropped in a non-matching cell, send back to play area
                List<NumberCollectionBoxNode> scoreCells = pairList.map( new F<NumberSceneCollectionBoxPair, NumberCollectionBoxNode>() {
                    @Override public NumberCollectionBoxNode f( final NumberSceneCollectionBoxPair pair ) {
                        return pair.collectionBoxNode;
                    }
                } );
                boolean locked = false;

                //Sort by distance to choose the closest one
                final List<NumberCollectionBoxNode> sortedCells = scoreCells.sort( FJUtils.ord( new F<NumberCollectionBoxNode, Double>() {
                    @Override public Double f( final NumberCollectionBoxNode collectionBoxNode ) {
                        return collectionBoxNode.getGlobalFullBounds().getCenter2D().distance( FractionCardNode.this.getGlobalFullBounds().getCenter2D() );
                    }
                } ) );

                //Only consider the closest box, otherwise students can overlap many boxes instead of thinking of the correct answer
                for ( NumberCollectionBoxNode scoreCell : sortedCells.take( 1 ) ) {
                    if ( cardShapeNode.getGlobalFullBounds().intersects( scoreCell.getGlobalFullBounds() ) &&
                         scoreCell.mixedFraction.approxEquals( fractionNode.getValue() ) &&
                         !scoreCell.isCompleted() ) {

                        //Lock in target cell
                        Point2D targetCenter = scoreCell.getFullBounds().getCenter2D();
                        final double scaleFactor = fractionNode.mixedNumber ? 0.65 : 0.75;

                        Point2D x = fractionNode.getGlobalTranslation();
                        numberSceneNode.addChild( fractionNode );
                        fractionNode.setGlobalTranslation( x );

                        Vector2D offset = fractionNode.mixedNumber ? Vector2D.v( 7, 30 ) : Vector2D.v( 25, 10 );
                        fractionNode.animateToPositionScaleRotation( targetCenter.getX() - fractionNode.getFullBounds().getWidth() / 2 * scaleFactor + offset.x,
                                                                     targetCenter.getY() - fractionNode.getFullBounds().getHeight() / 2 * scaleFactor + offset.y,
                                                                     scaleFactor, 0, BuildAFractionModule.ANIMATION_TIME ).setDelegate( new DisablePickingWhileAnimating( fractionNode, false ) );

                        fractionNode.undoButton.setVisible( false );
                        fractionNode.setDragRegionPickable( false );

                        //Get rid of the card itself
                        removeFromParent();

                        scoreCell.setCompletedFraction( fractionNode );
                        locked = true;

                        numberSceneNode.fractionCardNodeDroppedInCollectionBox();
                        break;
                    }
                }

                //If no match, and is overlapping a score cell or too far to the right, send back to play area
                if ( !locked ) {
                    boolean hitWrongOne = false;
                    for ( NumberCollectionBoxNode scoreCell : scoreCells ) {
                        if ( cardShapeNode.getGlobalFullBounds().intersects( scoreCell.getGlobalFullBounds() ) || cardShapeNode.getGlobalBounds().getMaxX() > numberSceneNode.minimumCollectionBoxX() ) {
                            hitWrongOne = true;
                        }
                    }
                    if ( hitWrongOne ) {

                        //This has the effect of sending it back to where it originally became a CardNode
                        animateToPositionScaleRotation( 0, 0, 1, 0, BuildAFractionModule.ANIMATION_TIME ).setDelegate( new DisablePickingWhileAnimating( FractionCardNode.this, true ) );
                    }
                }

            }
        } );

        fractionNode.setDragRegionPickable( false );
        fractionNode.addUndoListener( new VoidFunction1<Option<Fraction>>() {
            public void apply( final Option<Fraction> fractions ) {
                removeChild( cardShapeNode );
                if ( fractions.isSome() ) {
                    numberSceneNode.level.createdFractions.set( numberSceneNode.level.createdFractions.get().delete( fractions.some(), Equal.<Fraction>anyEqual() ) );
                }
            }
        } );

        addChild( cardShapeNode );
        fractionNode.moveToFront();
    }

    //Split it apart so the FractionNode is no longer shown on this card.
    public void undo() {
        Point2D location = fractionNode.getGlobalTranslation();
        location = fractionNodeParent.globalToLocal( location );
        fractionNode.removeFromParent();
        fractionNodeParent.addChild( fractionNode );
        fractionNode.setOffset( location );
    }
}