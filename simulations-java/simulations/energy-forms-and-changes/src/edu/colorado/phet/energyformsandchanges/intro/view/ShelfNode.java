// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.intro.view;

import java.awt.BasicStroke;
import java.awt.Color;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.view.graphics.transforms.ModelViewTransform;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetcommon.view.util.DoubleGeneralPath;
import edu.colorado.phet.common.piccolophet.nodes.PhetPPath;
import edu.colorado.phet.energyformsandchanges.intro.model.Shelf;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * Representation of a shelf in the view.
 *
 * @author John Blanco
 */
public class ShelfNode extends PNode {

    // Flag for controlling whether the 2D surface of the shelf, which is
    // where objects would rest upon it, should be visible.  This is used
    // primarily for debug.
    private static final boolean SHOW_2D_LOCATION = false;

    // Flag for controlling whether the 3D wire frame representation of the
    // shelf should be visible.  This is used primarily for debug.
    private static final boolean SHOW_WIRE_FRAME = false;

    public ShelfNode( final Shelf shelf, final ModelViewTransform mvt ) {

        // Set up some variable that will be used in several calculations.
        double edgeHeight = shelf.getForeshortenedHeight();
        double edgeWidth = shelf.getForeshortenedHeight() * Math.sin( shelf.getPerspectiveAngle() );
        ImmutableVector2D surfaceCenterLeftEdge = new ImmutableVector2D( shelf.getPosition() );
        ImmutableVector2D surfaceLowerLeftCorner = surfaceCenterLeftEdge.getAddedInstance( -edgeWidth / 2, -edgeHeight / 2 );
        ImmutableVector2D surfaceUpperLeftCorner = surfaceLowerLeftCorner.getAddedInstance( edgeWidth, edgeHeight );
        ImmutableVector2D surfaceUpperRightCorner = surfaceUpperLeftCorner.getAddedInstance( shelf.getWidth(), 0 );
        ImmutableVector2D surfaceLowerRightCorner = surfaceUpperRightCorner.getAddedInstance( -edgeWidth, -edgeHeight );

        // Define the shape of the faux 3D surface.
        DoubleGeneralPath surfacePath = new DoubleGeneralPath();
        surfacePath.moveTo( mvt.modelToView( surfaceLowerLeftCorner ) );
        surfacePath.lineTo( mvt.modelToView( surfaceUpperLeftCorner ) );
        surfacePath.lineTo( mvt.modelToView( surfaceUpperRightCorner ) );
        surfacePath.lineTo( mvt.modelToView( surfaceLowerRightCorner ) );
        surfacePath.lineTo( mvt.modelToView( surfaceLowerLeftCorner ) );

        double desiredImageWidth = surfacePath.getGeneralPath().getBounds2D().getWidth();
        double desiredImageHeight = surfacePath.getGeneralPath().getBounds2D().getHeight() + mvt.modelToViewDeltaY( -shelf.getThickness() );
        double imageScaleFactorX = desiredImageWidth / shelf.getImage().getWidth();
        double imageScaleFactorY = desiredImageHeight / shelf.getImage().getHeight();
        System.out.println( "imageScaleFactorX = " + imageScaleFactorX );
        System.out.println( "imageScaleFactorY = " + imageScaleFactorY );

        // Warn if the scale factors appear to be way off.
        if ( imageScaleFactorX < 0.5 || imageScaleFactorX > 2 ) {
            System.out.println( getClass().getName() + " - Warning: Image does not suit shelf size well, X scale factor = " + imageScaleFactorX );
        }
        if ( imageScaleFactorY < 0.5 || imageScaleFactorY > 2 ) {
            System.out.println( getClass().getName() + " - Warning: Image does not suit shelf size well, Y scale factor = " + imageScaleFactorY );
        }

        // Scale the image and create the image node.
        PImage shelfImageNode = new PImage( BufferedImageUtils.rescaleFractional( shelf.getImage(), imageScaleFactorX, imageScaleFactorY ) );

        // Position and add the image.
        shelfImageNode.setOffset( mvt.modelToView( shelf.getPosition().getX() - edgeWidth / 2, shelf.getPosition().getY() + shelf.getForeshortenedHeight() / 2 ) );
        addChild( shelfImageNode );

        if ( SHOW_2D_LOCATION ) {
            // Draw a line that represents the horizontal line where objects
            // can be placed.
            DoubleGeneralPath surface2DPath = new DoubleGeneralPath( mvt.modelToView( shelf.getPosition() ) );
            surface2DPath.lineTo( mvt.modelToViewX( shelf.getPosition().getX() ) + mvt.modelToViewDeltaX( shelf.getWidth() ),
                                  mvt.modelToViewY( shelf.getPosition().getY() ) );
            addChild( new PhetPPath( surface2DPath.getGeneralPath(), new BasicStroke( 3 ), Color.RED ) );
        }
        if ( SHOW_WIRE_FRAME ) {

            // Add an outline of the surface.
            addChild( new PhetPPath( surfacePath.getGeneralPath(), new BasicStroke( 1 ), Color.BLUE ) );

            // Draw front edge.
            DoubleGeneralPath frontEdgePath = new DoubleGeneralPath();
            frontEdgePath.moveTo( mvt.modelToView( surfaceLowerLeftCorner ) );
            ImmutableVector2D frontEdgeLowerLeftCorner = surfaceLowerLeftCorner.getAddedInstance( 0, -shelf.getThickness() );
            frontEdgePath.lineTo( mvt.modelToView( frontEdgeLowerLeftCorner ) );
            ImmutableVector2D frontEdgeLowerRightCorner = frontEdgeLowerLeftCorner.getAddedInstance( shelf.getWidth(), 0 );
            frontEdgePath.lineTo( mvt.modelToView( frontEdgeLowerRightCorner ) );
            frontEdgePath.lineTo( mvt.modelToView( surfaceLowerRightCorner ) );

            addChild( new PhetPPath( frontEdgePath.getGeneralPath(), new BasicStroke( 1 ), Color.BLUE ) );

            // Doesn't draw the side edge, add it here if needed.
        }
    }
}
