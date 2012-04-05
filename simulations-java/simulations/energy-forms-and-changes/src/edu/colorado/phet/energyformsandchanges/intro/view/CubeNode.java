// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.event.CursorHandler;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.intro.model.Cube;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that represents a cube in the view.  Examples of cubes are
 * bricks, lead blocks, and so forth.
 *
 * @author John Blanco
 */
public class CubeNode extends PNode {
    public CubeNode( Cube cube, final ModelViewTransform mvt ) {

        // Extract the scale transform from the MVT so that we can separate the
        // shape from the position of the cube.
        AffineTransform scaleTransform = AffineTransform.getScaleInstance( mvt.getTransform().getScaleX(), mvt.getTransform().getScaleY() );

        // Add the representation of the cube.
        addChild( new PhetPPath( scaleTransform.createTransformedShape( Cube.getRawShape() ), cube.getColor(), new BasicStroke( 2 ), Color.LIGHT_GRAY ) );

        // Update the offset if and when the model position changes.
        cube.position.addObserver( new VoidFunction1<Point2D>() {
            public void apply( Point2D position ) {
                setOffset( mvt.modelToView( position ) );
            }
        } );

        // Add the cursor handler.
        addInputEventListener( new CursorHandler( CursorHandler.HAND ) );

        // Add the drag handler.
        addInputEventListener( new MovableElementDragHandler( cube, this, mvt ) );
    }
}
