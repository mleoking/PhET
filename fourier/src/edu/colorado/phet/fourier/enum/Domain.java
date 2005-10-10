/* Copyright 2005, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.enum;


/**
 * Domain encapsulates the valid values for "domain".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class Domain {

    /* This class is not intended for instantiation. */
    private Domain() {}
    
    // Domain values
    public static final int SPACE = 0;
    public static final int TIME = 1;
    public static final int SPACE_AND_TIME = 2;
    
    /**
     * Determines if a domain value is valid.
     * 
     * @param domain
     * @return true or false
     */
    public static boolean isValid( int domain ) {
        boolean isValid = false;
        switch ( domain ) {
            case SPACE:
            case TIME:
            case SPACE_AND_TIME:
                 isValid = true;
                 break;
            default:
                 isValid = false;
        }
        return isValid;
    }
}