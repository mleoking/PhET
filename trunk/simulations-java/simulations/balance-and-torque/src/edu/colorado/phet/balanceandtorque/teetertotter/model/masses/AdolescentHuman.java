// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;

import static edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images.ADOLESCENT_SITTING;
import static edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images.ADOLESCENT_STANDING;

/**
 * Model class that represents a human who is roughly 10 years old.
 *
 * @author John Blanco
 */
public class AdolescentHuman extends ImageMass {

    private static final double MASS = 20; // in kg
    private static final double STANDING_HEIGHT = 1.2; // In meters.
    private static final double SITTING_HEIGHT = 0.7; // In meters.

    public AdolescentHuman() {
        super( MASS, ADOLESCENT_STANDING, STANDING_HEIGHT, new Point2D.Double( 0, 0 ) );
    }

    @Override public void setOnPlank( boolean onPlank ) {
        if ( onPlank ) {
            height = SITTING_HEIGHT;
            if ( getPosition().getX() > 0 ) {
                imageProperty.set( ADOLESCENT_SITTING );
            }
            else {
                imageProperty.set( BufferedImageUtils.flipX( ADOLESCENT_SITTING ) );
            }
        }
        else {
            height = STANDING_HEIGHT;
            imageProperty.set( ADOLESCENT_STANDING );
        }
    }
}
