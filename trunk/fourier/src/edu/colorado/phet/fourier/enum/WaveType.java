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
 * WaveType encapsulates the valid values for "wave type".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class WaveType {

    /* This class is not intended for instantiation. */
    private WaveType() {}
    
    // Wave Type values
    public static final int SINES = 0;
    public static final int COSINES = 1;
    
    /**
     * Determines if a wave type value is valid.
     * 
     * @param waveType
     * @return true or false
     */
    public static boolean isValid( int waveType ) {
        boolean isValid = false;
        switch ( waveType ) {
            case SINES:
            case COSINES:
                 isValid = true;
                 break;
            default:
                 isValid = false;
        }
        return isValid;
    }
}