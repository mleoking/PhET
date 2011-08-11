// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.balanceandtorque.teetertotter.model.masses.ImageMass;

/**
 * This is an extension of the ImageMass class that adds a textual label.  This
 * was created in support of a request to label the mystery objects with
 * translatable labels.
 *
 * @author John Blanco
 */
public class LabeledImageMass extends ImageMass {
    private final String labelText;

    /**
     * Constructor.
     */
    public LabeledImageMass( double mass, BufferedImage image, double height, Point2D initialPosition, String labelText ) {
        super( mass, image, height, initialPosition );
        this.labelText = new String( labelText );
    }

    public String getLabelText() {
        return labelText;
    }
}
