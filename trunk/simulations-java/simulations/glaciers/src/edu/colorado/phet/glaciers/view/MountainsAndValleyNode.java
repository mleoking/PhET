/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.GlaciersConstants;
import edu.colorado.phet.glaciers.GlaciersImages;
import edu.colorado.phet.glaciers.model.Valley;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PAffineTransform;

/**
 * MountainsNode is a background image of the mountains and valley.
 * This image was created for a specific valley profile, as described
 * by the Valley class.
 * <p>
 * HOW TO ALIGN THE IMAGE:
 * The original SVG image file (glaciers/assets/mountains.svg) contains small
 * red rings that mark these positions on the valley floor: F(0), F(10000) and F(70000).
 * Create a PNG file with these rings visible, and put it in data/glaciers/images/mountains.png.
 * Open the PNG file in Photoshop (or other similar program), note the exact locations
 * of the F(0) and F(70000) markers in the file, and set constants F_O and F_70000 to those values.
 * <p>
 * TO VERIFY ALIGNMENT:
 * In the PlayArea, set DEBUG_BACKGROUND_IMAGE_ALIGNMENT=true to draw blue circles
 * to these same locations. The image is properly aligned when the blue circles
 * fall inside the red rings.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MountainsAndValleyNode extends PImage {

    // These are absolute x,y coordinates of the markers in the image file
    private static final Point2D F_0 = new Point2D.Double( 313, 142 );   // marker at x=0, y=F(0)
    private static final Point2D F_70000 = new Point2D.Double( 5253, 343 ); // marker at x=70000, y=F(70000)
    
    public MountainsAndValleyNode( Valley valley, ModelViewTransform mvt ) {
        super( GlaciersImages.MOUNTAINS );
        setPickable( false );
        setChildrenPickable( false );
        
        // x scale
        final double viewDistanceX = mvt.modelToView( 70000, 0 ).getX();
        final double imageDistanceX = ( F_70000.getX() - F_0.getX() );
        final double scaleX = viewDistanceX / imageDistanceX;
        
        // y scale
        final double viewDistanceY = mvt.modelToView( 0, valley.getElevation( 70000 ) - valley.getElevation( 0 ) ).getY();
        final double imageDistanceY = ( F_70000.getY() - F_0.getY() );
        final double scaleY = ( viewDistanceY / imageDistanceY ) / GlaciersConstants.Y_AXIS_SCALE_IN_IMAGE;
        
        // x & y offset
        final double offsetX = -F_0.getX();
        final double offsetY = ( mvt.modelToView( 0, valley.getElevation( 0 ) ).getY() / scaleY ) - F_0.getY();

        PAffineTransform transform = getTransformReference( true );
        transform.scale( scaleX, scaleY );
        transform.translate( offsetX, offsetY );
    }
}
