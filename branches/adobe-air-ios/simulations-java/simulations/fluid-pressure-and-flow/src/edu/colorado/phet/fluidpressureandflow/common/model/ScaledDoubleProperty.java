// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.fluidpressureandflow.common.model;

import java.math.BigDecimal;

import edu.colorado.phet.common.phetcommon.model.property.Property;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;

/**
 * This provides a 2-way mapping between properties, but where one of the values is scaled by a specified factor.
 * This is used to make the fluid density control work flexibly with different units.
 *
 * @author Sam Reid
 */
public class ScaledDoubleProperty extends Property<Double> {

    //Round off the value to prevent notification cycles
    private static double round( double value, int numDecimalPlaces ) {

        //Round to 8 decimal places
        return new BigDecimal( value ).setScale( numDecimalPlaces, BigDecimal.ROUND_HALF_UP ).doubleValue();
    }

    public ScaledDoubleProperty( final Property<Double> property, final double scale ) {
        this( property, scale, 8 );
    }

    public ScaledDoubleProperty( final Property<Double> property, final double scale, final int numDecimalPlaces ) {
        super( property.get() * scale );

        //Wire up for 2-way notifications, but make sure the value is rounded to prevent notification cycles caused by floating point error
        //For example, setting x = 0.22286099728778608, then scaling by scale=0.3642612329277407 then scaling back by the same number does not produce x, it produces x'=0.22286099728778605
        //This can cause looping notification cycles.  So we round the number to try to avoid this problem
        property.addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                set( round( value * scale, numDecimalPlaces ) );
            }
        } );
        addObserver( new VoidFunction1<Double>() {
            public void apply( Double value ) {
                property.set( round( value / scale, numDecimalPlaces ) );
            }
        } );
    }

    static class Test2 {
        public static void main( String[] args ) {
            double x = 0.22286099728778608;
            double scale = 0.3642612329277407;
            double z = x * scale / scale;
            System.out.println( "z = " + z );

            double a = 0.22286099728778608 * 0.3642612329277407 / 0.3642612329277407 / 0.22286099728778608;
            System.out.println( "a = " + a );

            final long precision = 1000000;

            double thisValue = Math.round( x * scale * precision ) / (double) precision;
            System.out.println( "thisValue = " + thisValue );
            System.out.println( "x = " + x * scale );

            System.out.println( "round( x ) = " + round( x, 8 ) );
        }
    }

    public static void main( String[] args ) {
        double x = Math.random();
        double scale = Math.random();
        double y = x * scale;
        double z = y / scale;
        if ( x != z ) {
            System.out.println( "x = " + x + ", z=" + z + ", scale = " + scale );
        }
        else {
            main( args );
        }
    }
}