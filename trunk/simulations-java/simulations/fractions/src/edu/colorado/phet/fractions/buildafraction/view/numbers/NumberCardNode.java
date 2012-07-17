// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import fj.data.Option;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractions.common.util.immutable.Vector2D;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class NumberCardNode extends PNode implements Stackable {
    public final int number;
    public final PhetPPath cardShape;
    public final NumberNode numberNode;
    private Stack stack;
    private Option<Integer> positionInStack = Option.none();

    public NumberCardNode( final Dimension2DDouble size, final Integer number, final NumberDragContext context ) {
        this.number = number;
        cardShape = new PhetPPath( new RoundRectangle2D.Double( 0, 0, size.width, size.height, 10, 10 ), Color.white, new BasicStroke( 1 ), Color.black );
        addChild( cardShape );
        numberNode = new NumberNode( number ) {{
            centerFullBoundsOnPoint( size.width / 2, size.height / 2 );
        }};
        addChild( numberNode );

        addInputEventListener( new SimSharingDragHandler( null, true ) {
            @Override protected void drag( final PInputEvent event ) {
                super.drag( event );
                moveToFront();
                positionInStack = Option.none();
                final PDimension delta = event.getDeltaRelativeTo( getParent() );
                translate( delta.width, delta.height );
            }

            @Override protected void endDrag( final PInputEvent event ) {
                super.endDrag( event );
                context.endDrag( NumberCardNode.this, event );
            }
        } );
        addInputEventListener( new CursorHandler() );
    }

    public void setAllPickable( final boolean b ) {
        setPickable( b );
        setChildrenPickable( b );
    }

    public void animateTo( Vector2D v ) { animateToPositionScaleRotation( v.x, v.y, 1, 0, 1000 ); }

    public void setCardShapeVisible( boolean visible ) { cardShape.setVisible( visible ); }

    public void addNumberNodeBackIn( final NumberNode numberNode ) {
        addChild( numberNode );
        numberNode.setOffset( cardShape.getCenterX() - numberNode.getFullWidth() / 2, cardShape.getCenterY() - numberNode.getFullHeight() / 2 );
    }

    public void setStack( final Stack stack ) {
        assert this.stack == null;
        addPropertyChangeListener( PNode.PROPERTY_TRANSFORM, new PropertyChangeListener() {
            public void propertyChange( final PropertyChangeEvent evt ) {
                stack.cardMoved();
            }
        } );
        this.stack = stack;
    }

    public void setPositionInStack( final Option<Integer> some ) { this.positionInStack = some; }

    public void moveToTopOfStack() { stack.moveToTopOfStack( this ); }

    public Option<Integer> getPositionInStack() {
        return positionInStack;
    }

    public boolean isAtStackIndex( final Integer site ) {
        return positionInStack.isSome() && positionInStack.some().equals( site );
    }
}