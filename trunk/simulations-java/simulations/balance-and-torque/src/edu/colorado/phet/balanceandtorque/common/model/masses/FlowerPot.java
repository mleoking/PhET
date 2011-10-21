// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;


/**
 * Model class that represents a flower pot.
 *
 * @author John Blanco
 */
public class FlowerPot extends ImageMass {

    private static final double MASS = 5; // in kg
    private static final double HEIGHT = 0.55; // In meters.

    public FlowerPot( boolean isMystery ) {
        super( MASS, Images.FLOWER_POT, HEIGHT, new Point2D.Double( 0, 0 ), isMystery );
    }
}
