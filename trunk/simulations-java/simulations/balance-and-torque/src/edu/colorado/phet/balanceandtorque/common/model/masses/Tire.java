// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;

import static edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing.UserComponents.tire;


/**
 * Model class that represents a tire.
 *
 * @author John Blanco
 */
public class Tire extends ImageMass {

    private static final double MASS = 15; // in kg
    private static final double HEIGHT = 0.15; // In meters.

    // For sim sharing - tracks the number of instances created, used in the
    // component ID for each instance.
    private static int instanceCount = 0;

    public Tire( boolean isMystery ) {
        super( UserComponentChain.chain( tire, instanceCount++ ), MASS, Images.TIRE, HEIGHT, new Point2D.Double( 0, 0 ), isMystery );
    }
}
