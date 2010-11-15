package edu.colorado.phet.fluidpressureandflow.model;

import edu.colorado.phet.common.phetcommon.math.Function;

/**
 * @author Sam Reid
 */
public class Units {
    public double feetToMeters( double feet ) {
        return feet * 0.3048;
    }

    public static interface PressureUnit {
        double siToUnit( double value );

        double toSI( double value );
        String getName();
        String getAbbreviation();

        public static PressureUnit ATMOSPHERE = new LinearUnit( "Atmospheres", "atm", 9.8692E-6 );//http://en.wikipedia.org/wiki/Atmosphere_%28unit%29
        public static PressureUnit PASCAL = new LinearUnit( "Pascal", "Pa", 1 );
        public static PressureUnit PSI = new LinearUnit( "Pounds per square inch", "psi", 145.04E-6 );
    }

    public static class LinearUnit implements PressureUnit {
        Function.LinearFunction linearFunction;
        private final String name;
        private final String abbreviation;

        public LinearUnit( String name, String abbreviation, double siToUnitScale ) {
            this.name = name;
            this.abbreviation = abbreviation;
            linearFunction = new Function.LinearFunction( 0, 1, 0, siToUnitScale );
        }

        public String getName() {
            return name;
        }

        public String getAbbreviation() {
            return abbreviation;
        }

        public double siToUnit( double value ) {
            return linearFunction.evaluate( value );
        }

        public double toSI( double value ) {
            return linearFunction.createInverse().evaluate( value );
        }
    }

}
