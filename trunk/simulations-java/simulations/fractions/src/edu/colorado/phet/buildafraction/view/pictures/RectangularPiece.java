package edu.colorado.phet.buildafraction.view.pictures;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class RectangularPiece extends PNode {
    private double initialX = Double.NaN;
    private double initialY = Double.NaN;
    private final Integer pieceSize;

    public RectangularPiece( final Integer pieceSize, final PieceContext context ) {
        this.pieceSize = pieceSize;
        PNode piece = new ZeroOffsetNode( new PhetPPath( ContainerNode.createRect( pieceSize ), Color.red, new BasicStroke( 1 ), Color.black ) );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new SimSharingDragHandler( null, true ) {
            @Override protected void drag( final PInputEvent event ) {
                final PDimension delta = event.getDeltaRelativeTo( event.getPickedNode().getParent() );
                translate( delta.width, delta.height );
            }

            @Override protected void endDrag( final PInputEvent event ) {
                context.endDrag( RectangularPiece.this, event );
            }
        } );
        addChild( piece );
        this.initialX = getXOffset();
        this.initialY = getYOffset();
    }

    public boolean isAtInitialPosition() {
        if ( Double.isNaN( initialX ) || Double.isNaN( initialY ) ) {
            throw new RuntimeException( "Position not initialized" );
        }
        else {
            return getXOffset() == initialX && getYOffset() == initialY;
        }
    }

    public void setInitialPosition( final double x, final double y ) {
        this.initialX = x;
        this.initialY = y;
        setOffset( x, y );
    }

    public Fraction toFraction() {
        return new Fraction( 1, pieceSize );
    }

    public void animateHome() { animateToPositionScaleRotation( initialX, initialY, 1, 0, 200 ); }
}