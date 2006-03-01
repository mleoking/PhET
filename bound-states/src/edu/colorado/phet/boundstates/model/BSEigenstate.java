/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.model;


/**
 * BSEigenstate is the model of an eigenstate.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class BSEigenstate implements Comparable {

    private double _energy;
    
    public BSEigenstate( double energy ) {
        _energy = energy;
    }
    
    public double getEnergy() {
        return _energy;
    }
    
    public static BSEigenstate[] createEigenstates( double[] energies ) {
        BSEigenstate[] eigenstates = new BSEigenstate[ energies.length ];
        for ( int i = 0; i < energies.length; i++ ) {
            eigenstates[i] = new BSEigenstate( energies[i] );
        }
        return eigenstates;
    }

    public int compareTo( Object o ) {
        int rval = 0;
        if ( o instanceof BSEigenstate ) {
            BSEigenstate eigenstate = (BSEigenstate) o;
            if ( equals( eigenstate ) ) {
                rval = 0;
            }
            else if ( _energy < eigenstate.getEnergy() ) {
                rval = -1;
            }
            else {
                rval = 1;
            }
        }
        else {
            throw new ClassCastException();
        }
        return rval;
    }
    
    public boolean equals( Object o ) {
        boolean b = false;
        if ( o instanceof BSEigenstate ) {
            if ( _energy == ((BSEigenstate)o).getEnergy() ) {
                b = true;
            }
        }
        return b;
    }
}
