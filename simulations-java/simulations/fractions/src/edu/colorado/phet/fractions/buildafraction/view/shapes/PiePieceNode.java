// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.shapes;

import fj.data.Option;

import java.awt.Shape;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractions.buildafraction.BuildAFractionModule;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.factories.CircularShapeFunction;
import edu.colorado.phet.fractions.fractionsintro.intro.view.pieset.ShapeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.math.vector.Vector2D.ZERO;
import static edu.colorado.phet.fractions.buildafraction.view.shapes.ContainerShapeNode.circleDiameter;
import static java.lang.Math.PI;

/**
 * PieceNode for drawing a circular pie piece, which rotates as it is dragged.
 *
 * @author Sam Reid
 */
public class PiePieceNode extends PieceNode {

    //Shadow to be shown while dragging (and maybe animating).  Shown with add/remove child so it doesn't disrupt bounds/layout
    private final PhetPPath pieShadow;
    private final PNode pieBackground;

    public PiePieceNode( final int pieceDenominator, final ShapeSceneNode shapeSceneNode, final PhetPPath shape ) {
        super( pieceDenominator, shapeSceneNode, shape );
        pieBackground = new PNode() {{
            addChild( new PhetPPath( ContainerShapeNode.createPieSlice( 1 ), BuildAFractionCanvas.TRANSPARENT ) );
        }};
        addChild( new ZeroOffsetNode( pieBackground ) );
        pieShadow = new PhetPPath( getShadowOffset().createTransformedShape( this.pathNode.getPathReference() ),
                                   ShapeNode.SHADOW_PAINT );
        pieBackground.addChild( this.pathNode );

        installInputListeners();
    }

    //As the object gets bigger, it should move so that it is centered on the mouse.  To compute how to move it, we must rotate it and scale it immediately
    //and invisibly to the user, then set back its settings and animate them.
    @Override void stepTowardMouse( final PInputEvent event ) {
        super.stepTowardMouse( event );

        //We want path node to move toward the mouse center.
        Point2D pt = event.getPositionRelativeTo( this );
        Point2D center = pathNode.getGlobalFullBounds().getCenter2D();
        center = globalToLocal( center );

        Vector2D delta = new Vector2D( center, pt ).times( 0.75 );
        translate( delta.x, delta.y );
    }

    @Override protected void dragStarted() {
        showShadow();
        final Option<Double> nextAngle = context.getNextAngle( this );
        if ( nextAngle.isSome() ) {
            animateToAngle( nextAngle.some() );
        }
    }

    protected void showShadow() {
        hideShadow();
        pieBackground.addChild( 0, pieShadow );
    }

    //If it moved closer to a different target site, update rotation.
    @Override protected void rotateTo( final double angle, final PInputEvent event ) {
        PActivity activity = animateToAngle( angle );
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

    @Override protected void dragEnded() {
        hideShadow();
    }

    protected void hideShadow() {
        while ( pieBackground.getChildrenReference().contains( pieShadow ) ) {
            pieBackground.removeChild( pieShadow );
        }
    }

    AnimateToAngle animateToAngle( final double angle ) {
        final AnimateToAngle activity = new AnimateToAngle( this, BuildAFractionModule.ANIMATION_TIME, reduceWindingNumber( angle ) );
        addActivity( activity );
        return activity;
    }

    //Make it so the piece won't rotate further than necessary
    private double reduceWindingNumber( final double angle ) {
        double originalAngle = pieceRotation;
        double delta = angle - originalAngle;
        while ( delta > PI ) { delta -= PI * 2; }
        while ( delta < -PI ) { delta += PI * 2; }
        return originalAngle + delta;
    }

    public void setPieceRotation( final double angle ) {
        final double extent = PI * 2.0 / pieceSize;
        Shape shape = new CircularShapeFunction( extent, circleDiameter / 2 ).createShape( ZERO, angle );
        pathNode.setPathTo( shape );
        pieShadow.setPathTo( getShadowOffset().createTransformedShape( shape ) );
        this.pieceRotation = angle;
    }

    @Override public void animateToTopOfStack() {
        animateToAngle( 0 );
        super.animateToTopOfStack();
    }
}