// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.teetertotter.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;


/**
 * Model class that represents a potted plant.
 *
 * @author John Blanco
 */
public class PottedPlant extends ImageMass {

    private static final double MASS = 10; // in kg
    private static final double HEIGHT = 0.65; // In meters.

    public PottedPlant( boolean isMystery ) {
        super( MASS, Images.POTTED_PLANT, HEIGHT, new Point2D.Double( 0, 0 ), isMystery );
    }
}
