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
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.DragEvent;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.SimSharingCanvasBoundedDragHandler;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractions.buildafraction.view.DisablePickingWhileAnimating;
import edu.colorado.phet.fractions.buildafraction.view.shapes.AnimateToScale;
import edu.colorado.phet.fractions.buildafraction.view.shapes.MixedFractionNode;
import edu.colorado.phet.fractions.buildafraction.view.shapes.UndoButton;
import edu.colorado.phet.fractions.common.math.Fraction;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.ParameterKeys;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain.chain;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components.fraction;
import static java.awt.BasicStroke.CAP_ROUND;
import static java.awt.BasicStroke.JOIN_MITER;
import static java.awt.Color.black;

/**
 * Node for the fraction that starts empty and gets numbers added to numerator and denominator, and is moved to the scoring cells.
 *
 * @author Sam Reid
 */
public class FractionNode extends RichPNode {

    public final Box numerator = new Box();
    public final Box denominator = new Box();
    public final Box whole = new Box();//Whole box is only shown and used for mixed numbers

    public final PhetPPath divisorLine;
    public final UndoButton undoButton;

    private final ArrayList<VoidFunction1<Option<Fraction>>> undoListeners = new ArrayList<VoidFunction1<Option<Fraction>>>();
    private double toolboxPositionX;
    private double toolboxPositionY;

    private FractionCardNode cardNode;
    private final FractionDraggingContext context;
    public final boolean mixedNumber;

    private final double SCALE_IN_TOOLBOX = 0.7;

