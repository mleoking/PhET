// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;


/**
 * Model class that represents a rock.
 *
 * @author John Blanco
 */
public class CinderBlock extends ImageMass {

    private static final double MASS = 7.5; // in kg
    private static final double HEIGHT = 0.2; // In meters.

    public CinderBlock( boolean isMystery ) {
        super( MASS, Images.CINDER_BLOCK, HEIGHT, new Point2D.Double( 0, 0 ), isMystery );
    }
}
