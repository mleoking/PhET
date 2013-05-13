// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.common.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueResources.Images;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;

import static edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueSimSharing.UserComponents.mediumTrashCan;


/**
 * Model class that represents a medium-size trash can.
 *
 * @author John Blanco
 */
public class MediumTrashCan extends ImageMass {

    private static final double MASS = 30; // in kg
    private static final double HEIGHT = 0.65; // In meters.

    // For sim sharing - tracks the number of instances created, used in the
    // component ID for each instance.
    private static int instanceCount = 0;

    public MediumTrashCan( boolean isMystery ) {
        super( UserComponentChain.chain( mediumTrashCan, instanceCount++ ), MASS, Images.TRASH_CAN, HEIGHT, new Point2D.Double( 0, 0 ), isMystery );
    }
}
