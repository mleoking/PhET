// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.intro.model.Cube;
import edu.umd.cs.piccolo.PNode;

/**
 * Piccolo node that represents a cube in the view.  Examples of cubes are
 * brick, lead blocks, and so forth.
 *
 * @author John Blanco
 */
public class CubeNode extends PNode {
    public CubeNode( Cube cube, final ModelViewTransform mvt ) {
        addChild( new PhetPPath( mvt.modelToView( Cube.getRawShape() ), cube.getColor(), new BasicStroke( 2 ), Color.LIGHT_GRAY ) );
        cube.position.addObserver( new VoidFunction1<Point2D>() {
            public void apply( Point2D position ) {
                setOffset( mvt.modelToView( position ) );
            }
        } );
    }
}
