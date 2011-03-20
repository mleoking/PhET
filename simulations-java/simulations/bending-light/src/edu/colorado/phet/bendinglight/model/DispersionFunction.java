// Co
// pyright 2002-2011, University of Colorado
package edu.colorado.phet.bendinglight.model;

import edu.colorado.phet.common.phetcommon.math.Function;

import static edu.colorado.phet.bendinglight.model.BendingLightModel.WAVELENGTH_RED;

/**
 * @author Sam Reid
 */
public class DispersionFunction {
    public Function function;
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
            final double n = Math.sqrt( 1 + B1 * L2 / ( L2 - C1 ) + B2 * L2 / ( L2 - C2 ) + B3 * L2 / ( L2 - C3 ) );
//                System.out.println( "n (L)= n(" + wavelength + ") = " + n );
            return n;
        }

        public Function createInverse() {
            throw new RuntimeException( "No inverse yet" );
        }
    };

    public DispersionFunction( final double indexOfRefraction, final double wavelength ) {
        function = new Function() {
            public double evaluate( double x ) {
                //choose from a family of curves but making sure that we get the specified value for red
                return sellmeierEquation.evaluate( x ) + indexOfRefraction - sellmeierEquation.evaluate( wavelength );
            }

            public Function createInverse() {
                return null;
            }
        };
    }

    public DispersionFunction( final double indexForRed ) {//Index of refraction for red wavelength
//        function = new Function.LinearFunction( WAVELENGTH_RED, MAX_WAVELENGTH / 1E9, indexForRed, indexForRed - 0.012 );//having the delta be more than 0.012 causes speed of light to become noticably more than 1.00 (readout would be 1.01)
        function = new Function() {
            public double evaluate( double x ) {
                //choose from a family of curves but making sure that we get the specified value for red
                return sellmeierEquation.evaluate( x ) + indexForRed - sellmeierEquation.evaluate( WAVELENGTH_RED );
            }

            public Function createInverse() {
                return null;
            }
        };
    }

    public double getIndexOfRefractionForRed() {
        return getIndexOfRefraction( WAVELENGTH_RED );
    }

    public double getIndexOfRefraction( double wavelength ) {
        return function.evaluate( wavelength );
    }
}
