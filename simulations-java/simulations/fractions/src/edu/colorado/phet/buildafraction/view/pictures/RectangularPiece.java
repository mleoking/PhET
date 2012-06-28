// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.buildafraction.view.pictures;

import fj.F;

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
    private double initialScale = Double.NaN;
    private final PhetPPath pathNode;
    private final BasicStroke stroke = new BasicStroke( 2 );

    public RectangularPiece( final Integer pieceSize, final PieceContext context ) {
        this.pieceSize = pieceSize;
        pathNode = new PhetPPath( SimpleContainerNode.createRect( pieceSize ), Color.red, stroke, Color.black );
        PNode piece = new ZeroOffsetNode( pathNode );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new SimSharingDragHandler( null, true ) {
            @Override protected void startDrag( final PInputEvent event ) {
                super.startDrag( event );
                RectangularPiece.this.moveToFront();
                addActivity( new AnimateToScale( RectangularPiece.this, 1.0, 200 ) );
//                pathNode.setStroke( null );
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

    public Fraction toFraction() { return new Fraction( 1, pieceSize );}

    public static final F<RectangularPiece, Fraction> _toFraction = new F<RectangularPiece, Fraction>() {
        @Override public Fraction f( final RectangularPiece r ) {
            return r.toFraction();
        }
    };

    public void animateHome() {
        animateToPositionScaleRotation( initialX, initialY, initialScale, 0, 200 );
        pathNode.setStroke( stroke );
    }
}