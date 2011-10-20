// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;


/**
 * Model class that represents a rock.
 *
 * @author John Blanco
 */
public class TinyRock extends ImageMass {

    private static final double MASS = 2; // in kg
    private static final double HEIGHT = 0.1; // In meters.

    public TinyRock( boolean isMystery ) {
        super( MASS, Images.TINY_ROCK, HEIGHT, new Point2D.Double( 0, 0 ), isMystery );
    }
}
