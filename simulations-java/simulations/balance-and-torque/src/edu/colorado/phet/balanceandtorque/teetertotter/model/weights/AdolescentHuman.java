// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.weights;

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.balanceandtorque.TeeterTotterTorqueApplication;

/**
 * Model class that represents a human who is roughly 10 years old.
 *
 * @author John Blanco
 */
public class AdolescentHuman extends ImageWeight {

    private static final double MASS = 20; // in kg
    private static final double STANDING_HEIGHT = 1.2; // In meters.
    private static final double SITTING_HEIGHT = 0.7; // In meters.

    private static final BufferedImage STANDING_IMAGE = TeeterTotterTorqueApplication.RESOURCES.getImage( "adolescent-standing.png" );
    private static final BufferedImage SITTING_IMAGE = TeeterTotterTorqueApplication.RESOURCES.getImage( "adolescent-sitting.png" );

    public AdolescentHuman() {
        super( MASS, STANDING_IMAGE, STANDING_HEIGHT, new Point2D.Double( 0, 0 ) );
    }

    @Override public void setOnPlank( boolean onPlank ) {
        if ( onPlank ) {
            height = SITTING_HEIGHT;
            if ( getPosition().getX() > 0 ) {
                imageProperty.set( SITTING_IMAGE );
            }
            else {
                imageProperty.set( BufferedImageUtils.flipX( SITTING_IMAGE ) );
            }
        }
        else {
            height = STANDING_HEIGHT;
            imageProperty.set( STANDING_IMAGE );
        }
    }
}
