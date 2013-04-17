// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.quantumwaveinterference.tests;

import java.util.Random;

/**
 * Author: Sam Reid
 * Apr 13, 2007, 2:41:10 AM
 */
class DistributionAccessor {
    Distribution d;
    Random r;

    public DistributionAccessor( Distribution d ) {
        this( d, new Random() );
    }

    public DistributionAccessor( Distribution d, Random r ) {
        this.d = d;
        this.r = r;
    }

    public Object get() {
        if( d.totalWeight() <= 0 ) {
            throw new RuntimeException( "Cannot access elements in an empty distribution." );
        }
        double value = r.nextDouble() * d.totalWeight();
        double at = 0;
        Object[] keys = d.getEntries();
        for( int i = 0; i < keys.length; i++ ) {
            double amount = d.getAmount( keys[i] );
            at += amount;
            if( value <= at ) {
                return keys[i];
            }
        }
        throw new RuntimeException( "Value not found in distribution: value=" + value + ", at=" + at );
    }

    public static void main( String[] args ) throws UnnormalizableDistributionException {
        Distribution originalDistribution = new Distribution();
        originalDistribution.add( "a", 10 );
        originalDistribution.add( "b", 20 );
        DistributionAccessor accessor = new DistributionAccessor( originalDistribution, new Random() );
        Distribution newDistribution = new Distribution();
        for( int i = 0; i < 1230; i++ ) {
            Object o = accessor.get();
            System.out.print( o );
            newDistribution.add( o, 1 );
        }
        System.out.println( "\nInitial distribution: " + originalDistribution );
        System.out.println( "Reloaded dist: " + newDistribution );
        System.out.println( "normalized-----" );
        System.out.println( "init=" + originalDistribution.toNormalizedDistribution() );
        System.out.println( "relo=" + newDistribution.toNormalizedDistribution() );
    }
}
