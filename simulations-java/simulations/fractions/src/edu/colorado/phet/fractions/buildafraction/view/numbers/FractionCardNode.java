// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import fj.F;
import fj.data.List;
import fj.data.Option;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractions.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractions.common.util.FJUtils;
import edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Shows a FractionNode on a card and makes it draggable.
 *
 * @author Sam Reid
 */
public class FractionCardNode extends RichPNode {
    public final FractionNode fractionNode;
    private final PNode fractionNodeParent;

    public FractionCardNode( final FractionNode fractionNode, final PNode rootNode, final List<Pair> pairList, final BuildAFractionModel model, final NumberSceneNode numberSceneNode ) {
        this.fractionNode = fractionNode;
        this.fractionNode.setCardNode( this );
        this.fractionNodeParent = fractionNode.getParent();

        //Put fraction node in our coordinate frame so the rest of the geometry calculation is easy
        addChild( fractionNode );

        //create an overlay that allows dragging all parts together
        PBounds topBounds = fractionNode.getTopNumberNode().getGlobalFullBounds();
        PBounds bottomBounds = fractionNode.getBottomNumberNode().getGlobalFullBounds();
        Rectangle2D divisorBounds = fractionNode.divisorLine.getGlobalFullBounds();
        Rectangle2D union = topBounds.createUnion( bottomBounds ).createUnion( divisorBounds );

        Rectangle2D borderShape = union;
        borderShape = globalToLocal( borderShape );
        borderShape = RectangleUtils.expand( borderShape, 10, 2 );

        final PhetPPath cardShapeNode = new PhetPPath( new RoundRectangle2D.Double( borderShape.getX(), borderShape.getY(), borderShape.getWidth(), borderShape.getHeight(), 10, 10 ),
                                                       Color.white, new BasicStroke( 1 ), Color.black );
        cardShapeNode.addInputEventListener( new CursorHandler() );

        cardShapeNode.addInputEventListener( new SimSharingDragHandler( null, true ) {
            @Override protected void drag( final PInputEvent event ) {
                super.drag( event );
                moveToFront();
                final PDimension delta = event.getDeltaRelativeTo( rootNode );
                translate( delta.width, delta.height );
            }

            @Override protected void endDrag( final PInputEvent event ) {
                super.endDrag( event );

                //Snap to a scoring cell or go back to the play area.
                //If dropped in a non-matching cell, send back to play area
                List<NumberScoreBoxNode> scoreCells = pairList.map( new F<Pair, NumberScoreBoxNode>() {
                    @Override public NumberScoreBoxNode f( final Pair pair ) {
                        return pair.targetCell;
                    }
                } );
                boolean locked = false;

                //Sort by distance to choose the closest one
                final List<NumberScoreBoxNode> sortedCells = scoreCells.sort( FJUtils.ord( new F<NumberScoreBoxNode, Double>() {
                    @Override public Double f( final NumberScoreBoxNode scoreBoxNode ) {
                        return scoreBoxNode.getGlobalFullBounds().getCenter2D().distance( FractionCardNode.this.getGlobalFullBounds().getCenter2D() );
                    }
                } ) );
                for ( NumberScoreBoxNode scoreCell : sortedCells ) {
                    if ( cardShapeNode.getGlobalFullBounds().intersects( scoreCell.getGlobalFullBounds() ) &&
                         scoreCell.fraction.approxEquals( fractionNode.getValue() ) &&
                         !scoreCell.isCompleted() ) {
                        //Lock in target cell
                        Point2D targetCenter = scoreCell.getFullBounds().getCenter2D();
                        final double scaleFactor = 0.75;

                        Point2D x = fractionNode.getGlobalTranslation();
                        numberSceneNode.addChild( fractionNode );
                        fractionNode.setGlobalTranslation( x );
                        fractionNode.animateToPositionScaleRotation( targetCenter.getX() - fractionNode.getFullBounds().getWidth() / 2 * scaleFactor + 15,
                                                                     targetCenter.getY() - fractionNode.getFullBounds().getHeight() / 2 * scaleFactor + 10,
                                                                     scaleFactor, 0, 200 );

                        fractionNode.splitButton.setVisible( false );
                        fractionNode.setDragRegionPickable( false );

                        //Get rid of the card itself
                        removeFromParent();

                        scoreCell.setCompletedFraction( fractionNode );
                        locked = true;

                        numberSceneNode.fractionCardNodeDragEnded( FractionCardNode.this, event );
                        break;
                    }
                }

                //If no match, and is overlapping a score cell, send back to play area
                if ( !locked ) {
                    boolean hitWrongOne = false;
                    for ( NumberScoreBoxNode scoreCell : scoreCells ) {
                        if ( cardShapeNode.getGlobalFullBounds().intersects( scoreCell.getGlobalFullBounds() ) ) {
                            hitWrongOne = true;
                        }
                    }
                    if ( hitWrongOne ) {

                        //This has the effect of sending it back to where it originally became a CardNode
                        animateToPositionScaleRotation( 0, 0, 1, 0, 1000 );
                    }
                }
            }
        } );
        fractionNode.setDragRegionPickable( false );
        fractionNode.addSplitListener( new VoidFunction1<Option<Fraction>>() {
            public void apply( final Option<Fraction> fractions ) {
                removeChild( cardShapeNode );
                if ( fractions.isSome() ) {
                    model.removeCreatedValueFromNumberLevel( fractions.some() );
                }
            }
        } );

        addChild( cardShapeNode );
        fractionNode.moveToFront();
    }

    public void split() {
        Point2D location = fractionNode.getGlobalTranslation();
        location = fractionNodeParent.globalToLocal( location );
        fractionNode.removeFromParent();
        fractionNodeParent.addChild( fractionNode );
        fractionNode.setOffset( location );
    }
}