// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.numbers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.RoundRectangle2D;

import edu.colorado.phet.common.phetcommon.view.Dimension2DDouble;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class NumberCardNode extends PNode {
    private double initialX;
    private double initialY;
    public final int number;
    public final PhetPPath cardShape;
    public final NumberNode numberNode;

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

    public void setInitialPosition( final double x, final double y ) {
        this.initialX = x;
        this.initialY = y;
        setOffset( x, y );
    }

    public double getInitialX() { return initialX; }

    public double getInitialY() { return initialY; }

    public void animateHome() { animateToPositionScaleRotation( getInitialX(), getInitialY(), 1, 0, 1000 ); }

    public void setCardShapeVisible( boolean visible ) { cardShape.setVisible( visible ); }

    public void addNumberNodeBackIn( final NumberNode numberNode ) {
        addChild( numberNode );
        numberNode.setOffset( cardShape.getCenterX() - numberNode.getFullWidth() / 2, cardShape.getCenterY() - numberNode.getFullHeight() / 2 );
    }
}