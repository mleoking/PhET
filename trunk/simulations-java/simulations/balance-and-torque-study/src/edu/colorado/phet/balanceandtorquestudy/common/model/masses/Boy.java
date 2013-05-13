// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.common.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueResources.Images;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;

import static edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueSimSharing.UserComponents.boy;


/**
 * Model class that represents a boy who is roughly 6 years old.  The data
 * for his height and weight came from:
 * http://www.disabled-world.com/artman/publish/height-weight-teens.shtml
 *
 * @author John Blanco
 */
public class Boy extends HumanMass {

    public static final double MASS = 20; // in kg
    private static final double STANDING_HEIGHT = 1.1; // In meters.
    private static final double SITTING_HEIGHT = 0.7; // In meters.
    private static final double SITTING_CENTER_OF_MASS_X_OFFSET = 0.07; // In meters, determined visually.  Update if image changes.

    // For sim sharing - tracks the number of instances created, used in the
    // component ID for each instance.
    private static int instanceCount = 0;

    public Boy() {
        super( UserComponentChain.chain( boy, instanceCount++ ), MASS, Images.BOY_STANDING, STANDING_HEIGHT, Images.BOY_SITTING, SITTING_HEIGHT, new Point2D.Double( 0, 0 ), SITTING_CENTER_OF_MASS_X_OFFSET, false );
    }

    @Override public Mass createCopy() {
        Mass copy = new Boy();
        copy.setPosition( this.getPosition() );
        return copy;
    }
}
