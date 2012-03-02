// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

/**
 * Transmittance model: T = 10^A = 10^(abC), where
 * <ul>
 * <li>A is absorbance
 * <li>a is path length (cm)
 * <li>b is path length (cm)
 * <li>C is concentration (M)
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Transmittance {

    public final CompositeProperty<Double> value; // 1=fully transmitted, 0=fully absorbed

    public Transmittance( final Absorbance absorbance ) {
        value = new CompositeProperty<Double>( new Function0<Double>() {
            public Double apply() {
                return getTransmittance( absorbance.value.get() );
            }
        }, absorbance.value );
    }

    // General model of transmittance: T = 10^A
    public static double getTransmittance( double absorbance ) {
        return Math.pow( 10, -absorbance );
    }

    // General model of transmittance: T = 10^(abC) = 10^A
    public static double getTransmittance( double molarAbsorptivity, double pathLength, double concentration ) {
        return getTransmittance( Absorbance.getAbsorbance( molarAbsorptivity, pathLength, concentration ) );
    }
}
