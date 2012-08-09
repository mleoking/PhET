// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import fj.data.Option;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.CanvasBoundedDragHandler;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.DragEvent;
import edu.colorado.phet.fractions.buildafraction.view.Stackable;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager.sendUserMessage;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterKeys.value;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet.parameterSet;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.*;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain.chain;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes.sprite;
import static edu.colorado.phet.fractions.fractionsintro.FractionsIntroSimSharing.Components.numberCard;

/**
 * Card that shows a single number (could be multiple digits).
 *
 * @author Sam Reid
 */
public class NumberCardNode extends Stackable {
    public final int number;
    private final PhetPPath cardShape;
    public final NumberNode numberNode;

    public NumberCardNode( final Dimension2DDouble size, final Integer number, final NumberDragContext context ) {
        this.number = number;
        cardShape = new PhetPPath( new RoundRectangle2D.Double( 0, 0, size.width, size.height, 10, 10 ), Color.white, new BasicStroke( 1 ), Color.black );
        addChild( cardShape );
        numberNode = new NumberNode( number ) {{
            centerFullBoundsOnPoint( size.width / 2, size.height / 2 );
        }};
        addChild( numberNode );

        addInputEventListener( new CanvasBoundedDragHandler( NumberCardNode.this ) {
            @Override public void mousePressed( final PInputEvent event ) {
                sendUserMessage( chain( numberCard, NumberCardNode.this.hashCode() ), sprite, pressed, parameterSet( value, number ) );
                super.mousePressed( event );
            }

            @Override protected void dragNode( final DragEvent event ) {
                sendUserMessage( chain( numberCard, NumberCardNode.this.hashCode() ), sprite, drag, parameterSet( value, number ) );
                moveToFront();
                setPositionInStack( Option.<Integer>none() );
                translate( event.delta.width, event.delta.height );
            }

            @Override public void mouseReleased( final PInputEvent event ) {
                sendUserMessage( chain( numberCard, NumberCardNode.this.hashCode() ), sprite, released, parameterSet( value, number ) );
                super.mouseReleased( event );
                context.endDrag( NumberCardNode.this );
            }
        } );

        addInputEventListener( new CursorHandler() );
    }

    public PTransformActivity animateTo( Vector2D v ) {
        return animateToPositionScaleRotation( v.x, v.y, 1, 0, 400 );
    }

    public void setCardShapeVisible( boolean visible ) { cardShape.setVisible( visible ); }

    public void addNumberNodeBackIn( final NumberNode numberNode ) {
        addChild( numberNode );
        numberNode.setOffset( cardShape.getCenterX() - numberNode.getFullWidth() / 2, cardShape.getCenterY() - numberNode.getFullHeight() / 2 );
    }

    public void moveToTopOfStack() { stack.moveToTopOfStack( this ); }
}