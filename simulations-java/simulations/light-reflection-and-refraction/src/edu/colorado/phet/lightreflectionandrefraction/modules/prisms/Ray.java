// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lightreflectionandrefraction.modules.prisms;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.util.Function1;

/**
 * For propagation
 *
 * @author Sam Reid
 */
public class Ray {
    public final ImmutableVector2D tail;
    public final ImmutableVector2D directionUnitVector;
    public final double power;
    public final Function1<Double, Double> indexOfRefraction;
    public final Function1<Double, Double> oppositeIndexOfRefraction;
    public final double wavelength;

    public Ray( ImmutableVector2D tail, ImmutableVector2D directionUnitVector, double power, Function1<Double, Double> indexOfRefraction, Function1<Double, Double> oppositeIndexOfRefraction, double wavelength ) {
        this.tail = tail;
        this.power = power;
        this.indexOfRefraction = indexOfRefraction;
        this.oppositeIndexOfRefraction = oppositeIndexOfRefraction;
        this.wavelength = wavelength;
        this.directionUnitVector = directionUnitVector.getNormalizedInstance();
//        System.out.println( "power = " + power );
    }
}
