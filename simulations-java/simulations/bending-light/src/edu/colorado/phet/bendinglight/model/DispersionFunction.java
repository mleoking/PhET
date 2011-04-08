// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.Pair;

import static edu.colorado.phet.bendinglight.model.BendingLightModel.WAVELENGTH_RED;

/**
 * Models dispersion functions for each material.  Uses the actual dispersion equation for air (A) and the actual dispersion equation for glass (G)
 * then interpolates between the functions n(lambda) = beta * A(lambda) + (1-beta) * G(lambda) where 0<=beta<=infinity is a characteristic of the material.
 * The material is characterized by a reference wavelength, so that when light is the specified wavelength, the index of refraction takes the reference value.
 *
 * @author Sam Reid
 */
public class DispersionFunction {
    private double referenceIndexOfRefraction;
    private double referenceWavelength;

    //See http://en.wikipedia.org/wiki/Sellmeier_equation
    public double getSellmeierValue( double wavelength ) {
        double L2 = wavelength * wavelength;
        double B1 = 1.03961212;
        double B2 = 0.231792344;
        double B3 = 1.01046945;
        double C1 = 6.00069867E-3 * 1E-12;//convert to metric
        double C2 = 2.00179144E-2 * 1E-12;
        double C3 = 1.03560653E2 * 1E-12;
        return Math.sqrt( 1 + B1 * L2 / ( L2 - C1 ) + B2 * L2 / ( L2 - C2 ) + B3 * L2 / ( L2 - C3 ) );
    }

    public DispersionFunction( final double indexForRed ) {//Index of refraction for red wavelength
        this( indexForRed, WAVELENGTH_RED );
    }

    public DispersionFunction( final double referenceIndexOfRefraction, final double wavelength ) {
        this.referenceIndexOfRefraction = referenceIndexOfRefraction;
        this.referenceWavelength = wavelength;
    }

    public double getIndexOfRefractionForRed() {
        return getIndexOfRefraction( WAVELENGTH_RED );
    }

    //See class-level documentation for an explanation of this algorithm
    public double getIndexOfRefraction( double wavelength ) {
        double nAirReference = getAirIndex( referenceWavelength );
        double nGlassReference = getSellmeierValue( referenceWavelength );

        double delta = nGlassReference - nAirReference;
        double x = ( referenceIndexOfRefraction - nAirReference ) / delta;//0 to 1 (air to glass)

        x = MathUtil.clamp( 0, x, Double.POSITIVE_INFINITY );

        final double index = x * getSellmeierValue( wavelength ) + ( 1 - x ) * getAirIndex( wavelength );
        return index;
    }

    //See http://refractiveindex.info/?group=GASES&material=Air
    private double getAirIndex( double wavelength ) {
        return 1 + 5792105E-8 / ( 238.0185 - Math.pow( wavelength * 1E6, -2 ) ) + 167917E-8 / ( 57.362 - Math.pow( wavelength * 1E6, -2 ) );
    }

    /*
     * Prints out the dispersion functions for several substances.
     */
    public static void main( String[] args ) {
        final ArrayList<Pair<String, Double>> states = new ArrayList<Pair<String, Double>>() {{
            add( new Pair<String, Double>( "Air", 1.000293 ) );
            add( new Pair<String, Double>( "Water", 1.333 ) );
            add( new Pair<String, Double>( "Glass", 1.5 ) );
            add( new Pair<String, Double>( "Diamond", 2.419 ) );
        }};

        ArrayList<DispersionFunction> dispersionFunctions = new ArrayList<DispersionFunction>() {{
            for ( Pair<String, Double> state : states ) {
                add( new DispersionFunction( state._2 ) );
                System.out.println( state + " -> " + new DispersionFunction( state._2 ).getIndexOfRefractionForRed() );
            }
        }};
        System.out.println();

        double minLambda = 350E-9;
        double maxLambda = 800E-9;
        int numSteps = 100;
        double dLambda = ( maxLambda - minLambda ) / numSteps;
        System.out.print( "Wavelength\t" );
        for ( Pair<String, Double> state : states ) {
            System.out.print( state._1 + "\t" );
        }
        System.out.println();
        for ( double lambda = minLambda; lambda <= maxLambda; lambda += dLambda ) {
            System.out.print( lambda + "\t" );
            for ( DispersionFunction dispersionFunction : dispersionFunctions ) {
                System.out.print( dispersionFunction.getIndexOfRefraction( lambda ) + "\t" );
            }
            System.out.println();
        }
    }
}
