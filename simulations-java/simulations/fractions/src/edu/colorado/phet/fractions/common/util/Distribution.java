// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractions.common.util;

import fj.data.List;

import java.util.HashMap;
import java.util.Random;

/**
 * A distribution which maps T=>Probability (as double) and can be used to sample from the distribution accordly
 *
 * @author Sam Reid
 */
public class Distribution<T> {
    private final HashMap<T, Double> map = new HashMap<T, Double>();
    private static final Random random = new Random();

    public Distribution() {
    }

    public Distribution( final List<T> list ) {
        for ( T t : list ) {
            map.put( t, 1.0 );
        }
    }

    public void put( T t, double value ) { map.put( t, value );}

    public T draw() {
        Distribution<T> normal = normalize();
        double r = random.nextDouble();
        double runningTotal = 0.0;
        for ( T t : normal.map.keySet() ) {
            runningTotal += normal.map.get( t );
            if ( r <= runningTotal ) {
                return t;
            }
        }
        throw new RuntimeException( "No element found, r = " + r );
    }

    public T drawAndRemove() {
        T value = draw();
        map.remove( value );
        return value;
    }

    public Distribution<T> normalize() {
        final double sum = getSum();
        final Distribution<T> distribution = new Distribution<T>();
        for ( T t : map.keySet() ) {
            distribution.put( t, map.get( t ) / sum );
        }
        return distribution;
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