// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;


/**
 * Model class that represents an adult male.
 *
 * @author John Blanco
 */
public class AdultMaleHuman extends HumanMass {

    private static final double MASS = 80; // in kg
    private static final double STANDING_HEIGHT = 1.7; // In meters.
    private static final double SITTING_HEIGHT = 0.9; // In meters.
    private static final double SITTING_CENTER_OF_MASS_X_OFFSET = 0.1; // In meters, determined visually.  Update if image changes.

    public AdultMaleHuman() {
        super( MASS, Images.ADULT_MAN_STANDING, STANDING_HEIGHT, Images.ADULT_MAN_SITTING, SITTING_HEIGHT,
               new Point2D.Double( 0, 0 ), SITTING_CENTER_OF_MASS_X_OFFSET, false );
    }
}
