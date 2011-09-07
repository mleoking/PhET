// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;


/**
 * Model class that represents a human who is roughly 10 years old.
 *
 * @author John Blanco
 */
public class YoungGirl extends ImageMass {

    private static final double MASS = 30; // in kg
    private static final double STANDING_HEIGHT = 1.4; // In meters.
    private static final double SITTING_HEIGHT = 0.8; // In meters.
    private static final double SITTING_CENTER_OF_MASS_X_OFFSET = 0.07; // In meters, determined visually.  Update if image changes.

    public YoungGirl() {
        super( MASS, Images.YOUNG_GIRL_STANDING, STANDING_HEIGHT, new Point2D.Double( 0, 0 ) );
    }

    @Override public void setOnPlank( boolean onPlank ) {
        if ( onPlank ) {
            heightProperty.set( SITTING_HEIGHT );
            if ( getPosition().getX() > 0 ) {
                imageProperty.set( Images.YOUNG_GIRL_SITTING );
                setCenterOfMassXOffset( SITTING_CENTER_OF_MASS_X_OFFSET );
            }
            else {
                imageProperty.set( BufferedImageUtils.flipX( Images.YOUNG_GIRL_SITTING ) );
                setCenterOfMassXOffset( -SITTING_CENTER_OF_MASS_X_OFFSET );
            }
        }
        else {
            heightProperty.set( STANDING_HEIGHT );
            imageProperty.set( Images.YOUNG_GIRL_STANDING );
        }
    }
}
