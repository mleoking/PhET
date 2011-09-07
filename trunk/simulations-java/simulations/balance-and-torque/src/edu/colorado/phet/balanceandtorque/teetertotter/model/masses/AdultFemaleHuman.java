// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;
import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;


/**
 * Model class that represents a woman.
 *
 * @author John Blanco
 */
public class AdultFemaleHuman extends ImageMass {

    private static final double MASS = 50; // in kg
    private static final double STANDING_HEIGHT = 1.5; // In meters.
    private static final double SITTING_HEIGHT = 0.9; // In meters.
    private static final double SITTING_CENTER_OF_MASS_X_OFFSET = 0.1; // In meters, determined visually.  Update if image changes.

    public AdultFemaleHuman() {
        super( MASS, Images.ADULT_WOMAN_STANDING, STANDING_HEIGHT, new Point2D.Double( 0, 0 ) );
    }

    @Override public void setOnPlank( boolean onPlank ) {
        if ( onPlank ) {
            heightProperty.set( SITTING_HEIGHT );
            if ( getPosition().getX() > 0 ) {
                imageProperty.set( Images.ADULT_WOMAN_SITTING );
                setCenterOfMassXOffset( SITTING_CENTER_OF_MASS_X_OFFSET );
            }
            else {
                // Reverse image if on other side of balance.
                imageProperty.set( BufferedImageUtils.flipX( Images.ADULT_WOMAN_SITTING ) );
                setCenterOfMassXOffset( -SITTING_CENTER_OF_MASS_X_OFFSET );
            }
        }
        else {
            heightProperty.set( STANDING_HEIGHT );
            imageProperty.set( Images.ADULT_WOMAN_STANDING );
            setCenterOfMassXOffset( 0 );
        }
    }
}
