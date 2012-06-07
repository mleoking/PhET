package edu.colorado.phet.fractionsintro.buildafraction.view.pictures;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class RectangularPiece extends PNode {
    private final double initialX;
    private final double initialY;

    public RectangularPiece( final Integer pieceSize, final double centerX, final double centerY ) {
        PNode piece = new PhetPPath( ContainerNode.createRect( pieceSize ), Color.red, new BasicStroke( 1 ), Color.black );
        piece.centerFullBoundsOnPoint( centerX, centerY );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new SimSharingDragHandler( null, true ) {
            @Override protected void drag( final PInputEvent event ) {
                final PDimension delta = event.getDeltaRelativeTo( event.getPickedNode().getParent() );
                translate( delta.width, delta.height );
            }
        } );
        addChild( piece );
        this.initialX = getXOffset();
        this.initialY = getYOffset();
    }

    public boolean isAtInitialPosition() { return getXOffset() == initialX && getYOffset() == initialY; }
}