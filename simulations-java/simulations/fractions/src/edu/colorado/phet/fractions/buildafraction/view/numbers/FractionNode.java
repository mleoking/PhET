// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import fj.data.Option;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.DragEvent;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.SimSharingCanvasBoundedDragHandler;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractions.buildafraction.view.shapes.AnimateToScale;
import edu.colorado.phet.fractions.buildafraction.view.shapes.UndoButton;
import edu.colorado.phet.fractions.common.math.Fraction;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain.chain;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components.fraction;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.ParameterKeys.denominator;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.ParameterKeys.numerator;
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
    public final UndoButton splitButton;
    private NumberCardNode topCard;
    private NumberCardNode bottomCard;
    private final ArrayList<VoidFunction1<Option<Fraction>>> splitListeners = new ArrayList<VoidFunction1<Option<Fraction>>>();
    private double toolboxPositionX;
    private double toolboxPositionY;
    private PNode topCardParent;
    private PNode bottomCardParent;
    private NumberNode topNumberNode;
    private NumberNode bottomNumberNode;
    private FractionCardNode cardNode;
    private final FractionDraggingContext context;

    private final double SCALE_IN_TOOLBOX = 0.7;

    public FractionNode( final FractionDraggingContext context ) {
        this.context = context;
        topBox = box( true );
        bottomBox = box( true );

        //Size in the toolbox is smaller to keep the toolbox size good
        setScale( SCALE_IN_TOOLBOX );
        divisorLine = new PhetPPath( new Line2D.Double( 0, 0, 50, 0 ), new BasicStroke( 4, CAP_ROUND, JOIN_MITER ), black );

        splitButton = new UndoButton( chain( Components.numberSplitButton, FractionNode.this.hashCode() ) );
        splitButton.addActionListener( new ActionListener() {
            @Override public void actionPerformed( final ActionEvent e ) {
                split();
            }
        } );

        final VBox box = new VBox( topBox, divisorLine, bottomBox );

        //Show a background behind it to make the entire shape draggable
        final Color transparent = new Color( 0, 0, 0, 0 );
        final RichPNode dragRegion = new RichPNode( new PhetPPath( RectangleUtils.expand( box.getFullBounds(), 5, 5 ), transparent ), box );
        addChild( dragRegion );

        Rectangle2D bounds = divisorLine.getFullBounds();
        bounds = box.localToParent( bounds );
        splitButton.setOffset( bounds.getMinX() - 2 - splitButton.getFullBounds().getWidth(), bounds.getCenterY() - splitButton.getFullBounds().getHeight() / 2 );
        splitButton.addInputEventListener( new CursorHandler() );
        splitButton.setVisible( false );
        addChild( splitButton );

        dragRegion.addInputEventListener( new CursorHandler() );
        dragRegion.addInputEventListener( new SimSharingCanvasBoundedDragHandler( chain( fraction, FractionNode.this.hashCode() ), FractionNode.this ) {
            @Override protected ParameterSet getParametersForAllEvents( final PInputEvent event ) {
                return super.getParametersForAllEvents( event ).with( numerator, topNumberNode != null ? topNumberNode.number + "" : "empty" ).
                        with( denominator, bottomNumberNode != null ? bottomNumberNode.number + "" : "empty" );
            }

            @Override public void mousePressed( final PInputEvent event ) {
                super.mousePressed( event );

                //Grow as it moves out of the toolbox
                addActivity( new AnimateToScale( FractionNode.this, BuildAFractionModule.ANIMATION_TIME ) );
            }

            @Override protected void dragNode( final DragEvent event ) {
                moveToFront();
                translate( event.delta.width, event.delta.height );
            }

            @Override public void mouseReleased( final PInputEvent event ) {
                super.mouseReleased( event );
                context.endDrag( FractionNode.this );
            }
        } );
    }

    public void split() {
        setDragRegionPickable( true );
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

            topCard.animateToTopOfStack();
            topCard = null;
            topNumberNode = null;
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

            bottomCard.animateToTopOfStack();
            bottomCard = null;
            bottomNumberNode = null;
        }
        splitButton.setVisible( false );
        for ( VoidFunction1<Option<Fraction>> splitListener : splitListeners ) {
            splitListener.apply( value );
        }

        context.updateStacks();
    }

    private static PhetPPath box( boolean showOutline ) {
        return new PhetPPath( new Rectangle2D.Double( 0, 0, 40, 50 ), new BasicStroke( 2, BasicStroke.CAP_SQUARE, JOIN_MITER, 1, new float[] { 10, 6 }, 0 ), showOutline ? Color.red : BuildAFractionCanvas.TRANSPARENT );
    }

    public void attachNumber( final PhetPPath box, final NumberCardNode numberCardNode ) {
        if ( box == topBox ) {
            topCard = numberCardNode;

            //Store the parent so it can be re-parented on split
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

    public NumberNode getTopNumberNode() { return topNumberNode; }

    public NumberNode getBottomNumberNode() { return bottomNumberNode; }

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

    public void setCardNode( final FractionCardNode fractionCardNode ) { this.cardNode = fractionCardNode; }

    public void sendFractionSkeletonToToolbox() {
        animateToPositionScaleRotation( toolboxPositionX, toolboxPositionY, SCALE_IN_TOOLBOX, 0, 1000 );
    }

    public void sendFractionSkeletonToCenterOfScreen() {
        double x = 428;
        double y = 300;
        animateToPositionScaleRotation( x - getFullBounds().getWidth() / 2, y, 1.0, 0, 1000 );
    }

    //Return true if nothing is in top and nothing is in bottom
    public boolean isEmpty() { return topCard == null && bottomCard == null; }
}