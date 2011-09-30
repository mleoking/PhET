// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;


/**
 * Model class that represents a trash can.
 *
 * @author John Blanco
 */
public class TrashCan extends ImageMass {

    private static final double MASS = 15; // in kg
    private static final double HEIGHT = 1.2; // In meters.

    public TrashCan() {
        super( MASS, Images.TRASH_CAN, HEIGHT, new Point2D.Double( 0, 0 ), true );
    }
}