    public FractionNode( final FractionDraggingContext context, boolean mixedNumber ) {
        this.context = context;
        this.mixedNumber = mixedNumber;
        numerator.box = box( true );
        denominator.box = box( true );
        whole.box = box( true, MixedFractionNode.mixedNumberWholeScale );

        //Size in the toolbox is smaller to keep the toolbox size good
        setScale( SCALE_IN_TOOLBOX );
        divisorLine = new PhetPPath( new Line2D.Double( 0, 0, 50, 0 ), new BasicStroke( 4, CAP_ROUND, JOIN_MITER ), black );

        undoButton = new UndoButton( chain( Components.playAreaUndoButton, FractionNode.this.hashCode() ) );
        undoButton.addActionListener( new ActionListener() {
            public void actionPerformed( final ActionEvent e ) {
                undo();
            }
        } );

        final VBox fractionPart = new VBox( numerator.box, divisorLine, denominator.box );

        //Only show the whole box for mixed numbers
        final PNode box = mixedNumber ? new HBox( whole.box, fractionPart ) :
                          fractionPart;

        //Show a background behind it to make the entire shape draggable
        final Color transparent = new Color( 0, 0, 0, 0 );
        final RichPNode dragRegion = new RichPNode( new PhetPPath( RectangleUtils.expand( box.getFullBounds(), 5, 5 ), transparent ), box );
        addChild( dragRegion );

        Rectangle2D bounds = divisorLine.getFullBounds();
        bounds = box.localToParent( bounds );
        undoButton.setOffset( bounds.getMinX() - 2 - undoButton.getFullBounds().getWidth(), bounds.getCenterY() - undoButton.getFullBounds().getHeight() / 2 );
        undoButton.addInputEventListener( new CursorHandler() );
        undoButton.setVisible( false );
        addChild( undoButton );

        dragRegion.addInputEventListener( new CursorHandler() );
        dragRegion.addInputEventListener( new SimSharingCanvasBoundedDragHandler( chain( fraction, FractionNode.this.hashCode() ), FractionNode.this ) {
            @Override protected ParameterSet getParametersForAllEvents( final PInputEvent event ) {
                final String numString = numerator.number != null ? numerator.number.number + "" : "empty";
                final String denString = denominator.number != null ? denominator.number.number + "" : "empty";
                return super.getParametersForAllEvents( event ).with( ParameterKeys.numerator, numString ).
                        with( ParameterKeys.denominator, denString );
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

    public void undo() {
        setDragRegionPickable( true );
        Option<Fraction> value = isComplete() ? Option.some( getValue() ) : Option.<Fraction>none();

        Point2D topCardLocation = numerator.card != null ? numerator.card.getGlobalTranslation() : null;
        Point2D bottomCardLocation = denominator.card != null ? denominator.card.getGlobalTranslation() : null;
        Point2D wholeCardLocation = whole.card != null ? whole.card.getGlobalTranslation() : null;

        if ( cardNode != null ) {
            cardNode.undo();
            cardNode = null;
        }

        //TODO simsharing message
        undo( numerator, topCardLocation );
        undo( denominator, bottomCardLocation );
        undo( whole, wholeCardLocation );

        undoButton.setVisible( false );
        for ( VoidFunction1<Option<Fraction>> undoListener : undoListeners ) {
            undoListener.apply( value );
        }

        context.updateStacks();
    }

    private void undo( final Box box, final Point2D topCardLocation ) {
        if ( box.card != null ) {
            box.card.setCardShapeVisible( true );
            box.card.setAllPickable( true );
            box.box.setVisible( true );

            box.parent.addChild( box.card );
            box.card.addNumberNodeBackIn( box.number );

            //fix offset
            box.card.setGlobalTranslation( topCardLocation );

            box.card.setVisible( true );
            box.card.setPickable( true );
            box.card.setChildrenPickable( true );

            box.card.animateToTopOfStack();
            box.card = null;
            box.number = null;
        }
    }

    private static PhetPPath box( boolean showOutline ) {
        return box( showOutline, 1.0 );
    }

    private static PhetPPath box( boolean showOutline, double scale ) {
        return new PhetPPath( new Rectangle2D.Double( 0, 0, 40 * scale, 50 * scale ), new BasicStroke( 2, BasicStroke.CAP_SQUARE, JOIN_MITER, 1, new float[] { 10, 6 }, 0 ), showOutline ? Color.red : BuildAFractionCanvas.TRANSPARENT );
    }

    public void attachNumber( final PhetPPath box, final NumberCardNode numberCardNode ) {
        if ( box == numerator.box ) {
            attachToBox( numberCardNode, numerator );
        }
        else if ( box == denominator.box ) {
            attachToBox( numberCardNode, denominator );
        }
        else if ( box == whole.box ) {
            attachToBox( numberCardNode, whole );
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

    private void attachToBox( final NumberCardNode numberCardNode, final Box box ) {
        box.card = numberCardNode;

        //Store the parent so it can be re-parented on undo
        box.parent = numberCardNode.getParent();
        box.number = numberCardNode.numberNode;
    }

    public boolean isComplete() {
        return mixedNumber ?
               numerator.card != null && denominator.card != null && whole.card != null :
               numerator.card != null && denominator.card != null;
    }

    public Fraction getValue() {
        return mixedNumber ?
               getFractionPart().plus( Fraction.fraction( whole.card.number, 1 ) ) :
               getFractionPart();
    }

    private Fraction getFractionPart() {
        return new Fraction( numerator.card.number, denominator.card.number );
    }

    public NumberNode getTopNumberNode() { return numerator.number; }

    public NumberNode getBottomNumberNode() { return denominator.number; }

    public NumberNode getWholeNumberNode() { return whole.number; }

    //Ignore click events on everything except the "undo" button, which appears over the card
    public void setDragRegionPickable( final boolean b ) {
        for ( Object child : getChildrenReference() ) {
            PNode node = (PNode) child;
            if ( node != undoButton ) {
                node.setPickable( b );
                node.setChildrenPickable( b );
            }
        }
        setChildrenPickable( true );
        setPickable( true );
    }

    public void addUndoListener( final VoidFunction1<Option<Fraction>> listener ) {
        undoListeners.add( listener );
    }

    public boolean isInToolboxPosition() {
        return getXOffset() == toolboxPositionX && getYOffset() == toolboxPositionY;
    }

    public void setToolboxPosition( final double x, final double y ) {
        this.toolboxPositionX = x;
        this.toolboxPositionY = y;
    }

    public void setCardNode( final FractionCardNode fractionCardNode ) {
        this.cardNode = fractionCardNode;
    }

    public void animateToToolbox() {
        animateToPositionScaleRotation( toolboxPositionX, toolboxPositionY, SCALE_IN_TOOLBOX, 0, 1000 );
    }

    public void animateToCenterOfScreen() {
        final double x = context.getCenterOfScreen().x;
        final double y = context.getCenterOfScreen().y;
        animateToPositionScaleRotation( x, y, 1.0, 0, 1000 ).setDelegate( new DisablePickingWhileAnimating( this, true ) );
    }

    //Return true if nothing is in top and nothing is in bottom
    public boolean isEmpty() {
        return mixedNumber ?
               numerator.card == null && denominator.card == null && whole.card == null :
               numerator.card == null && denominator.card == null;
    }
}