// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.common.view;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * Absolute locations in model coordinates (in meters) of where particles flow to leave the drain pipe, and where they leave when they exit the drain pipe.
 *
 * @author Sam Reid
 */
public class DrainFaucetMetrics {

    //Location where particles enter the drain faucet
    public final ImmutableVector2D inputPoint;

    //Location where particles leave the drain faucet
    public final ImmutableVector2D outputPoint;

    public DrainFaucetMetrics( ImmutableVector2D inputPoint, ImmutableVector2D outputPoint ) {
        this.inputPoint = inputPoint;
        this.outputPoint = outputPoint;
    }
}