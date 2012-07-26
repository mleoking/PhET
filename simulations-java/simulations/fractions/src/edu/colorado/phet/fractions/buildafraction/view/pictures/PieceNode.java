// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.pictures;

import fj.F;
import fj.data.Option;

import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.math.Function.LinearFunction;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.common.piccolophet.simsharing.SimSharingDragHandler;
import edu.colorado.phet.fractions.buildafraction.model.pictures.ShapeType;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractions.buildafraction.view.Stackable;
import edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.factories.CircularShapeFunction;
import edu.colorado.phet.fractions.fractionsintro.intro.view.pieset.ShapeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PInterpolatingActivity;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PDimension;

import static edu.colorado.phet.common.phetcommon.math.vector.Vector2D.ZERO;
import static edu.colorado.phet.fractions.buildafraction.view.pictures.SimpleContainerNode.circleDiameter;
import static java.awt.geom.AffineTransform.getTranslateInstance;

/**
 * @author Sam Reid
 */
public class PieceNode extends Stackable {
    public final Integer pieceSize;
    private double initialScale = Double.NaN;
    private PhetPPath pathNode;
    private final ShapeType shapeType;
    public static final BasicStroke stroke = new BasicStroke( 2 );
    double pieceRotation = 0.0;
    private PhetPPath pieShadow;
    private PNode pieBackground;

    public PieceNode( final Integer pieceSize, final PieceContext context, PhetPPath shape, final ShapeType shapeType ) {
        this.pieceSize = pieceSize;
        pathNode = shape;
        this.shapeType = shapeType;

//        shadowNode = new PhetPPath( pathNode.getPathReference(), ShapeNode.SHADOW_PAINT );
//        final ZeroOffsetNode shadow = new ZeroOffsetNode( shadowNode );
//        shadow.translate( 6, 6 );
//        shadow.setVisible( false );

        if ( shapeType == ShapeType.HORIZONTAL_BAR ) {

//            addChild( shadow );

            PNode piece = new ZeroOffsetNode( pathNode );
            addChild( piece );
        }
        else {
            pieBackground = new PNode() {{
                addChild( new PhetPPath( SimpleContainerNode.createPieSlice( 1 ), BuildAFractionCanvas.TRANSPARENT ) );
            }};
            addChild( new ZeroOffsetNode( pieBackground ) );
            pieShadow = new PhetPPath( getShadowOffset().createTransformedShape( pathNode.getPathReference() ),
                                       ShapeNode.SHADOW_PAINT );
            pieBackground.addChild( pathNode );
        }

        addInputEventListener( new CursorHandler() );
        addInputEventListener( new SimSharingDragHandler( null, true ) {
            @Override protected void startDrag( final PInputEvent event ) {
                super.startDrag( event );

                if ( shapeType == ShapeType.PIE ) {
                    pieBackground.addChild( 0, pieShadow );
                }
                PieceNode.this.moveToFront();
                setPositionInStack( Option.<Integer>none() );
                final AnimateToScale activity = new AnimateToScale( PieceNode.this, 200 );
                addActivity( activity );
                if ( shapeType == ShapeType.PIE ) {
                    animateToAngle( context.getNextAngle( PieceNode.this ) );
                }
            }

            @Override protected void drag( final PInputEvent event ) {
                super.drag( event );
                final PDimension delta = event.getDeltaRelativeTo( event.getPickedNode().getParent() );
                translate( delta.width, delta.height );
            }

            @Override protected void endDrag( final PInputEvent event ) {
                super.endDrag( event );
                context.endDrag( PieceNode.this, event );

                //Protect against multiple copies accidentally being added
                if ( shapeType == ShapeType.PIE ) {
                    while ( pieBackground.getChildrenReference().contains( pieShadow ) ) {
                        pieBackground.removeChild( pieShadow );
                    }
                }
            }
        } );
    }

    private AffineTransform getShadowOffset() {return getTranslateInstance( 6, 6 );}

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

    public void moveToTopOfStack() {
        if ( shapeType == ShapeType.PIE ) {
            animateToAngle( 0 );
        }
        stack.moveToTopOfStack( this );
    }

    protected double getAnimateToScale() { return this.initialScale; }

    public void animateToAngle( final double angle ) {
        addActivity( new AnimateToAngle( this, 200, angle ) );
    }

    public static class AnimateToAngle extends PInterpolatingActivity {
        private final PieceNode node;
        private final double finalAngle;
        private double initialAngle;

        public AnimateToAngle( final PieceNode node, long duration, double finalAngle ) {
            super( duration );
            this.node = node;
            this.finalAngle = finalAngle;
            this.initialAngle = node.pieceRotation;
        }

        @Override public void setRelativeTargetValue( final float zeroToOne ) {
            LinearFunction linearFunction = new LinearFunction( 0, 1, initialAngle, finalAngle );
            node.setPieceRotation( linearFunction.evaluate( zeroToOne ) );
        }
    }

    public void setPieceRotation( final double angle ) {
        final double extent = Math.PI * 2.0 / pieceSize;
        Shape shape = new CircularShapeFunction( extent, circleDiameter / 2 ).createShape( ZERO, angle );
        pathNode.setPathTo( shape );
        pieShadow.setPathTo( getShadowOffset().createTransformedShape( shape ) );
        this.pieceRotation = angle;
    }
}