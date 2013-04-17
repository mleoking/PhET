// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.quantumwaveinterference.tests;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;

/**
 * A collection of objects with scalar weights.
 */
public class Distribution implements Serializable, Cloneable {
    Hashtable t = new Hashtable();

    public String toString() {
        return t.toString();
    }

    public void add( Object obj, double a ) {
        if( a < 0 ) {
            throw new RuntimeException( "Negative values not supported in a distribution." );
        }
        double value = a;
        if( t.containsKey( obj ) ) {
            value = getAmount( obj ) + a;
        }
        setAmount( obj, value );
    }

    public void setAmount( Object key, double a ) {
        t.put( key, new Double( a ) );
    }

    public double getAmount( Object key ) {
        if( !t.containsKey( key ) ) {
            return 0;
        }
        else {
            Object value = t.get( key );
            Double d = (Double)value;
            return d.doubleValue();
        }
    }

    public int numEntries() {
        return t.size();
    }

    public double totalWeight() {
        double sum = 0;
        for( Iterator it = t.keySet().iterator(); it.hasNext(); ) {
            Object next = it.next();
            sum += getAmount( next );
        }
        return sum;
    }

    public void normalize() throws UnnormalizableDistributionException {
        if( numEntries() == 0 ) {
            throw new UnnormalizableDistributionException( "An empty distribution cannot be normalized." );
        }
        double sum = totalWeight();
        if( sum <= 0 ) {
            throw new UnnormalizableDistributionException( "Illegal total weight: " + sum );
        }
        for( Iterator it = t.keySet().iterator(); it.hasNext(); ) {
            Object key = it.next();
            double value = getAmount( key );
            setAmount( key, value / sum );
        }
    }

    public Object clone() {
        try {
            Distribution clone = (Distribution)super.clone();
            clone.t = (Hashtable)t.clone();
            return clone;
        }
        catch( CloneNotSupportedException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    public Distribution toNormalizedDistribution() throws UnnormalizableDistributionException {
        Distribution d = (Distribution)clone();
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
        for( int i = 0; i < entries.length; i++ ) {
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
