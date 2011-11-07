// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;


/**
 * Model class that represents a small rock.
 *
 * @author John Blanco
 */
public class SmallRock extends ImageMass {

    private static final double MASS = 30; // in kg
    private static final double HEIGHT = 0.25; // In meters.

    public SmallRock( boolean isMystery ) {
        super( MASS, Images.ROCK_4, HEIGHT, new Point2D.Double( 0, 0 ), isMystery );
    }
}
