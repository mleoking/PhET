// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;


/**
 * Model class that represents a boy who is roughly 9 years old.
 *
 * @author John Blanco
 */
public class AdolescentBoy extends HumanMass {

    private static final double MASS = 20; // in kg
    private static final double STANDING_HEIGHT = 1.1; // In meters.
    private static final double SITTING_HEIGHT = 0.7; // In meters.
    private static final double SITTING_CENTER_OF_MASS_X_OFFSET = 0.07; // In meters, determined visually.  Update if image changes.

    public AdolescentBoy() {
        super( MASS, Images.ADOLESCENT_STANDING, STANDING_HEIGHT, Images.COMPACT_ADOLESCENT_SITTING, SITTING_HEIGHT, new Point2D.Double( 0, 0 ), SITTING_CENTER_OF_MASS_X_OFFSET, false );
    }
}
