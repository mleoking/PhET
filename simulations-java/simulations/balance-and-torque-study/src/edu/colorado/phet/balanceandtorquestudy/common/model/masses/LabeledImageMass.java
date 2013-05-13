// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.common.model.masses;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;

/**
 * This is an extension of the ImageMass class that adds a textual label.  This
 * was created in support of a request to label the mystery masses with
 * translatable labels.
 *
 * @author John Blanco
 */
public class LabeledImageMass extends ImageMass {
    private final String labelText;

    public LabeledImageMass( IUserComponent userComponent, Point2D initialPosition, LabeledImageMassConfig config ) {
        this( userComponent, initialPosition, config.mass, config.image, config.height, config.labelText, config.isMystery );
    }

    /**
     * Constructor.
     */
    public LabeledImageMass( IUserComponent userComponent, Point2D initialPosition, double mass, BufferedImage image, double height, String labelText, boolean isMystery ) {
        super( userComponent, mass, image, height, initialPosition, isMystery );
        this.labelText = labelText;
    }

    public String getLabelText() {
        return labelText;
    }

    // Collection of information needed to define a particular configuration
    // of a labeled image mass.
    static class LabeledImageMassConfig {
        public final double mass;            // In kg
        public final BufferedImage image;    // Image to use when depicting this object.
        public final double height;          // In model space, which is in meters
        public final String labelText;
        public final boolean isMystery;

        LabeledImageMassConfig( double mass, BufferedImage image, double height, String labelText, boolean isMystery ) {
            this.mass = mass;
            this.image = image;
            this.height = height;
            this.labelText = labelText;
            this.isMystery = isMystery;
        }
    }
}
