package edu.colorado.phet.buildafraction.view.pictures;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class RectangularPiece extends PNode {
    private double initialX = Double.NaN;
    private double initialY = Double.NaN;
    private final Integer pieceSize;
    private double initialScale = Double.NaN;

    public RectangularPiece( final Integer pieceSize, final PieceContext context ) {
        this.pieceSize = pieceSize;
        PNode piece = new ZeroOffsetNode( new PhetPPath( SimpleContainerNode.createRect( pieceSize ), new Color( 255, 0, 0, 213 ), new BasicStroke( 1 ), Color.black ) );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new SimSharingDragHandler( null, true ) {
            @Override protected void startDrag( final PInputEvent event ) {
                super.startDrag( event );
                final LinearFunction function = new LinearFunction( 0, 1, getScale(), 1.00 );
                PInterpolatingActivity activity = new PInterpolatingActivity( 200 ) {

                    @Override public void setRelativeTargetValue( final float zeroToOne ) {
                        setScale( function.evaluate( zeroToOne ) );
                    }
                };
                addActivity( activity );
            }

            @Override protected void drag( final PInputEvent event ) {
                super.drag( event );
                final PDimension delta = event.getDeltaRelativeTo( event.getPickedNode().getParent() );
                translate( delta.width, delta.height );
            }

            @Override protected void endDrag( final PInputEvent event ) {
                super.endDrag( event );
                context.endDrag( RectangularPiece.this, event );
            }
        } );
        addChild( piece );
    }

    public boolean isAtInitialPosition() {
        if ( Double.isNaN( initialX ) || Double.isNaN( initialY ) ) {
            throw new RuntimeException( "Position not initialized" );
        }
        else {
            return getXOffset() == initialX && getYOffset() == initialY;
        }
    }

    public void setInitialState( final double x, final double y, double scale ) {
        this.initialX = x;
        this.initialY = y;
        this.initialScale = scale;
        setOffset( x, y );
        setScale( scale );
    }

    public Fraction toFraction() {
        return new Fraction( 1, pieceSize );
    }

    public void animateHome() { animateToPositionScaleRotation( initialX, initialY, initialScale, 0, 200 ); }
}