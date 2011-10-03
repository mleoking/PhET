// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;


/**
 * Model class that represents a medium size trash can.
 *
 * @author John Blanco
 */
public class MediumTrashCan extends ImageMass {

    private static final double MASS = 10; // in kg
    private static final double HEIGHT = 0.60; // In meters.

    public MediumTrashCan() {
        super( MASS, Images.TRASH_CAN, HEIGHT, new Point2D.Double( 0, 0 ), true );
    }
}
