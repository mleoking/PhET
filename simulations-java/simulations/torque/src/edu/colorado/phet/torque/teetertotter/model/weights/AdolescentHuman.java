// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.torque.teetertotter.model.weights;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.torque.TeeterTotterTorqueApplication;

/**
 * Model class that represents a human who is roughly 10 years old.
 *
 * @author John Blanco
 */
public class AdolescentHuman extends ImageWeight {

    private static final double MASS = 20; // in kg
    private static final double STANDING_HEIGHT = 1.2; // In meters.
    private static final double SITTING_HEIGHT = 0.6; // In meters.

    private static final BufferedImage STANDING_IMAGE = TeeterTotterTorqueApplication.RESOURCES.getImage( "person standing 01.png" );
    private static final BufferedImage SITTING_IMAGE = TeeterTotterTorqueApplication.RESOURCES.getImage( "person sitting 01.png" );

    public AdolescentHuman() {
        super( MASS, STANDING_IMAGE, STANDING_HEIGHT, new Point2D.Double( 0, 0 ) );
    }
}
