// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;


/**
 * Model class that represents a girl who is roughly 10 years old.  The height
 * and weight information came from:
 * http://www.disabled-world.com/artman/publish/height-weight-teens.shtml
 *
 * @author John Blanco
 */
public class YoungGirl extends HumanMass {

    public static final double MASS = 30; // in kg
    private static final double STANDING_HEIGHT = 1.3; // In meters.
    private static final double SITTING_HEIGHT = 0.75; // In meters.
    private static final double SITTING_CENTER_OF_MASS_X_OFFSET = 0.07; // In meters, determined visually.  Update if image changes.

    public YoungGirl() {
        super( MASS, Images.YOUNG_GIRL_STANDING_01, STANDING_HEIGHT, Images.YOUNG_GIRL_SITTING_01, SITTING_HEIGHT, new Point2D.Double( 0, 0 ), SITTING_CENTER_OF_MASS_X_OFFSET, false );
    }
}
