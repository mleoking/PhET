// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.beerslaw.model;

import edu.colorado.phet.common.phetcommon.model.property.ChangeObserver;
import edu.colorado.phet.common.phetcommon.model.property.CompositeProperty;
import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.Function0;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * Measures the absorbance of a solution in a cuvette.
 * <p>
 * Absorbance model: A = abC
 * <p>
 * Transmittance model: T = 10^A
 * <p>
 * where:
 * <ul>
 * <li>A is absorbance
 * <li>T is transmittance (1=fully transmitted, 0=fully absorbed)
 * <li>a is molar absorptivity (1/(cm*M))
 * <li>b is path length (cm)
 * <li>C is concentration (M)
 * </ul>
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class Absorbance {

    private final CompositeProperty<Double> molarAbsorptivity; // a, units=1/(cm*M)
    private final CompositeProperty<Double> pathLength; // b, units=cm
    private final Property<Double> concentration; // C, units=M

    public final CompositeProperty<Double> value;

    public Absorbance( final Property<BeersLawSolution> solution, final Light light, final Cuvette cuvette ) {

        // a: molar absorptivity
        this.molarAbsorptivity = new CompositeProperty<Double>( new Function0<Double>() {
            public Double apply() {
                return solution.get().molarAbsorptivityData.wavelengthToMolarAbsorptivity( light.wavelength.get() );
            }
        }, solution, light.wavelength );

        // b: path length, synonymous with cuvette width
        this.pathLength = new CompositeProperty<Double>( new Function0<Double>() {
            public Double apply() {
                return cuvette.width.get();
            }
        }, cuvette.width );

        // C: concentration
        {
            this.concentration = new Property<Double>( solution.get().concentration.get() );

            // Observe the concentration property of the current solution.
            final VoidFunction1<Double> concentrationObserver = new VoidFunction1<Double>() {
                public void apply( Double concentration ) {
                    Absorbance.this.concentration.set( concentration );
                }
            };
            solution.get().concentration.addObserver( concentrationObserver );

            // Rewire the concentration observer when the solution changes.
            solution.addObserver( new ChangeObserver<BeersLawSolution>() {
                public void update( BeersLawSolution newSolution, BeersLawSolution oldSolution ) {
                    oldSolution.concentration.removeObserver( concentrationObserver );
                    newSolution.concentration.addObserver( concentrationObserver );
                }
            } );
        }

        // compute absorbance: A = abC
        this.value = new CompositeProperty<Double>( new Function0<Double>() {
            public Double apply() {
                return getAbsorbance( molarAbsorptivity.get(), pathLength.get(), concentration.get() );
            }
        }, molarAbsorptivity, pathLength, concentration );
    }

    // Gets absorbance for a specified path length.
    public double getAbsorbanceAt( double pathLength ) {
        return getAbsorbance( molarAbsorptivity.get(), pathLength, concentration.get() );
    }

    // Gets transmittance for a specified path length.
    public double getTransmittanceAt( double pathLength ) {
        return getTransmittance( getAbsorbanceAt( pathLength ) );
    }

    // Converts absorbance to transmittance.
    public double getTransmittance() {
        return getTransmittance( value.get() );
    }

    // General model of absorbance: A = abC
    private static double getAbsorbance( double molarAbsorptivity, double pathLength, double concentration ) {
        return molarAbsorptivity * pathLength * concentration;
    }

    // General model of transmittance: T = 10^A
    private static double getTransmittance( double absorbance ) {
        return Math.pow( 10, -absorbance );
    }
}
