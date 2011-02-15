// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.prisms;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * For propagation
 *
 * @author Sam Reid
 */
public class Ray {
    public final ImmutableVector2D tail;
    public final ImmutableVector2D directionUnitVector;
    public final double power;

    public Ray( ImmutableVector2D tail, ImmutableVector2D directionUnitVector, double power ) {
        this.tail = tail;
        this.power = power;
        this.directionUnitVector = directionUnitVector.getNormalizedInstance();
    }
}
