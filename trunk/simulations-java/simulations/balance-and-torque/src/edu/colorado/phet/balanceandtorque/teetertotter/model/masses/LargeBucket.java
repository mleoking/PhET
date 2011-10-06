// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;


/**
 * Model class that represents a medium size bucket.
 *
 * @author John Blanco
 */
public class LargeBucket extends ImageMass {

    private static final double MASS = 40; // in kg
    private static final double HEIGHT = 0.5; // In meters.

    public LargeBucket( boolean isMystery ) {
        super( MASS, Images.METAL_BUCKET, HEIGHT, new Point2D.Double( 0, 0 ), isMystery );
    }
}
