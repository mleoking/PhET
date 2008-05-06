/* Copyright 2008, University of Colorado */

package edu.colorado.phet.glaciers.view;

import java.awt.geom.Point2D;

import edu.colorado.phet.glaciers.GlaciersImages;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PAffineTransform;

/**
 * MountainsNode is a background image of the mountains and valley.
 * This image was created for a specific valley profile, as described
 * by the Valley class.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MountainsAndValleyNode extends PImage {

    // 2D elevation change between foreground and background valley width boundaries
    private static final double PERSPECTIVE_HEIGHT = 250; // meters
    
    public MountainsAndValleyNode( ModelViewTransform mvt ) {
        super( GlaciersImages.MOUNTAINS );
        setPickable( false );
        setChildrenPickable( false );
        
        /*
         * ABOUT THIS HACK:
         * The artist was unable to tell me what scale was used to create the image, 
         * or where the origin was relative to the upper-left corner of the image.
         * And there are other scaling factor (eg, ModelViewTransform, camera transforms)
         * that make computation of the proper alignment difficult.  And the image 
         * kept changing.  So after much fiddling, I found that it was easier to 
         * simply align this image via trail-&-error.
         * 
         * HOW TO ALIGN THE IMAGE:
         * The original SVG image file (glaciers/assets/mountains.svg) contains small
         * red rings that mark these positions on the valley floor: F(0), F(10000) and F(70000).
         * Create a PNG file with these rings visible, and put it in data/glaciers/images/mountains.png.
         * In the PlayArea, set DEBUG_BACKGROUND_IMAGE_ALIGNMENT=true to draw blue circles
         * to these same locations. The image is properly aligned when the blue circles
         * fall inside the red rings.
         */
        PAffineTransform transform = getTransformReference( true );
        transform.scale( 70000./79677., 1.42 );
        Point2D offset = mvt.modelToView( -5044, 4400 );
        transform.translate( offset.getX(), offset.getY() );
    }
    
    public static double getPerspectiveHeight() {
        return PERSPECTIVE_HEIGHT;
    }
}
