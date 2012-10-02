// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;


/**
 * Model class that represents a fire hydrant.
 *
 * @author John Blanco
 */
public class FireHydrant extends ImageMass {

    private static final double MASS = 60; // in kg
    private static final double HEIGHT = 0.75; // In meters.

    public FireHydrant( boolean isMystery ) {
        super( MASS, Images.FIRE_HYDRANT, HEIGHT, new Point2D.Double( 0, 0 ), isMystery );
    }
}