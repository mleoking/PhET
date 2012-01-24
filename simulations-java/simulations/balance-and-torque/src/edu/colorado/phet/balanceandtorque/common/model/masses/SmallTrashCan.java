// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;

import static edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing.UserComponents.smallTrashCan;


/**
 * Model class that represents a small trash can.
 *
 * @author John Blanco
 */
public class SmallTrashCan extends ImageMass {

    private static final double MASS = 10; // in kg
    private static final double HEIGHT = 0.55; // In meters.

    public SmallTrashCan( boolean isMystery ) {
        this( DEFAULT_INITIAL_LOCATION, isMystery );
    }

    // For sim sharing - tracks the number of instances created, used in the
    // component ID for each instance.
    private static int instanceCount = 0;

    public SmallTrashCan( Point2D initialPosition, boolean isMystery ) {
        super( UserComponentChain.chain( smallTrashCan, instanceCount++ ), MASS, Images.TRASH_CAN, HEIGHT, initialPosition, isMystery );
    }
}
