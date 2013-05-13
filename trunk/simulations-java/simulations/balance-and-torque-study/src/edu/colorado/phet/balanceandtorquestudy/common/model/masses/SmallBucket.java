// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.common.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueResources.Images;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;

import static edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueSimSharing.UserComponents.smallBucket;


/**
 * Model class that represents a small bucket.
 *
 * @author John Blanco
 */
public class SmallBucket extends ImageMass {

    private static final double MASS = 2.5; // in kg
    private static final double HEIGHT = 0.3; // In meters.

    // For sim sharing - tracks the number of instances created, used in the
    // component ID for each instance.
    private static int instanceCount = 0;

    public SmallBucket( boolean isMystery ) {
        super( UserComponentChain.chain( smallBucket, instanceCount++ ), MASS, Images.BLUE_BUCKET, HEIGHT, new Point2D.Double( 0, 0 ), isMystery );
    }
}
