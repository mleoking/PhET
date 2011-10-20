// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;


/**
 * Model class that represents a bottle of soda.
 *
 * @author John Blanco
 */
public class SodaBottle extends ImageMass {

    private static final double MASS = 2; // in kg
    private static final double HEIGHT = 0.5; // In meters.

    public SodaBottle( boolean isMystery ) {
        super( MASS, Images.SODA_BOTTLE, HEIGHT, new Point2D.Double( 0, 0 ), isMystery );
    }
}
