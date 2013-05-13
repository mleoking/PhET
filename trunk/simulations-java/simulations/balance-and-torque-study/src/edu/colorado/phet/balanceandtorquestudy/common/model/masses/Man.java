// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.common.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueResources.Images;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;

import static edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueSimSharing.UserComponents.man;


/**
 * Model class that represents an adult male.
 *
 * @author John Blanco
 */
public class Man extends HumanMass {

    public static final double MASS = 80; // in kg
    private static final double STANDING_HEIGHT = 1.8; // In meters.
    private static final double SITTING_HEIGHT = 0.9; // In meters.
    private static final double SITTING_CENTER_OF_MASS_X_OFFSET = 0.1; // In meters, determined visually.  Update if image changes.

    // For sim sharing - tracks the number of instances created, used in the
    // component ID for each instance.
    private static int instanceCount = 0;

    public Man() {
        super( UserComponentChain.chain( man, instanceCount++ ), MASS, Images.MAN_STANDING, STANDING_HEIGHT, Images.MAN_SITTING, SITTING_HEIGHT,
               new Point2D.Double( 0, 0 ), SITTING_CENTER_OF_MASS_X_OFFSET, false );
    }

    @Override public Mass createCopy() {
        Mass copy = new Man();
        copy.setPosition( this.getPosition() );
        return copy;
    }
}
