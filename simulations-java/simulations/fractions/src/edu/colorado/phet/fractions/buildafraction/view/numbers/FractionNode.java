// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import fj.data.List;
import fj.data.Option;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.model.property.ObservableProperty;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.RichPNode;
import edu.colorado.phet.common.piccolophet.activities.AnimateToScale;
import edu.colorado.phet.common.piccolophet.activities.PActivityDelegateAdapter;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.layout.HBox;
import edu.colorado.phet.common.piccolophet.nodes.layout.VBox;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.DragEvent;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.SimSharingCanvasBoundedDragHandler;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractions.buildafraction.view.DisablePickingWhileAnimating;
import edu.colorado.phet.fractions.buildafraction.view.UndoButton;
import edu.colorado.phet.fractions.buildafraction.view.numbers.Box.ShapeContainer;
import edu.colorado.phet.fractions.buildafraction.view.shapes.CompositeDelegate;
import edu.colorado.phet.fractions.buildafraction.view.shapes.MixedFractionNode;
import edu.colorado.phet.fractions.common.math.Fraction;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components;
import edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.ParameterKeys;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
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

    public final Box whole = new Box();//Whole box is only shown and used for mixed numbers
    public final Box numerator = new Box();
    public final PhetPPath divisorLine;
    public final Box denominator = new Box();
    public final UndoButton undoButton;
    public final boolean mixedNumber;
    private final ArrayList<VoidFunction1<Option<Fraction>>> undoListeners = new ArrayList<VoidFunction1<Option<Fraction>>>();
    private final FractionDraggingContext context;
    private final double SCALE_IN_TOOLBOX = 0.7;
    private double toolboxPositionX;
    private double toolboxPositionY;
    private FractionCardNode cardNode;
    //Keep track of recent activity so it can be incrementally undone with the "undo" button
    private List<FractionNodePosition> dropListHistory = List.nil();

    public FractionNode( final FractionDraggingContext context, boolean mixedNumber ) {
        this.context = context;
        this.mixedNumber = mixedNumber;
        numerator.box = new ShapeContainer( box() );
        denominator.box = new ShapeContainer( box() );
        whole.box = new ShapeContainer( box( MixedFractionNode.mixedNumberWholeScale ) );

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
        dragRegion.addInputEventListener( new SimSharingCanvasBoundedDragHandler( chain( fraction, FractionNode.this.hashCode() ), FractionNode.this, false ) {
            @Override protected ParameterSet getParametersForAllEvents( final PInputEvent event ) {
                final String numString = numerator.numberNode != null ? numerator.numberNode.number + "" : "empty";
                final String denString = denominator.numberNode != null ? denominator.numberNode.number + "" : "empty";
                return super.getParametersForAllEvents( event ).with( ParameterKeys.numerator, numString ).
                        with( ParameterKeys.denominator, denString );
            }

            @Override public void mousePressed( final PInputEvent event ) {
                super.mousePressed( event );

                //Grow as it moves out of the toolbox
                addActivity( new AnimateToScale( 1.0, FractionNode.this, BuildAFractionModule.ANIMATION_TIME ) );

                context.startDrag( FractionNode.this );
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

        //Prevent the user from creating improper fractions when in mixed number mode

        if ( mixedNumber ) {
            ObservableProperty<Option<Integer>> draggedCard = context.getDraggedCardProperty();

            //Add an observer to the card.  Note that this causes a memory leak, but I think the risk of it causing an out of memory exception is very low
            draggedCard.addObserver( new VoidFunction1<Option<Integer>>() {
                public void apply( final Option<Integer> draggedCard ) {
                    numerator.setEnabled( true );
                    denominator.setEnabled( true );

                    final boolean inCollectionBox = context.isInCollectionBox( FractionNode.this );
                    if ( draggedCard.isSome() && !inCollectionBox && !isComplete() ) {
                        Integer card = draggedCard.some();
                        if ( numerator.numberNode != null && card <= numerator.numberNode.number ) {
                            denominator.setEnabled( false );
                        }
                        if ( denominator.numberNode != null && card >= denominator.numberNode.number ) {
                            numerator.setEnabled( false );
                        }
                    }
                }
            } );
        }
    }

    private static PhetPPath box() {
        return box( 1.0 );
    }

    private static PhetPPath box( double scale ) {
        return new PhetPPath( new Rectangle2D.Double( 0, 0, 40 * scale, 50 * scale ), new BasicStroke( 2, BasicStroke.CAP_SQUARE, JOIN_MITER, 1, new float[]{10, 6}, 0 ), Color.red );
    }

    //Send the last piece to the toolbox (First-In-Last-Out)
    void undo() {
        setDragRegionPickable( true );
        Option<Fraction> value = isComplete() ? Option.some( getValue() ) : Option.<Fraction>none();

        Point2D topCardLocation = numerator.cardNode != null ? numerator.cardNode.getGlobalTranslation() : null;
        Point2D bottomCardLocation = denominator.cardNode != null ? denominator.cardNode.getGlobalTranslation() : null;
        Point2D wholeCardLocation = whole.cardNode != null ? whole.cardNode.getGlobalTranslation() : null;

        if ( cardNode != null ) {
            cardNode.undo();
            cardNode = null;
        }

        //Undo whichever happened last
        if ( dropListHistory.length() > 0 ) {
            FractionNodePosition element = dropListHistory.last();
            if ( element == FractionNodePosition.WHOLE ) { undo( whole, wholeCardLocation ); }
            else if ( element == FractionNodePosition.NUMERATOR ) { undo( numerator, topCardLocation ); }
            else if ( element == FractionNodePosition.DENOMINATOR ) { undo( denominator, bottomCardLocation ); }

            //Drop the last item
            dropListHistory = dropListHistory.reverse().drop( 1 ).reverse();
        }
        undoButton.setVisible( numerator.cardNode != null || denominator.cardNode != null || whole.cardNode != null );
        for ( VoidFunction1<Option<Fraction>> undoListener : undoListeners ) {
            undoListener.apply( value );
        }

        context.updateStacks();
    }

    //Undo all the parts, called when "undo" pressed in the collection box
    public void undoAll() {
        while ( undoButton.getVisible() ) {
            undo();
        }
    }

    private void undo( final Box box, final Point2D topCardLocation ) {
        if ( box.cardNode != null ) {

            //Undo the effects caused by attachNumber()
            box.cardNode.numberNode.setScale( 1.0 );
            box.cardNode.numberNode.setBoldFont( true );

            box.cardNode.setCardShapeVisible();
            box.cardNode.setAllPickable( true );
            box.box.shape.setVisible( true );

            box.parent.addChild( box.cardNode );
            box.cardNode.addNumberNodeBackIn( box.numberNode );

            //fix offset
            box.cardNode.setGlobalTranslation( topCardLocation );

            box.cardNode.setVisible( true );
            box.cardNode.setPickable( true );
            box.cardNode.setChildrenPickable( true );

            box.cardNode.animateToTopOfStack( context.isFractionLab() );
            box.cardNode = null;
            box.numberNode = null;

            box.setEnabled( true );
        }
    }

    public void attachNumber( final PhetPPath box, final NumberCardNode numberCardNode ) {
        if ( box == numerator.box.shape ) {
            attachToBox( numberCardNode, numerator, FractionNodePosition.NUMERATOR );
        }
        else if ( box == denominator.box.shape ) {
            attachToBox( numberCardNode, denominator, FractionNodePosition.DENOMINATOR );
        }
        else if ( box == whole.box.shape ) {
            attachToBox( numberCardNode, whole, FractionNodePosition.WHOLE );
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

        if ( box == whole.box.shape ) {
            numberNode.setScale( MixedFractionNode.mixedNumberWholeScale );
            numberNode.translate( -3, -numberNode.getFullHeight() / 8 );
            numberNode.setBoldFont( false );
        }
    }

    private void attachToBox( final NumberCardNode numberCardNode, final Box box, final FractionNodePosition type ) {
        box.cardNode = numberCardNode;
        box.numberNode = numberCardNode.numberNode;

        //Store the parent so it can be re-parented on undo
        box.parent = numberCardNode.getParent();

        dropListHistory = dropListHistory.snoc( type );
    }

    public boolean isComplete() {
        return mixedNumber ?
               numerator.cardNode != null && denominator.cardNode != null && whole.cardNode != null :
               numerator.cardNode != null && denominator.cardNode != null;
    }

    public Fraction getValue() {
        return mixedNumber ?
               getFractionPart().plus( Fraction.fraction( whole.cardNode.number, 1 ) ) :
               getFractionPart();
    }

    private Fraction getFractionPart() {
        return new Fraction( numerator.cardNode.number, denominator.cardNode.number );
    }

    public NumberNode getTopNumberNode() { return numerator.numberNode; }

    public NumberNode getBottomNumberNode() { return denominator.numberNode; }

    public NumberNode getWholeNumberNode() { return whole.numberNode; }

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
        animateToPositionScaleRotation( toolboxPositionX, toolboxPositionY, SCALE_IN_TOOLBOX, 0, 1000 ).setDelegate( new DisablePickingWhileAnimating( this, true ) );
    }

    public void animateToCenterOfScreen() {
        final double x = context.getCenterOfScreen().x;
        final double y = context.getCenterOfScreen().y;
        animateToPositionScaleRotation( x, y, 1.0, 0, BuildAFractionModule.ANIMATION_TIME ).setDelegate( new CompositeDelegate( new DisablePickingWhileAnimating( this, true ),
                                                                                                                                new PActivityDelegateAdapter() {
                                                                                                                                    @Override public void activityStarted( final PActivity activity ) {
                                                                                                                                    }

                                                                                                                                    @Override public void activityFinished( final PActivity activity ) {
                                                                                                                                    }
                                                                                                                                } ) );
    }

    //Don't go to the exact center of screen, or it is likely to overlap another one
    public void animateNearCenterOfScreen() {
        final double x = context.getCenterOfScreen().x + ( Math.random() * 2 - 1 ) * 75;
        final double y = context.getCenterOfScreen().y + ( Math.random() * 2 - 1 ) * 75;
        animateToPositionScaleRotation( x, y, 1.0, 0, BuildAFractionModule.ANIMATION_TIME ).setDelegate( new DisablePickingWhileAnimating( this, true ) );
    }

    //Return true if nothing is in top and nothing is in bottom
    public boolean isEmpty() {
        return mixedNumber ?
               numerator.cardNode == null && denominator.cardNode == null && whole.cardNode == null :
               numerator.cardNode == null && denominator.cardNode == null;
    }

    public void reset() {
        //undo until completely undone
        undoAll();
    }
}