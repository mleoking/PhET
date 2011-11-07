// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;


/**
 * Model class that represents a medium-size rock.
 *
 * @author John Blanco
 */
public class MediumRock extends ImageMass {

    private static final double MASS = 40; // in kg
    private static final double HEIGHT = 0.4; // In meters.

    public MediumRock( boolean isMystery ) {
        super( MASS, Images.ROCK_1, HEIGHT, new Point2D.Double( 0, 0 ), isMystery );
    }
}
