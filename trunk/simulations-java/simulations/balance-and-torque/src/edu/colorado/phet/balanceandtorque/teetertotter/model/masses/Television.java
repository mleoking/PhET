// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;


/**
 * Model class that represents a television.
 *
 * @author John Blanco
 */
public class Television extends ImageMass {

    private static final double MASS = 10; // in kg
    private static final double HEIGHT = 0.5; // In meters.

    public Television() {
        super( MASS, Images.OLD_TELEVISION, HEIGHT, new Point2D.Double( 0, 0 ), true );
    }
}
