// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.balanceandtorquestudy.common.model.masses;

import java.awt.geom.Point2D;

import edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueStudyResources.Images;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;

import static edu.colorado.phet.balanceandtorquestudy.BalanceAndTorqueSimSharing.UserComponents.fireExtinguisher;


/**
 * Model class that represents a fire extinguisher.
 *
 * @author John Blanco
 */
public class FireExtinguisher extends ImageMass {

    private static final double MASS = 5; // in kg
    private static final double HEIGHT = 0.5; // In meters.

    // For sim sharing - tracks the number of instances created, used in the
    // component ID for each instance.
    private static int instanceCount = 0;

    public FireExtinguisher( Point2D initialPosition, boolean isMystery ) {
        super( UserComponentChain.chain( fireExtinguisher, instanceCount++ ), MASS, Images.FIRE_EXTINGUISHER, HEIGHT, initialPosition, isMystery );
        setCenterOfMassXOffset( 0.04 );
    }
}
