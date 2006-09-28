/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.test;

import java.io.Serializable;
import java.util.*;


/**
 * Distribution is a collection of objects with scalar weights.
 *
 * @author Sam Reid
 */
public class TestDistribution {

    public static void main( String[] args ) throws UnnormalizableDistributionException {
        TestDistribution d = new TestDistribution();
        d.test();
    }

    public final void test() throws UnnormalizableDistributionException {
        Distribution originalDistribution = new Distribution();
        originalDistribution.add( "a", 10 );
        originalDistribution.add( "b", 20 );
        DistributionAccessor accessor = new DistributionAccessor( originalDistribution, new Random() );
        Distribution newDistribution = new Distribution();
        for ( int i = 0; i < 1230; i++ ) {
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
    
    public class Distribution implements Serializable, Cloneable {

        Hashtable t = new Hashtable();

        public Distribution() {}
        
        public String toString() {
            return t.toString();
        }

        public void add( Object obj, double a ) {
            if ( a < 0 ) {
                throw new RuntimeException( "Negative values not supported in a distribution." );
            }
            double value = a;
            if ( t.containsKey( obj ) ) {
                value = getAmount( obj ) + a;
            }
            setAmount( obj, value );
        }

        public void setAmount( Object key, double a ) {
            t.put( key, new Double( a ) );
        }

        public double getAmount( Object key ) {
            if ( !t.containsKey( key ) ) {
                return 0;
            }
            else {
                Object value = t.get( key );
                Double d = (Double) value;
                return d.doubleValue();
            }
        }

        public int numEntries() {
            return t.size();
        }

        public double totalWeight() {
            double sum = 0;
            for ( Iterator it = t.keySet().iterator(); it.hasNext(); ) {
                Object next = it.next();
                sum += getAmount( next );
            }
            return sum;
        }

        public void normalize() throws UnnormalizableDistributionException {
            if ( numEntries() == 0 ) {
                throw new UnnormalizableDistributionException( "An empty distribution cannot be normalized." );
            }
            double sum = totalWeight();
            if ( sum <= 0 ) {
                throw new UnnormalizableDistributionException( "Illegal total weight: " + sum );
            }
            for ( Iterator it = t.keySet().iterator(); it.hasNext(); ) {
                Object key = it.next();
                double value = getAmount( key );
                setAmount( key, value / sum );
            }
        }

        public Object clone() {
            try {
                Distribution clone = (Distribution) super.clone();
                clone.t = (Hashtable) t.clone();
                return clone;
            }
            catch ( CloneNotSupportedException e ) {
                e.printStackTrace();
                throw new RuntimeException( e );
            }
        }

        public Distribution toNormalizedDistribution() throws UnnormalizableDistributionException {
            Distribution d = (Distribution) clone();
            d.normalize();
            return d;
        }

        public Object[] getEntries() {
            return t.keySet().toArray();
        }

        public Object[] getEntries( Object[] a ) {
            return t.keySet().toArray( a );
        }

        public Object[] getSortedKeys() {
            Object[] e = getEntries();
            Arrays.sort( e, new DistributionComparator() );
            return e;
        }

        public Object[] getSortedKeys( Object[] k ) {
            Object[] e = getEntries( k );
            Arrays.sort( e, new DistributionComparator() );
            return e;
        }

        public void add( Distribution a ) {
            Object[] entries = a.getEntries();
            for ( int i = 0; i < entries.length; i++ ) {
                Object entry = entries[i];
                add( entry, a.getAmount( entry ) );
            }
        }

        private class DistributionComparator implements Comparator {

            public int compare( Object o1, Object o2 ) {
                double a = Distribution.this.getAmount( o1 );
                double b = Distribution.this.getAmount( o2 );
                return Double.compare( a, b );
            }
        }
    }

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
            if ( d.totalWeight() <= 0 ) {
                throw new RuntimeException( "Cannot access elements in an empty distribution." );
            }
            double value = r.nextDouble() * d.totalWeight();
            double at = 0;
            Object[] keys = d.getEntries();
            for ( int i = 0; i < keys.length; i++ ) {
                double amount = d.getAmount( keys[i] );
                at += amount;
                if ( value <= at ) {
                    return keys[i];
                }
            }
            throw new RuntimeException( "Value not found in distribution: value=" + value + ", at=" + at );
        }
    }

    class UnnormalizableDistributionException extends Exception {

        public UnnormalizableDistributionException( String s ) {
            super( s );
        }
    }
}
