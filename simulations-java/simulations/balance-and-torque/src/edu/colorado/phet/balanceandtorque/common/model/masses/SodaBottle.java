// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;

import static edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing.UserComponents.sodaBottle;


/**
 * Model class that represents a bottle of soda.
 *
 * @author John Blanco
 */
public class SodaBottle extends ImageMass {

    private static final double MASS = 2.5; // in kg
    private static final double HEIGHT = 0.5; // In meters.

    // For sim sharing - tracks the number of instances created, used in the
    // component ID for each instance.
    private static int instanceCount = 0;

    public SodaBottle( boolean isMystery ) {
        super( UserComponentChain.chain( sodaBottle, instanceCount++ ), MASS, Images.SODA_BOTTLE, HEIGHT, new Point2D.Double( 0, 0 ), isMystery );
    }
}
