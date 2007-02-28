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


/**
 * ArgUtils is a set of utilites for processing command line arguments.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ArgUtils {

    private ArgUtils() {}
    
    /**
     * Does the set of command line args contain a specific arg?
     * 
     * @param args
     * @param arg
     * @return true or false
     */
    public static final boolean contains( String[] args, String arg ) {
        boolean found = false;
        if ( args != null && arg != null ) {
            for ( int i = 0; i < args.length; i++ ) {
                if ( arg.equals( args[i] ) ) {
                    found = true;
                    break;
                }
            }
        }
        return found;
    }
}
