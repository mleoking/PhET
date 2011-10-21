// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;


/**
 * Model class that represents a statue.
 *
 * @author John Blanco
 */
public class StatueOfOdysseus extends ImageMass {

    private static final double MASS = 90; // in kg
    private static final double HEIGHT = 1.4; // In meters.

    public StatueOfOdysseus( boolean isMystery ) {
        super( MASS, Images.STATUE_OF_ODYSSEUS, HEIGHT, new Point2D.Double( 0, 0 ), isMystery );
        setCenterOfMassXOffset( 0.2 );
    }
}
