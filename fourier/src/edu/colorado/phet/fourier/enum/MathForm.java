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
 * MathForm encapsulates the valid values for "math form".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MathForm {

    /* This class is not intended for instantiation. */
    private MathForm() {}
    
    // Math Form values
    public static final int WAVE_NUMBER = 0;
    public static final int WAVELENGTH = 1;
    public static final int MODE = 2;
    public static final int ANGULAR_FREQUENCY = 3;
    public static final int FREQUENCY = 4;
    public static final int PERIOD = 5;
    public static final int WAVE_NUMBER_AND_ANGULAR_FREQUENCY = 6;
    public static final int WAVELENGTH_AND_PERIOD = 7;
    
    /**
     * Determines if a math form value is valid.
     * 
     * @param mathForm
     * @return true or false
     */
    public static boolean isValid( int mathForm ) {
        boolean isValid = false;
        switch ( mathForm ) {
            case WAVE_NUMBER:
            case WAVELENGTH:
            case MODE:
            case ANGULAR_FREQUENCY:
            case FREQUENCY:
            case PERIOD:
            case WAVE_NUMBER_AND_ANGULAR_FREQUENCY:
            case WAVELENGTH_AND_PERIOD:
                 isValid = true;
                 break;
            default:
                 isValid = false;
        }
        return isValid;
    }  
}
