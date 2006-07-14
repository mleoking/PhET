/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.boundstates.debug;

import java.text.DecimalFormat;

import edu.colorado.phet.boundstates.model.BSEigenstate;

/**
 * DebugOutput is a collection of methods related to debugging output.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class DebugOutput {

    /* Not intended for instantiation */
    private DebugOutput() {}
    
    /**
     * Prints eigenstate energies to System.out.
     * 
     * @param eigenstates
     */
    public static void printEigenstates( BSEigenstate[] eigenstates ) {
        System.out.print( "eigenstates=" );
        if ( eigenstates == null ) {
            System.out.print( "null" );
        }
        else {
            DecimalFormat format = new DecimalFormat( "0.00" );
            for ( int i = 0; i < eigenstates.length; i++ ) {
                System.out.print( format.format( eigenstates[i].getEnergy() ) );
                if ( i < eigenstates.length - 1 ) {
                    System.out.print( ", " );
                }
            }
        }
        System.out.println();
    } 
}
