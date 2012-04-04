// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.intro.model.Shelf;
import edu.umd.cs.piccolo.PNode;

/**
 * Representation of a shelf in the view.
 *
 * @author John Blanco
 */
public class ShelfNode extends PNode {

    // Flag for controlling whether the 2D surface of the shelf, which is
    // where objects would rest upon it, should be visible.  This is used
    // primarily for debug.
    private static final boolean SHOW_2D_LOCATION = true;

    // Flag for controlling whether the 3D wire frame representation of the
    // shelf should be visible.  This is used primarily for debug.
    private static final boolean SHOW_WIRE_FRAME = true;

    public ShelfNode( final Shelf shelf, final ModelViewTransform mvt ) {
        if ( SHOW_2D_LOCATION ) {
            // Draw a line that represents the horizontal line where objects
            // can be placed.
            DoubleGeneralPath surface2DPath = new DoubleGeneralPath( mvt.modelToView( shelf.getPosition() ) );
            surface2DPath.lineTo( mvt.modelToViewX( shelf.getPosition().getX() ) + mvt.modelToViewDeltaX( shelf.getWidth() ),
                                  mvt.modelToViewY( shelf.getPosition().getY() ) );
            addChild( new PhetPPath( surface2DPath.getGeneralPath(), new BasicStroke( 3 ), Color.RED ) );
        }
        if ( SHOW_WIRE_FRAME ) {
            // Draw top surface.
            DoubleGeneralPath surfacePath = new DoubleGeneralPath();
            double edgeHeight = shelf.getForeshortenedHeight();
            double edgeWidth = shelf.getForeshortenedHeight() * Math.sin( shelf.getPerspectiveAngle() );
            ImmutableVector2D centerLeftEdge = new ImmutableVector2D( shelf.getPosition() );
            ImmutableVector2D lowerLeftCorner = centerLeftEdge.getAddedInstance( -edgeWidth / 2, -edgeHeight / 2 );
            surfacePath.moveTo( mvt.modelToView( lowerLeftCorner ) );
            ImmutableVector2D upperLeftCorner = lowerLeftCorner.getAddedInstance( edgeWidth, edgeHeight );
            surfacePath.lineTo( mvt.modelToView( upperLeftCorner ) );
            ImmutableVector2D upperRightCorner = upperLeftCorner.getAddedInstance( shelf.getWidth(), 0 );
            surfacePath.lineTo( mvt.modelToView( upperRightCorner ) );
            ImmutableVector2D lowerRightCorner = upperRightCorner.getAddedInstance( -edgeWidth, -edgeHeight );
            surfacePath.lineTo( mvt.modelToView( lowerRightCorner ) );
            surfacePath.lineTo( mvt.modelToView( lowerLeftCorner ) );

            addChild( new PhetPPath( surfacePath.getGeneralPath(), new BasicStroke( 1 ), Color.BLUE ) );
        }

    }
}
