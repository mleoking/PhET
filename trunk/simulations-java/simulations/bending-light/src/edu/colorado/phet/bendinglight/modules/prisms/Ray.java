// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.modules.prisms;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.function.Function2;

/**
 * For propagation
 *
 * @author Sam Reid
 */
public class Ray {
    public final ImmutableVector2D tail;
    public final ImmutableVector2D directionUnitVector;
    public final double power;
    public final Function2<Double, Double, Double> indexOfRefraction;
    public final double wavelength;
    public final double mediumIndexOfRefraction;

    public Ray( ImmutableVector2D tail, ImmutableVector2D directionUnitVector, double power, Function2<Double, Double, Double> indexOfRefraction, double wavelength, double mediumIndexOfRefraction ) {
        this.tail = tail;
        this.power = power;
        this.indexOfRefraction = indexOfRefraction;
        this.wavelength = wavelength;
        this.mediumIndexOfRefraction = mediumIndexOfRefraction;
        this.directionUnitVector = directionUnitVector.getNormalizedInstance();
    }
}
