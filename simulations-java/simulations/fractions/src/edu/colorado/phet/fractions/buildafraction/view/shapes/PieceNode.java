// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import fj.F;
import fj.data.Option;

import java.awt.BasicStroke;
import java.awt.geom.AffineTransform;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.CanvasBoundedDragHandler;
import edu.colorado.phet.common.piccolophet.nodes.toolbox.DragEvent;
import edu.colorado.phet.fractions.buildafraction.view.Stackable;
import edu.colorado.phet.fractions.fractionsintro.intro.model.Fraction;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;
import edu.umd.cs.piccolo.activities.PTransformActivity;
import edu.umd.cs.piccolo.event.PInputEvent;

import static java.awt.geom.AffineTransform.getTranslateInstance;

/**
 * Node for a single piece that can be dragged from the toolbox and dropped in a shape container.
 *
 * @author Sam Reid
 */
public abstract class PieceNode extends Stackable {
    public final Integer pieceSize;
    private double initialScale = Double.NaN;
    final PieceContext context;
    final PhetPPath pathNode;
    public static final BasicStroke stroke = new BasicStroke( 2 );
    double pieceRotation = 0.0;

    PieceNode( final Integer pieceSize, final PieceContext context, PhetPPath pathNode ) {
        this.pieceSize = pieceSize;
        this.context = context;
        this.pathNode = pathNode;
    }

    void installInputListeners() {
        addInputEventListener( new CursorHandler() );

        addInputEventListener( new CanvasBoundedDragHandler( PieceNode.this ) {
            @Override public void mousePressed( final PInputEvent event ) {
                super.mousePressed( event );

                dragStarted();
                PieceNode.this.moveToFront();
                setPositionInStack( Option.<Integer>none() );
                final AnimateToScale activity = new AnimateToScale( PieceNode.this, 200 );
                addActivity( activity );

                activity.setDelegate( new PActivityDelegate() {
                    public void activityStarted( final PActivity activity ) {
                    }

                    public void activityStepped( final PActivity activity ) {
                        stepTowardMouse( event );
                    }

                    public void activityFinished( final PActivity activity ) {
                    }
                } );

            }

            @Override protected void dragNode( final DragEvent event ) {
                Option<Double> originalAngle = context.getNextAngle( PieceNode.this );
                translate( event.delta.width, event.delta.height );
                Option<Double> newAngle = context.getNextAngle( PieceNode.this );
                if ( originalAngle.isSome() && newAngle.isSome() && !originalAngle.some().equals( newAngle.some() ) ) {
                    rotateTo( newAngle.some(), event.event );
                }
            }

            @Override public void mouseReleased( final PInputEvent event ) {
                super.mouseReleased( event );
                context.endDrag( PieceNode.this );

                //Protect against multiple copies accidentally being added
                dragEnded();
            }
        } );
    }

    void rotateTo( final double angle, final PInputEvent event ) {}

    void stepTowardMouse( final PInputEvent event ) { }

    void dragEnded() { }

    void dragStarted() { }

    protected double getAnimateToScale() { return this.initialScale; }

    AffineTransform getShadowOffset() {return getTranslateInstance( 6, 6 );}

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
        stack.moveToTopOfStack( this );
    }

    //Show drop shadow when moving back to toolbox
    public PTransformActivity animateTo( Vector2D v ) {
        PTransformActivity a = super.animateTo( v );
        a.setDelegate( new PActivityDelegate() {
            public void activityStarted( final PActivity activity ) {
                showShadow();
            }

            public void activityStepped( final PActivity activity ) {
            }

            public void activityFinished( final PActivity activity ) {
                hideShadow();
            }
        } );
        return a;
    }

    protected abstract void hideShadow();

    protected abstract void showShadow();
}