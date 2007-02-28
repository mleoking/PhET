/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.quantumtunneling.util;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Random;


/**
 * Distribution is a collection of objects with scalar weights.
 *
 * @author Sam Reid (revised by Chris Malley)
 * @version $Revision$
 */
public class Distribution implements Serializable, Cloneable {

    private Hashtable _hashTable = new Hashtable(); // table of objects and their weights

    /**
     * Sole constructor.
     *
     */
    public Distribution() {}
    
    /**
     * Adds an object to the distribution, and assigns it a weight.
     * If the object is already in the distribution, the weight is 
     * added to its current weight.
     * 
     * @param object
     * @param weight
     */
    public void add( Object object, final double weight ) {
        if ( weight < 0 ) {
            throw new RuntimeException( "Negative weights not supported in a distribution: " + weight );
        }
        double cumulativeWeight = weight;
        if ( _hashTable.containsKey( object ) ) {
            cumulativeWeight = getWeight( object ) + weight;
        }
        setWeight( object, cumulativeWeight );
    }

    /**
     * Gets the number of objects in the distribution.
     * 
     * @return
     */
    public int getNumObjects() {
        return _hashTable.size();
    }
    
    /*
     * Gets the objects that are in the distribution.
     * 
     * @return array of Object
     */
    private Object[] getObjects() {
        return _hashTable.keySet().toArray();
    }
    
    /*
     * Sets the weigth of an object.
     * 
     * @param object
     * @param weight
     */
    private void setWeight( Object object, final double weight ) {
        _hashTable.put( object, new Double( weight ) );
    }

    /*
     * Gets the weight of an object.
     * 
     * @param object
     * @return its weight, 0 if the object is not in the distribution
     */
    protected double getWeight( Object object ) {
        if ( !_hashTable.containsKey( object ) ) {
            return 0;
        }
        else {
            Object value = _hashTable.get( object );
            Double d = (Double) value;
            return d.doubleValue();
        }
    }

    /*
     * Gets the total weight of all objects in the distribution.
     * 
     * @return total weight
     */
    private double getTotalWeight() {
        double sum = 0;
        for ( Iterator it = _hashTable.keySet().iterator(); it.hasNext(); ) {
            Object next = it.next();
            sum += getWeight( next );
        }
        return sum;
    }

    /**
     * Normalizes the distribution, so that all weights are between 0 and 1.
     */
    public void normalize() {
        if ( getNumObjects() == 0 ) {
            throw new IllegalStateException( "An empty distribution cannot be normalized." );
        }
        final double sum = getTotalWeight();
        if ( sum <= 0 ) {
            throw new IllegalStateException( "Illegal total weight: " + sum );
        }
        for ( Iterator it = _hashTable.keySet().iterator(); it.hasNext(); ) {
            Object key = it.next();
            final double value = getWeight( key );
            setWeight( key, value / sum );
        }
    }
    
    /**
     * Gets a normalized distribution, without modifying this distribution.
     * 
     * @return a normalized Distribution object
     */
    public Distribution toNormalizedDistribution() {
        Distribution d = (Distribution) clone();
        d.normalize();
        return d;
    }
    
    /**
     * Clones this object.
     * 
     * @return a copy (clone)
     */
    public Object clone() {
        try {
            Distribution clone = (Distribution) super.clone();
            clone._hashTable = (Hashtable) _hashTable.clone();
            return clone;
        }
        catch ( CloneNotSupportedException e ) {
            e.printStackTrace();
            throw new RuntimeException( e );
        }
    }

    /**
     * Provides a string representation of the distibution.
     * 
     * @return String
     */
    public String toString() {
        return _hashTable.toString();
    }

    /**
     * DistributionAccessor randomly accesses objects in a distribution.
     */
    public static class DistributionAccessor {

        private Distribution _distribution;
        private Random _random;

        public DistributionAccessor( Distribution distribution ) {
            this( distribution, new Random() );
        }

        public DistributionAccessor( Distribution distribution, Random random ) {
            _distribution = distribution;
            _random = random;
        }

        /**
         * Randomly selects an object from the distribution.
         * 
         * @return an object, possibly null
         */
        public Object nextObject() {
            double totalWeight = _distribution.getTotalWeight();
            if ( totalWeight <= 0 ) {
                return null;
            }
            final double value = _random.nextDouble() * totalWeight;
            double at = 0;
            Object[] keys = _distribution.getObjects();
            for ( int i = 0; i < keys.length; i++ ) {
                final double weight = _distribution.getWeight( keys[i] );
                at += weight;
                if ( value <= at ) {
                    return keys[i];
                }
            }
            throw new RuntimeException( "Value not found in distribution: value=" + value + ", at=" + at );
        }
    }
    
    /**
     * Test harness.
     * 
     * @param args
     * @throws UnnormalizableDistributionException
     */
    public static void main( String[] args ) {
        
        // Created a weighted distribution, using random weights
        Distribution originalDistribution = new Distribution();
        originalDistribution.add( "a", (int) ( Math.random() * 100 ) );
        originalDistribution.add( "b", (int) ( Math.random() * 100 ) );
        
        // Use the weighted distribution to create a new distribution.
        Distribution newDistribution = new Distribution();
        DistributionAccessor accessor = new DistributionAccessor( originalDistribution );
        for ( int i = 0; i < 1230; i++ ) {
            Object object = accessor.nextObject();
            newDistribution.add( object, 1 );
        }
        
        // Compare the distributions; their normalized weights should be almost identical.
        System.out.println( "originalDistribution: " + originalDistribution );
        System.out.println( "newDistribution: " + newDistribution );
        System.out.println( "originalDistribution (normalized):" + originalDistribution.toNormalizedDistribution() );
        System.out.println( "newDistribution (normalized):" + newDistribution.toNormalizedDistribution() );
    }
}
