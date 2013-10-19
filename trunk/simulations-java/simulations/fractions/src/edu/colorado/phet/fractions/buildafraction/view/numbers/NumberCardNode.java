// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import fj.data.Option;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.phetcommon.view.util.RectangleUtils;
import edu.colorado.phet.common.piccolophet.activities.PActivityDelegateAdapter;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.DragEvent;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.SimSharingCanvasBoundedDragHandler;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractions.buildafraction.view.DisablePickingWhileAnimating;
import edu.colorado.phet.fractions.buildafraction.view.Stackable;
import edu.colorado.phet.fractions.buildafraction.view.UpdateAnimatingFlag;
import edu.colorado.phet.fractions.buildafraction.view.shapes.CompositeDelegate;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.value;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain.chain;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components.numberCard;

/**
 * Card that shows a single number (could be multiple digits).
 *
 * @author Sam Reid
 */
public class NumberCardNode extends Stackable {
    public final int number;
    public final NumberNode numberNode;
    private final PhetPPath cardShape;
    private final Dimension2DDouble size;
    private final NumberDragContext context;

    public NumberCardNode( final Dimension2DDouble size, final Integer number, final NumberDragContext context ) {
        this.number = number;
        this.size = size;
        this.context = context;
        cardShape = new PhetPPath( new RoundRectangle2D.Double( 0, 0, size.width, size.height, 10, 10 ), Color.white, new BasicStroke( 1 ), Color.black ) {

            //Get rid of "trail" of black lines on Mac.  I could not reproduce the problem but KH could
            @Override public Rectangle2D getPathBoundsWithStroke() {
                return RectangleUtils.expand( super.getPathBoundsWithStroke(), 1, 1 );
            }
        };
        addChild( cardShape );
        numberNode = new
                NumberNode( number ) {{
                    centerFullBoundsOnPoint( size.width / 2, size.height / 2 );
                }};
        addChild( numberNode );

        addInputEventListener( new SimSharingCanvasBoundedDragHandler( chain( numberCard, NumberCardNode.this.hashCode() ), NumberCardNode.this, false ) {
            @Override protected ParameterSet getParametersForAllEvents( final PInputEvent event ) {
                return super.getParametersForAllEvents( event ).with( value, number );
            }

            @Override protected void startDrag( final PInputEvent event ) {
                super.startDrag( event );
                context.startDrag( NumberCardNode.this );
            }

            @Override protected void dragNode( final DragEvent event ) {
                moveToFront();
                setPositionInStack( Option.<Integer>none() );
                translate( event.delta.width, event.delta.height );
            }

            @Override public void mouseReleased( final PInputEvent event ) {
                super.mouseReleased( event );
                context.endDrag( NumberCardNode.this );
            }
        } );

        addInputEventListener( new CursorHandler() );
    }

    //Animates the card moving back to its location in its stack of cards.
    public void animateToStackLocation( Vector2D v, final boolean deleteOnArrival ) {
        animateToPositionScaleRotation( v.x, v.y, 1, 0, BuildAFractionModule.ANIMATION_TIME ).setDelegate( new CompositeDelegate(
                new DisablePickingWhileAnimating( this, true ),
                new UpdateAnimatingFlag( animating ),
                new PActivityDelegateAdapter() {
                    @Override public void activityFinished( final PActivity activity ) {
                        if ( deleteOnArrival ) {
                            delete();
                        }
                    }
                } ) );
    }

    //From Stackable interface: determine what scale the card should be animated to.
    @Override protected double getAnimateToScale() { return 1.0; }

    //For "Fractions Lab", delete a card once it has moved back to its stack
    protected void delete() {
        super.delete();
        removeFromParent();
    }

    //Show or hide the outline of the card shape.
    public void setCardShapeVisible() { cardShape.setVisible( true ); }

    //Adds the NumberNode to this card.
    public void addNumberNodeBackIn( final NumberNode numberNode ) {
        addChild( numberNode );
        numberNode.setOffset( cardShape.getCenterX() - numberNode.getFullWidth() / 2, cardShape.getCenterY() - numberNode.getFullHeight() / 2 );
    }

    @SuppressWarnings("unchecked") public void animateToTopOfStack( boolean deleteOnArrival ) { stack.animateToTopOfStack( this, deleteOnArrival ); } //unchecked warning

    //Make a copy of the NumberCardNode, used in the Fractions Lab where there is an infinite supply of cards.
    @SuppressWarnings("unchecked") public NumberCardNode copy() {
        final NumberCardNode node = new NumberCardNode( size, number, context );
        node.setStack( stack ); //unchecked warning
        node.setPositionInStack( getPositionInStack() ); //unchecked warning
        stack.cards = stack.cards.snoc( node ); //unchecked warning
        return node;
    }
}