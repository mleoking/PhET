/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.quantum.util;

import java.util.Random;

/**
 * ProbabilisticChooser
 * <p>
 * An object that selects from a collection of objects based on probabilities.
 * <p>
 * The object is constructed with an array of ProbabilisticChooser.Entry objects, each
 * of which contains a reference to an arbitrary object and the likelihood that it should
 * be chosen. The likelihoods can be expressed as any sort of number. The chooser
 * normalizes the likelihoods. The likelihoods could, for example, be expressed as the
 * count of a particular object in a population.
 * <p>
 * When the chooser is asked to get() an object, an object from the collection of
 * entries will be returned with the likelihood expressed in its particular entry.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class ProbabilisticChooser {

    private static final Random RANDOM = new Random();

    private Entry[] _entries;


    /**
     *
     * @param entries   The entries can be in any order in the array
     */
    public ProbabilisticChooser( Entry[] entries ) {
        _entries = new Entry[ entries.length];

        // Get the normalization factor for the probabilities
        double pTotal = 0;
        for( int i = 0; i < entries.length; i++ ) {
            pTotal += entries[i].getP();
        }
        double fNorm = 1 / pTotal;

        // Build the internal list that is used for choosing. Each choose-able object
        // is put in an array, with an associated probability that is the sum of
        // its own probability plus the cummulative probability of all objects before
        // it in the list. This makes the choosing process work properly in get( double p );
        double p = 0;
        for( int i = 0; i < entries.length; i++ ) {
            p += entries[i].getP() * fNorm;
            _entries[i] = new Entry( entries[i].getObj() , p );
        }
    }

    /**
     * Choose an object from the entries
     * @return An object from the entries
     */
    public Object get() {
        return get( RANDOM.nextDouble());
    }

    /**
     * Get an object from the collection of entries, given a specified selector
     * probability. This method is provided mostly for testing
     *
     * @param p
     * @return An object from the entries
     */
    public Object get( double p ) {
        Object result = null;
        for( int i = 0; i < _entries.length && result == null; i++ ) {
            Entry entry = _entries[i];
            if( p <= entry.getP() ) {
                result = entry.getObj();
            }
        }
        return result;
    }

    /**
     * An entry for a ProbabilisticChooser
     */
    public static class Entry {
        private Object obj;
        private double p;

        /**
         *
         * @param obj   An object to be put in the chooser
         * @param p     The likelihood that the object should be chosen, or count of the object
         *              instances in the total population
         */
        public Entry( Object obj, double p ) {
            this.obj = obj;
            this.p = p;
        }

        Object getObj() {
            return obj;
        }

        double getP() {
            return p;
        }
    }
}
