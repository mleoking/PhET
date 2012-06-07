package edu.colorado.phet.fractionsintro.buildafraction.view.pictures;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Rectangle2D;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * Some copied from NumberNode, may need to be remerged.
 *
 * @author Sam Reid
 */
public class ContainerNode extends PNode {
    private double initialX;
    private double initialY;
    private int number;

    public ContainerNode( int number, final ContainerContext context ) {
        addChild( new PhetPPath( new Rectangle2D.Double( 0, 0, 100, 50 ), Color.white, new BasicStroke( 2 ), Color.black ) );
        this.number = number;
        addInputEventListener( new SimSharingDragHandler( null, true ) {
            @Override protected void drag( final PInputEvent event ) {
                super.drag( event );
                final PDimension delta = event.getDeltaRelativeTo( getParent() );
                translate( delta.width, delta.height );
            }

            @Override protected void endDrag( final PInputEvent event ) {
                super.endDrag( event );
                context.endDrag( ContainerNode.this, event );
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

}