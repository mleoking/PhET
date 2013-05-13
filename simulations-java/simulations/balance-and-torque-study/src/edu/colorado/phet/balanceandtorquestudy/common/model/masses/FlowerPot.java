// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.common.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueStudyResources.Images;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;

import static edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueSimSharing.UserComponents.flowerPot;


/**
 * Model class that represents a flower pot.
 *
 * @author John Blanco
 */
public class FlowerPot extends ImageMass {

    private static final double MASS = 5; // in kg
    private static final double HEIGHT = 0.55; // In meters.

    // For sim sharing - tracks the number of instances created, used in the
    // component ID for each instance.
    private static int instanceCount = 0;

    public FlowerPot( boolean isMystery ) {
        super( UserComponentChain.chain( flowerPot, instanceCount++ ), MASS, Images.FLOWER_POT, HEIGHT, new Point2D.Double( 0, 0 ), isMystery );
    }
}
