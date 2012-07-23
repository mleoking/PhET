// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.pictures;

import fj.F;
import fj.data.Option;

import java.awt.BasicStroke;
import java.awt.Shape;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractions.buildafraction.model.pictures.ShapeType;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractions.buildafraction.view.Stackable;
import edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.factories.CircularShapeFunction;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

/**
 * @author Sam Reid
 */
public class PieceNode extends Stackable {
    public final Integer pieceSize;
    private double initialScale = Double.NaN;
    private PhetPPath pathNode;
    public static final BasicStroke stroke = new BasicStroke( 2 );
    double pieceRotation = 0.0;

    public PieceNode( final Integer pieceSize, final PieceContext context, PhetPPath shape, ShapeType shapeType ) {
        this.pieceSize = pieceSize;
        pathNode = shape;

        if ( shapeType == ShapeType.HORIZONTAL_BAR ) {

            PNode piece = new ZeroOffsetNode( pathNode );
            addChild( piece );
        }
        else {
            final PhetPPath b = new PhetPPath( SimpleContainerNode.createPieSlice( 1 ), BuildAFractionCanvas.TRANSPARENT );

            PNode background = new PNode() {{
                addChild( b );
            }};
            addChild( new ZeroOffsetNode( background ) );
            addChild( pathNode );
            background.addChild( pathNode );
        }

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new SimSharingDragHandler( null, true ) {
            @Override protected void startDrag( final PInputEvent event ) {
                super.startDrag( event );
                PieceNode.this.moveToFront();
                setPositionInStack( Option.<Integer>none() );
                final AnimateToScale activity = new AnimateToScale( PieceNode.this, 200 );
                addActivity( activity );
                animateToAngle( context.getNextAngle( PieceNode.this ) );
            }

            @Override protected void drag( final PInputEvent event ) {
                super.drag( event );
                final PDimension delta = event.getDeltaRelativeTo( event.getPickedNode().getParent() );
                translate( delta.width, delta.height );
            }

            @Override protected void endDrag( final PInputEvent event ) {
                super.endDrag( event );
                context.endDrag( PieceNode.this, event );
            }
        } );
    }

    public void setInitialScale( double s ) {
        this.initialScale = s;
        setScale( s );
    }

    public Fraction toFraction() { return new Fraction( 1, pieceSize );}

    public static final F<PieceNode, Fraction> _toFraction = new F<PieceNode, Fraction>() {
        @Override public Fraction f( final PieceNode r ) {
            return r.toFraction();
        }
    };

    public void moveToTopOfStack() { stack.moveToTopOfStack( this ); }

    protected double getAnimateToScale() { return this.initialScale; }

    public void animateToAngle( final double angle ) {
        addActivity( new AnimateToAngle( this, 200, angle ) );
    }

    public static class AnimateToAngle extends PInterpolatingActivity {
        private final PieceNode node;
        private final double angle;

        public AnimateToAngle( final PieceNode node, long duration, double angle ) {
            super( duration );
            this.node = node;
            this.angle = angle;
        }

        @Override public void setRelativeTargetValue( final float zeroToOne ) {
            node.setPieceRotation( zeroToOne * angle );
        }
    }

    public void setPieceRotation( final double angle ) {
        final double extent = Math.PI * 2.0 / pieceSize;
        Shape shape = new CircularShapeFunction( extent, SimpleContainerNode.circleDiameter / 2 ).createShape( Vector2D.ZERO, angle );
        pathNode.setPathTo( shape );
        this.pieceRotation = angle;
    }

}