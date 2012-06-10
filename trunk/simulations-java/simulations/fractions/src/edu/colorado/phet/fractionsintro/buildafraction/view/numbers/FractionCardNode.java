package edu.colorado.phet.fractionsintro.buildafraction.view.numbers;

import fj.F;
import fj.data.List;
import fj.data.Option;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.FaceNode;
import edu.colorado.phet.common.piccolophet.nodes.HTMLImageButtonNode;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractionsintro.buildafraction.model.BuildAFractionModel;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class FractionCardNode extends RichPNode {
    private final NumberSceneNode numberSceneNode;
    private final FractionNode fractionNode;
    private final PNode fractionNodeParent;

    public FractionCardNode( final FractionNode fractionNode, final PNode rootNode, final List<Pair> pairList, final BuildAFractionModel model, final NumberSceneNode numberSceneNode ) {
        this.numberSceneNode = numberSceneNode;
        this.fractionNode = fractionNode;
        this.fractionNode.setCardNode( this );
        this.fractionNodeParent = fractionNode.getParent();

        //create an overlay that allows dragging all parts together
        PBounds topBounds = fractionNode.getTopNumberNode().getGlobalFullBounds();
        PBounds bottomBounds = fractionNode.getBottomNumberNode().getGlobalFullBounds();
        Rectangle2D divisorBounds = fractionNode.divisorLine.getGlobalFullBounds();
        Rectangle2D union = topBounds.createUnion( bottomBounds ).createUnion( divisorBounds );

        //For debugging, show a yellow border
        Rectangle2D expanded = RectangleUtils.expand( union, 10, 2 );
        expanded = globalToLocal( expanded );
        expanded = localToParent( expanded );

        final PhetPPath cardShapeNode = new PhetPPath( new RoundRectangle2D.Double( expanded.getX(), expanded.getY(), expanded.getWidth(), expanded.getHeight(), 10, 10 ),
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
                List<ScoreBoxNode> scoreCells = pairList.map( new F<Pair, ScoreBoxNode>() {
                    @Override public ScoreBoxNode f( final Pair pair ) {
                        return pair.targetCell;
                    }
                } );
                boolean locked = false;
                for ( ScoreBoxNode scoreCell : scoreCells ) {
                    if ( cardShapeNode.getGlobalFullBounds().intersects( scoreCell.getGlobalFullBounds() ) && scoreCell.fraction.approxEquals( fractionNode.getValue() ) ) {
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

                        //Add a new fraction skeleton when the previous one is completed
                        if ( !allTargetsComplete() ) {

                            //If no fraction skeleton in play area, move one there
                            if ( numberSceneNode.allIncompleteFractionsInToolbox() ) {
                                FractionNode g = null;
                                for ( FractionNode graphic : numberSceneNode.fractionGraphics ) {
                                    if ( graphic.isInToolboxPosition() ) {
                                        g = graphic;
                                    }
                                }
                                if ( g != null ) {
                                    g.animateToPositionScaleRotation( numberSceneNode.toolboxNode.getCenterX() - fractionNode.getFullBounds().getWidth() / 2, 300, 1, 0, 1000 );
                                }
                            }
                        }

                        //but if all filled up, then add a "next" button
                        else {
                            final ActionListener nextLevel = new ActionListener() {
                                public void actionPerformed( final ActionEvent e ) {
                                    numberSceneNode.context.nextNumberLevel();
                                }
                            };
                            numberSceneNode.addChild( new VBox( new FaceNode( 300 ), new HTMLImageButtonNode( "Next", Color.orange ) {{
                                addActionListener( nextLevel );
                            }} ) {{
                                setOffset( numberSceneNode.STAGE_SIZE.getWidth() / 2 - getFullBounds().getWidth() / 2 - 100, numberSceneNode.STAGE_SIZE.getHeight() / 2 - getFullBounds().getHeight() / 2 - 100 );
                            }} );
                        }
                    }
                }

                //If no match, and is overlapping a score cell, send back to play area
                if ( !locked ) {
                    boolean hitWrongOne = false;
                    for ( ScoreBoxNode scoreCell : scoreCells ) {
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

        Point2D location = fractionNode.getGlobalTranslation();

        addChild( fractionNode );
        fractionNode.setGlobalTranslation( location );

        //I Don't know why this offset is necessary; couldn't figure it out.  Perhaps due to the card inset, but it doesn't exactly match up.
        translate( 0, -15 );
    }

    private boolean allTargetsComplete() {
        return numberSceneNode.pairList.map( new F<Pair, Boolean>() {
            @Override public Boolean f( final Pair pair ) {
                return pair.targetCell.isCompleted();
            }
        } ).filter( new F<Boolean, Boolean>() {
            @Override public Boolean f( final Boolean b ) {
                return b;
            }
        } ).length() == numberSceneNode.pairList.length();
    }


    public void split() {
        Point2D location = fractionNode.getGlobalTranslation();
        location = fractionNodeParent.globalToLocal( location );
        fractionNode.removeFromParent();
        fractionNodeParent.addChild( fractionNode );
        fractionNode.setOffset( location );
    }
}