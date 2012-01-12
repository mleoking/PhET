// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;

import static edu.colorado.phet.bendinglight.model.BendingLightModel.SPEED_OF_LIGHT;

/**
 * A single immutable ray, used in the ray propagation algorithm.
 *
 * @author Sam Reid
 */
public class Ray {
    public final ImmutableVector2D tail;
    public final ImmutableVector2D directionUnitVector;
    public final double power;//power of the ray (1 is full power of the laser), will be reduced if partial reflection/refraction
    public final double wavelength;//Wavelength inside the medium (depends on index of refraction)
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

    //Gets the wavelength for this ray if it wasn't inside a medium
    public double getBaseWavelength() {
        return SPEED_OF_LIGHT / frequency;
    }
}
