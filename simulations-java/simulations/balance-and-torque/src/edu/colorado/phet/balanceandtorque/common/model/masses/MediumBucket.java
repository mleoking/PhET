// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;


/**
 * Model class that represents a medium size bucket.
 *
 * @author John Blanco
 */
public class MediumBucket extends ImageMass {

    private static final double MASS = 20; // in kg
    private static final double HEIGHT = 0.4; // In meters.

    public MediumBucket( boolean isMystery ) {
        super( MASS, Images.YELLOW_BUCKET, HEIGHT, new Point2D.Double( 0, 0 ), isMystery );
    }
}
