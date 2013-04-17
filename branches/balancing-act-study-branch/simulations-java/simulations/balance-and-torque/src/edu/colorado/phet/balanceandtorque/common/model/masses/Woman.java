// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;
import edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;


/**
 * Model class that represents a woman that can be moved on and off of the
 * balance.
 *
 * @author John Blanco
 */
public class Woman extends HumanMass {

    public static final double MASS = 60; // in kg
    private static final double STANDING_HEIGHT = 1.65; // In meters.
    private static final double SITTING_HEIGHT = 0.825; // In meters.
    private static final double SITTING_CENTER_OF_MASS_X_OFFSET = 0.1; // In meters, determined visually.  Update if image changes.

    // For sim sharing - tracks the number of instances created, used in the
    // component ID for each instance.
    private static int instanceCount = 0;

    public Woman() {
        super( UserComponentChain.chain( BalanceAndTorqueSimSharing.UserComponents.woman, instanceCount++ ), MASS, Images.WOMAN_STANDING, STANDING_HEIGHT, Images.WOMAN_SITTING, SITTING_HEIGHT, new Point2D.Double( 0, 0 ), SITTING_CENTER_OF_MASS_X_OFFSET, false );
    }

    @Override public Mass createCopy() {
        Mass copy = new Woman();
        copy.setPosition( this.getPosition() );
        return copy;
    }
}
