package edu.colorado.phet.fractionsintro.buildafraction.model;

import java.util.HashMap;
import java.util.Random;

/**
 * @author Sam Reid
 */
public class Distribution<T> {
    private final HashMap<T, Double> map = new HashMap<T, Double>();
    private static final Random random = new Random();

    public void put( T t, double value ) { map.put( t, value );}

    public T draw() {
        Distribution<T> normal = normalize();
        double r = random.nextDouble();
        double runningTotal = 0.0;
        for ( T t : map.keySet() ) {
            runningTotal += map.get( t );
            if ( r <= runningTotal ) {
                return t;
            }
        }
        throw new RuntimeException( "No element found, r = " + r );
    }

    public Distribution<T> normalize() {
        final double finalSum = getSum();
        return new Distribution<T>() {{
            for ( T t : map.keySet() ) {
                map.put( t, map.get( t ) / finalSum );
            }
        }};
    }

    private double getSum() {
        double sum = 0.0;
        for ( Double v : map.values() ) {
            sum += v;
        }
        return sum;
    }

    public static void main( String[] args ) {
        final Distribution<String> distribution = new Distribution<String>() {{
            put( "a", 60 );
            put( "b", 20 );
            put( "c", 20 );
        }};

        HashMap<String, Integer> counts = new HashMap<String, Integer>();
        for ( int i = 0; i < 10000; i++ ) {
            final String x = distribution.draw();
//            System.out.println( "distribution.draw() = " + x );
            if ( counts.containsKey( x ) ) {
                counts.put( x, counts.get( x ) + 1 );
            }
            else {
                counts.put( x, 1 );
            }
        }
        System.out.println( "counts = " + counts );

    }
}