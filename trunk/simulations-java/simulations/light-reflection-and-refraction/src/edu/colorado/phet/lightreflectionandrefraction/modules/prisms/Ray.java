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
    public final double indexOfRefraction;
    public final double oppositeIndexOfRefraction;

    public Ray( ImmutableVector2D tail, ImmutableVector2D directionUnitVector, double power, double indexOfRefraction, double oppositeIndexOfRefraction ) {
        this.tail = tail;
        this.power = power;
        this.indexOfRefraction = indexOfRefraction;
        this.oppositeIndexOfRefraction = oppositeIndexOfRefraction;
        this.directionUnitVector = directionUnitVector.getNormalizedInstance();
    }
}
