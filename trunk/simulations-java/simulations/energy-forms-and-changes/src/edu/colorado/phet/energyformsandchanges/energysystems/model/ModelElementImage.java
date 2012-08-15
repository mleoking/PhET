// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.energyformsandchanges.energysystems.model;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2D;
import edu.colorado.phet.energyformsandchanges.common.EFACConstants;

/**
 * Class that provides information about an image that is used in the view
 * representation of a model element.
 *
 * @author John Blanco
 */
public class ModelElementImage {

    // Scaling factor that uses the image width to define its size in the model.
    private static final double DEFAULT_SCALING_FACTOR = 1 / EFACConstants.ENERGY_SYSTEMS_MVT_SCALE_FACTOR;

    private final BufferedImage image;
    private final double width; // Width of the image in model units (meters).  Height is derived from aspect ratio of image.

    // Offset in model units (meters) from the center of the position of the
    // model element that owns this image to the center of the image.
    private final Vector2D centerToCenterOffset;

    public ModelElementImage( BufferedImage image, Vector2D centerToCenterOffset ) {
        this( image, image.getWidth() * DEFAULT_SCALING_FACTOR, centerToCenterOffset );
    }

    public ModelElementImage( BufferedImage image, double width, Vector2D centerToCenterOffset ) {
        this.image = image;
        this.width = width;
        this.centerToCenterOffset = centerToCenterOffset;
    }

    public BufferedImage getImage() {
        return image;
    }

    public double getWidth() {
        return width;
    }

    public Vector2D getCenterToCenterOffset() {
        return centerToCenterOffset;
    }
}