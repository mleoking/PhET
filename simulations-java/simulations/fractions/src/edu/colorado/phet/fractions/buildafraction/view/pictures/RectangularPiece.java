// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.pictures;

import fj.F;
import fj.data.Option;

import java.awt.BasicStroke;

import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractions.buildafraction.view.Stackable;
import edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class RectangularPiece extends Stackable {
    private final Integer pieceSize;
    private double initialScale = Double.NaN;
    private final PNode pathNode;
    public static final BasicStroke stroke = new BasicStroke( 2 );

    public RectangularPiece( final Integer pieceSize, final PieceContext context, PNode shape ) {
        this.pieceSize = pieceSize;
        pathNode = shape;
        PNode piece = new ZeroOffsetNode( pathNode );
        addInputEventListener( new CursorHandler() );
        addInputEventListener( new SimSharingDragHandler( null, true ) {
            @Override protected void startDrag( final PInputEvent event ) {
                super.startDrag( event );
                RectangularPiece.this.moveToFront();
                setPositionInStack( Option.<Integer>none() );
                addActivity( new AnimateToScale( RectangularPiece.this, 200 ) );
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

    public void setInitialScale( double s ) {
        this.initialScale = s;
        setScale( s );
    }

    public Fraction toFraction() { return new Fraction( 1, pieceSize );}

    public static final F<RectangularPiece, Fraction> _toFraction = new F<RectangularPiece, Fraction>() {
        @Override public Fraction f( final RectangularPiece r ) {
            return r.toFraction();
        }
    };

    public void moveToTopOfStack() { stack.moveToTopOfStack( this ); }

    protected double getAnimateToScale() { return this.initialScale; }
}