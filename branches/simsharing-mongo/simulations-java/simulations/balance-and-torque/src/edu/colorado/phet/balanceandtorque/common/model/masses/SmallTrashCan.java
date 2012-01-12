// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;


/**
 * Model class that represents a small trash can.
 *
 * @author John Blanco
 */
public class SmallTrashCan extends ImageMass {

    private static final double MASS = 10; // in kg
    private static final double HEIGHT = 0.55; // In meters.

    public SmallTrashCan( boolean isMystery ) {
        this( DEFAULT_INITIAL_LOCATION, isMystery );
    }

    public SmallTrashCan( Point2D initialPosition, boolean isMystery ) {
        super( MASS, Images.TRASH_CAN, HEIGHT, initialPosition, isMystery );
    }
}
