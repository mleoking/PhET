// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;

import static edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing.UserComponents.pottedPlant;


/**
 * Model class that represents a potted plant.
 *
 * @author John Blanco
 */
public class PottedPlant extends ImageMass {

    private static final double MASS = 10; // in kg
    private static final double HEIGHT = 0.65; // In meters.

    // For sim sharing - tracks the number of instances created, used in the
    // component ID for each instance.
    private static int instanceCount = 0;

    public PottedPlant( boolean isMystery ) {
        super( UserComponentChain.chain( pottedPlant, instanceCount++ ), MASS, Images.POTTED_PLANT, HEIGHT, new Point2D.Double( 0, 0 ), isMystery );
    }
}
