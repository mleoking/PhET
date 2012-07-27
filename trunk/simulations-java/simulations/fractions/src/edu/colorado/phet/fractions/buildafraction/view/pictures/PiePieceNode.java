// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.pictures;

import fj.data.Option;

import java.awt.Shape;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractions.buildafraction.model.pictures.ShapeType;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.factories.CircularShapeFunction;
import edu.colorado.phet.fractions.fractionsintro.intro.view.pieset.ShapeNode;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.activities.PActivity;
import edu.umd.cs.piccolo.activities.PActivity.PActivityDelegate;
import edu.umd.cs.piccolo.event.PInputEvent;

import static edu.colorado.phet.common.phetcommon.math.vector.Vector2D.ZERO;
import static edu.colorado.phet.fractions.buildafraction.view.pictures.SimpleContainerNode.circleDiameter;
import static java.lang.Math.PI;

/**
 * @author Sam Reid
 */
public class PiePieceNode extends PieceNode {
    private PhetPPath pieShadow;
    private PNode pieBackground;

    public PiePieceNode( final int pieceDenominator, final PictureSceneNode pictureSceneNode, final PhetPPath shape ) {
        super( pieceDenominator, pictureSceneNode, shape, ShapeType.PIE );
        pieBackground = new PNode() {{
            addChild( new PhetPPath( SimpleContainerNode.createPieSlice( 1 ), BuildAFractionCanvas.TRANSPARENT ) );
        }};
        addChild( new ZeroOffsetNode( pieBackground ) );
        pieShadow = new PhetPPath( getShadowOffset().createTransformedShape( this.pathNode.getPathReference() ),
                                   ShapeNode.SHADOW_PAINT );
        pieBackground.addChild( this.pathNode );

        installInputListeners();
    }

    //As the object gets bigger, it should move so that it is centered on the mouse.  To compute how to move it, we must rotate it and scale it immediately
    //and invisibly to the user, then set back its settings and animate them.
    @Override protected void stepTowardMouse( final PInputEvent event ) {
        super.stepTowardMouse( event );

        //We want pathnode to move toward the mouse center.
        Point2D pt = event.getPositionRelativeTo( this );
        Point2D center = pathNode.getGlobalFullBounds().getCenter2D();
        center = globalToLocal( center );

        Vector2D delta = new Vector2D( center, pt ).times( 0.75 );
        translate( delta.x, delta.y );
    }

    @Override protected void dragStarted() {
        pieBackground.addChild( 0, pieShadow );
        final Option<Double> nextAngle = context.getNextAngle( this );
        if ( nextAngle.isSome() ) {
            animateToAngle( nextAngle.some() );
        }
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
        while ( pieBackground.getChildrenReference().contains( pieShadow ) ) {
            pieBackground.removeChild( pieShadow );
        }
    }

    public AnimateToAngle animateToAngle( final double angle ) {
        final AnimateToAngle activity = new AnimateToAngle( this, 200, reduceWindingNumber( angle ) );
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

    @Override public void moveToTopOfStack() {
        animateToAngle( 0 );
        super.moveToTopOfStack();
    }
}