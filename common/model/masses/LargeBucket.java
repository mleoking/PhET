// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorque.common.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorque.BalanceAndTorqueResources.Images;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;

import static edu.colorado.phet.balanceandtorque.BalanceAndTorqueSimSharing.UserComponents.largeBucket;


/**
 * Model class that represents a large-size bucket.
 *
 * @author John Blanco
 */
public class LargeBucket extends ImageMass {

    private static final double MASS = 15; // in kg
    private static final double HEIGHT = 0.5; // In meters.

    // For sim sharing - tracks the number of instances created, used in the
    // component ID for each instance.
    private static int instanceCount = 0;

    public LargeBucket( boolean isMystery ) {
        super( UserComponentChain.chain( largeBucket, instanceCount++ ), MASS, Images.METAL_BUCKET, HEIGHT, new Point2D.Double( 0, 0 ), isMystery );
    }
}
