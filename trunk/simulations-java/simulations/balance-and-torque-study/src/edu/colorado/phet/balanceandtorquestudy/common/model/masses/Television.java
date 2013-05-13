// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.common.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueStudyResources.Images;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;

import static edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueSimSharing.UserComponents.television;


/**
 * Model class that represents a television.
 *
 * @author John Blanco
 */
public class Television extends ImageMass {

    private static final double MASS = 10; // in kg
    private static final double HEIGHT = 0.5; // In meters.

    // For sim sharing - tracks the number of instances created, used in the
    // component ID for each instance.
    private static int instanceCount = 0;

    public Television( boolean isMystery ) {
        super( UserComponentChain.chain( television, instanceCount++ ), MASS, Images.OLD_TELEVISION, HEIGHT, new Point2D.Double( 0, 0 ), isMystery );
    }
}
