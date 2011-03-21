// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

import static edu.colorado.phet.bendinglight.model.BendingLightModel.WAVELENGTH_RED;

/**
 * @author Sam Reid
 */
public class DispersionFunction {
    public Function1<Double, Double> function;
    //See http://en.wikipedia.org/wiki/Sellmeier_equation
    final Function sellmeierEquation = new Function() {
        public double evaluate( double wavelength ) {
            double L2 = wavelength * wavelength;
            double B1 = 1.03961212;
            double B2 = 0.231792344;
            double B3 = 1.01046945;
            double C1 = 6.00069867E-3 * 1E-12;//convert to metric
            double C2 = 2.00179144E-2 * 1E-12;
            double C3 = 1.03560653E2 * 1E-12;
            return Math.sqrt( 1 + B1 * L2 / ( L2 - C1 ) + B2 * L2 / ( L2 - C2 ) + B3 * L2 / ( L2 - C3 ) );
        }

        public Function createInverse() {
            throw new RuntimeException( "No inverse yet" );
        }
    };

    public DispersionFunction( final double indexOfRefraction, final double wavelength ) {
        function = new Function1<Double, Double>() {
            public Double apply( Double x ) {
                //choose from a family of curves but making sure that we get the specified value for red
                return sellmeierEquation.evaluate( x ) + indexOfRefraction - sellmeierEquation.evaluate( wavelength );
            }
        };
    }

    public DispersionFunction( final double indexForRed ) {//Index of refraction for red wavelength
        function = new Function1<Double, Double>() {
            public Double apply( Double x ) {
                //choose from a family of curves but making sure that we get the specified value for red
                return sellmeierEquation.evaluate( x ) + indexForRed - sellmeierEquation.evaluate( WAVELENGTH_RED );
            }
        };
    }

    public double getIndexOfRefractionForRed() {
        return getIndexOfRefraction( WAVELENGTH_RED );
    }

    public double getIndexOfRefraction( double wavelength ) {
        return function.apply( wavelength );
    }
}
