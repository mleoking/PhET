// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.common.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueStudyResources.Images;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;

import static edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueSimSharing.UserComponents.girl;


/**
 * Model class that represents a girl who is roughly 10 years old.  The height
 * and weight information came from:
 * http://www.disabled-world.com/artman/publish/height-weight-teens.shtml
 *
 * @author John Blanco
 */
public class Girl extends HumanMass {

    public static final double MASS = 30; // in kg
    private static final double STANDING_HEIGHT = 1.3; // In meters.
    private static final double SITTING_HEIGHT = 0.75; // In meters.
    private static final double SITTING_CENTER_OF_MASS_X_OFFSET = 0.07; // In meters, determined visually.  Update if image changes.

    // For sim sharing - tracks the number of instances created, used in the
    // component ID for each instance.
    private static int instanceCount = 0;

    public Girl() {
        super( UserComponentChain.chain( girl, instanceCount++ ), MASS, Images.GIRL_STANDING, STANDING_HEIGHT, Images.GIRL_SITTING, SITTING_HEIGHT, new Point2D.Double( 0, 0 ), SITTING_CENTER_OF_MASS_X_OFFSET, false );
    }

    @Override public Mass createCopy() {
        Mass copy = new Girl();
        copy.setPosition( this.getPosition() );
        return copy;
    }
}
