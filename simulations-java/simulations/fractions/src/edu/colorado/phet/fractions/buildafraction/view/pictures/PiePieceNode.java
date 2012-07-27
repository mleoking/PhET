// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.buildafraction.view.pictures;

import java.awt.Shape;

import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.common.piccolophet.nodes.kit.ZeroOffsetNode;
import edu.colorado.phet.fractions.buildafraction.model.pictures.ShapeType;
import edu.colorado.phet.fractions.buildafraction.view.BuildAFractionCanvas;
import edu.colorado.phet.fractions.fractionsintro.intro.model.pieset.factories.CircularShapeFunction;
import edu.colorado.phet.fractions.fractionsintro.intro.view.pieset.ShapeNode;
import edu.umd.cs.piccolo.PNode;

import static edu.colorado.phet.common.phetcommon.math.vector.Vector2D.ZERO;
import static edu.colorado.phet.fractions.buildafraction.view.pictures.SimpleContainerNode.circleDiameter;

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

    @Override protected void dragStarted() {
        pieBackground.addChild( 0, pieShadow );
        animateToAngle( context.getNextAngle( this ) );
    }

    @Override protected void dragEnded() {
        while ( pieBackground.getChildrenReference().contains( pieShadow ) ) {
            pieBackground.removeChild( pieShadow );
        }
    }

    public void animateToAngle( final double angle ) {
        addActivity( new AnimateToAngle( this, 200, angle ) );
    }

    public void setPieceRotation( final double angle ) {
        final double extent = Math.PI * 2.0 / pieceSize;
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