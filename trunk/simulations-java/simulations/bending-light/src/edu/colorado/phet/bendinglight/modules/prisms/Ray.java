// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import edu.colorado.phet.bendinglight.model.BendingLightModel;
import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

/**
 * A single immutable ray, used in the ray propagation algorithm.
 *
 * @author Sam Reid
 */
public class Ray {
    public final ImmutableVector2D tail;
    public final ImmutableVector2D directionUnitVector;
    public final double power;
    public final double wavelength;
    public final double mediumIndexOfRefraction;
    public final double frequency;

    public Ray( ImmutableVector2D tail, ImmutableVector2D directionUnitVector, double power, double wavelength, double mediumIndexOfRefraction, double frequency ) {
        this.tail = tail;
        this.power = power;
        this.wavelength = wavelength;
        this.mediumIndexOfRefraction = mediumIndexOfRefraction;
        this.frequency = frequency;
        this.directionUnitVector = directionUnitVector.getNormalizedInstance();
    }

    public double getBaseWavelength() {
        return BendingLightModel.SPEED_OF_LIGHT / frequency;
    }
}
