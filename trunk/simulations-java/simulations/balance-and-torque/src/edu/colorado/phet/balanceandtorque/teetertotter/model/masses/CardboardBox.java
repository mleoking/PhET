// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;


/**
 * Model class that represents a cardboard box.
 *
 * @author John Blanco
 */
public class CardboardBox extends ImageMass {

    private static final double MASS = 25; // in kg
    private static final double HEIGHT = 1.1; // In meters.

    public CardboardBox( boolean isMystery ) {
        super( MASS, Images.CARDBOARD_BOX, HEIGHT, new Point2D.Double( 0, 0 ), isMystery );
    }
}
