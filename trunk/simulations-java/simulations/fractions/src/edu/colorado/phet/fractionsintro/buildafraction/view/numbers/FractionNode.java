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
    private NumberCardNode topTarget;
    private NumberCardNode bottomTarget;
    private ArrayList<VoidFunction1<Option<Fraction>>> splitListeners = new ArrayList<VoidFunction1<Option<Fraction>>>();
    public double toolboxPositionX;
    public double toolboxPositionY;
    private final RichPNode dragRegion;
    private PNode topTargetParent;
    private PNode bottomTargetParent;
    private NumberNode topTargetNumberNode;
    private NumberNode bottomTargetNumberNode;
    private FractionCardNode cardNode;

    public FractionNode( final FractionDraggingContext context ) {
        topBox = box( true );
        bottomBox = box( true );
        divisorLine = new PhetPPath( new Line2D.Double( 0, 0, 50, 0 ), new BasicStroke( 4, CAP_ROUND, JOIN_MITER ), black );

        splitButton = new PImage( Images.SPLIT_BLUE );
        final VBox box = new VBox( topBox, divisorLine, bottomBox );

        //Show a background behind it to make the entire shape draggable
        dragRegion = new RichPNode( new PhetPPath( RectangleUtils.expand( box.getFullBounds(), 5, 5 ), new Color( 200, 200, 200, 200 ) ), box );
        addChild( dragRegion );

        Rectangle2D bounds = divisorLine.getFullBounds();
        bounds = box.localToParent( bounds );
        splitButton.setOffset( bounds.getMaxX() + 2, bounds.getCenterY() - splitButton.getFullBounds().getHeight() / 2 );
        splitButton.addInputEventListener( new CursorHandler() );
        splitButton.addInputEventListener( new PBasicInputEventHandler() {
            @Override public void mousePressed( final PInputEvent event ) {
                Option<Fraction> value = isComplete() ? Option.some( getValue() ) : Option.<Fraction>none();

                if ( cardNode != null ) {
                    cardNode.split();
                    cardNode = null;
                }
                //TODO simsharing message
                if ( topTarget != null ) {
                    topTarget.animateHome();
                    topTarget.setCardShapeVisible( true );
                    topTarget.setAllPickable( true );
                    topBox.setVisible( true );

                    topTargetParent.addChild( topTarget );
                    topTarget.addNumberNodeBackIn( topTargetNumberNode );

                    //fix offset
                    Point2D location = topTargetNumberNode.getGlobalTranslation();
                    location = topTargetParent.globalToLocal( location );
                    topTarget.setOffset( location );

                    topTarget.animateHome();
                    topTarget = null;
                }
                if ( bottomTarget != null ) {
                    bottomTarget.animateHome();
                    bottomTarget.setCardShapeVisible( true );
                    bottomTarget.setAllPickable( true );
                    bottomBox.setVisible( true );

                    bottomTargetParent.addChild( bottomTarget );
                    bottomTarget.addNumberNodeBackIn( bottomTargetNumberNode );

                    //fix offset
                    Point2D location = bottomTargetNumberNode.getGlobalTranslation();
                    location = bottomTargetParent.globalToLocal( location );
                    bottomTarget.setOffset( location );

                    bottomTarget.animateHome();
                    bottomTarget = null;
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
                translateAll( event.getDeltaRelativeTo( event.getPickedNode().getParent() ) );
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
            topTarget = numberCardNode;

            //Store the parent so it can be reparented on split
            topTargetParent = numberCardNode.getParent();
            topTargetNumberNode = numberCardNode.numberNode;
        }
        else if ( box == bottomBox ) {
            bottomTarget = numberCardNode;

            bottomTargetParent = numberCardNode.getParent();
            bottomTargetNumberNode = numberCardNode.numberNode;
        }
        else {
            throw new RuntimeException( "No such box!" );
        }

        //Move number node to our coordinate frame so it will translate, scale, animate and render with this node
        final NumberNode numberNode = numberCardNode.numberNode;
        Point2D location = numberNode.getGlobalTranslation();
        location = globalToLocal( location );

        numberNode.removeFromParent();
        addChild( numberNode );

        numberNode.setOffset( location );
        numberCardNode.setCardShapeVisible( false );

        numberNode.setPickable( false );
        numberNode.setChildrenPickable( false );
    }

    public boolean isComplete() { return topTarget != null && bottomTarget != null; }

    public Fraction getValue() { return new Fraction( topTarget.number, bottomTarget.number ); }

    public PNode getTopNumber() {return topTarget;}

    public PNode getBottomNumber() {return bottomTarget;}

    public void animateAllToPosition( double x, double y, long time ) {
        animateToPositionScaleRotation( x, y, 1.0, 0, time );
        if ( topTarget != null ) {
            double dx = topTarget.getXOffset() - getXOffset();
            double dy = topTarget.getYOffset() - getYOffset();
            topTarget.animateToPositionScaleRotation( x + dx, y + dy, 1.0, 0, time );
        }
        if ( bottomTarget != null ) {
            double dx = bottomTarget.getXOffset() - getXOffset();
            double dy = bottomTarget.getYOffset() - getYOffset();
            bottomTarget.animateToPositionScaleRotation( x + dx, y + dy, 1.0, 0, time );
        }
    }

    public void translateAll( final PDimension delta ) {
        translate( delta.getWidth(), delta.getHeight() );
        if ( topTarget != null ) { topTarget.translate( delta.getWidth(), delta.getHeight() ); }
        if ( bottomTarget != null ) { bottomTarget.translate( delta.getWidth(), delta.getHeight() ); }
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