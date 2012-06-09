package edu.colorado.phet.fractionsintro.buildafraction.view.numbers;

import fj.data.Option;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractions.FractionsResources.Images;
import edu.colorado.phet.fractionsintro.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PDimension;

import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_MITER;
import static java.awt.Color.black;

/**
 * Node for the fraction that starts empty and gets numbers added to numerator and denominator, and is moved to the scoring cells.
 *
 * @author Sam Reid
 */
public class FractionNode extends RichPNode {

    public final PhetPPath topBox;
    public final PhetPPath bottomBox;
    public final PhetPPath divisorLine;
    public final PImage splitButton;
    private NumberCardNode topCard;
    private NumberCardNode bottomCard;
    private ArrayList<VoidFunction1<Option<Fraction>>> splitListeners = new ArrayList<VoidFunction1<Option<Fraction>>>();
    public double toolboxPositionX;
    public double toolboxPositionY;
    private final RichPNode dragRegion;
    private PNode topCardParent;
    private PNode bottomCardParent;
    private NumberNode topNumberNode;
    private NumberNode bottomNumberNode;
    private FractionCardNode cardNode;

    public FractionNode( final FractionDraggingContext context ) {
        topBox = box( true );
        bottomBox = box( true );
        divisorLine = new PhetPPath( new Line2D.Double( 0, 0, 50, 0 ), new BasicStroke( 4, CAP_ROUND, JOIN_MITER ), black );

        splitButton = new PImage( Images.SPLIT_BLUE );
        final VBox box = new VBox( topBox, divisorLine, bottomBox );

        //Show a background behind it to make the entire shape draggable
        final Color transparentGray = new Color( 200, 200, 200, 200 );
        final Color transparent = new Color( 0, 0, 0, 0 );
        dragRegion = new RichPNode( new PhetPPath( RectangleUtils.expand( box.getFullBounds(), 5, 5 ), transparent ), box );
        addChild( dragRegion );

        Rectangle2D bounds = divisorLine.getFullBounds();
        bounds = box.localToParent( bounds );
        splitButton.setOffset( bounds.getMaxX() + 2, bounds.getCenterY() - splitButton.getFullBounds().getHeight() / 2 );
        splitButton.addInputEventListener( new CursorHandler() );
        splitButton.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( final PInputEvent event ) {
                Option<Fraction> value = isComplete() ? Option.some( getValue() ) : Option.<Fraction>none();

                Point2D topCardLocation = topCard != null ? topCard.getGlobalTranslation() : null;
                Point2D bottomCardLocation = bottomCard != null ? bottomCard.getGlobalTranslation() : null;

                if ( cardNode != null ) {
                    cardNode.split();
                    cardNode = null;
                }
                //TODO simsharing message
                if ( topCard != null ) {
                    topCard.setCardShapeVisible( true );
                    topCard.setAllPickable( true );
                    topBox.setVisible( true );

                    topCardParent.addChild( topCard );
                    topCard.addNumberNodeBackIn( topNumberNode );

                    //fix offset
                    topCard.setGlobalTranslation( topCardLocation );

                    topCard.setVisible( true );
                    topCard.setPickable( true );
                    topCard.setChildrenPickable( true );

                    topCard.animateHome();
                    topCard = null;
                }
                if ( bottomCard != null ) {
                    bottomCard.setCardShapeVisible( true );
                    bottomCard.setAllPickable( true );
                    bottomBox.setVisible( true );

                    bottomCardParent.addChild( bottomCard );
                    bottomCard.addNumberNodeBackIn( bottomNumberNode );

                    //fix offset
                    bottomCard.setGlobalTranslation( bottomCardLocation );

                    bottomCard.setVisible( true );
                    bottomCard.setPickable( true );
                    bottomCard.setChildrenPickable( true );

                    bottomCard.animateHome();
                    bottomCard = null;
                }
                splitButton.setVisible( false );
                for ( VoidFunction1<Option<Fraction>> splitListener : splitListeners ) {
                    splitListener.apply( value );
                }
            }
        } );
        splitButton.setVisible( false );
        addChild( splitButton );

        dragRegion.addInputEventListener( new CursorHandler() );
        dragRegion.addInputEventListener( new SimSharingDragHandler( null, true ) {
            @Override protected void drag( final PInputEvent event ) {
                super.drag( event );
                final PDimension delta = event.getDeltaRelativeTo( event.getPickedNode().getParent() );
                translate( delta.getWidth(), delta.getHeight() );
            }

            @Override protected void endDrag( final PInputEvent event ) {
                super.endDrag( event );
                context.endDrag( FractionNode.this, event );
            }
        } );
    }

    private static PhetPPath box( boolean showOutline ) {
        return new PhetPPath( new Rectangle2D.Double( 0, 0, 40, 50 ), new BasicStroke( 2, BasicStroke.CAP_SQUARE, JOIN_MITER, 1, new float[] { 10, 6 }, 0 ), showOutline ? Color.red : BuildAFractionCanvas.TRANSPARENT );
    }

    public void attachNumber( final PhetPPath box, final NumberCardNode numberCardNode ) {
        if ( box == topBox ) {
            topCard = numberCardNode;

            //Store the parent so it can be reparented on split
            topCardParent = numberCardNode.getParent();
            topNumberNode = numberCardNode.numberNode;
        }
        else if ( box == bottomBox ) {
            bottomCard = numberCardNode;

            bottomCardParent = numberCardNode.getParent();
            bottomNumberNode = numberCardNode.numberNode;
        }
        else {
            throw new RuntimeException( "No such box!" );
        }

        //Move number node to our coordinate frame so it will translate, scale, animate and render with this node

        //We need a method that changes coordinate frames for a node
        final NumberNode numberNode = numberCardNode.numberNode;
        Point2D location = numberNode.getGlobalTranslation();

        addChild( numberNode );

        Point2D cardLocation = numberCardNode.getGlobalTranslation();

        addChild( numberCardNode );
        numberCardNode.setGlobalTranslation( cardLocation );

        numberCardNode.setVisible( false );
        numberCardNode.setPickable( false );
        numberCardNode.setChildrenPickable( false );

        numberNode.setGlobalTranslation( location );

        numberNode.setPickable( false );
        numberNode.setChildrenPickable( false );
    }

    public boolean isComplete() { return topCard != null && bottomCard != null; }

    public Fraction getValue() { return new Fraction( topCard.number, bottomCard.number ); }

    public NumberNode getTopNumberNode() {
        return topNumberNode;
    }

    public NumberNode getBottomNumberNode() {
        return bottomNumberNode;
    }

    //Ignore click events on everything except the "split" button, which appears over the card
    public void setDragRegionPickable( final boolean b ) {
        for ( Object child : getChildrenReference() ) {
            PNode node = (PNode) child;
            if ( node != splitButton ) {
                node.setPickable( b );
                node.setChildrenPickable( b );
            }
        }
    }

    public void addSplitListener( final VoidFunction1<Option<Fraction>> listener ) { splitListeners.add( listener ); }

    public boolean isInToolboxPosition() { return getXOffset() == toolboxPositionX && getYOffset() == toolboxPositionY; }

    public void setToolboxPosition( final double x, final double y ) {
        this.toolboxPositionX = x;
        this.toolboxPositionY = y;
    }

    public double getToolboxPositionX() { return toolboxPositionX; }

    public double getToolboxPositionY() { return toolboxPositionY; }

    public void setCardNode( final FractionCardNode fractionCardNode ) { this.cardNode = fractionCardNode; }
}