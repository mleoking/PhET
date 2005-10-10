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
 * GameLevel encapsulates the valid values for "game level".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GameLevel {

    /* This class is not intended for instantiation. */
    private GameLevel() {}
    
    // Game levels
    public static final int PRESET = 0;
    public static final int EASY   = 1;
    public static final int MEDIUM = 2;
    public static final int HARD   = 3;
    
    /**
     * Determines if a game level value is valid.
     * 
     * @param gameLevel
     * @return true or false
     */
    public static boolean isValid( int gameLevel ) {
        boolean isValid = false;
        switch ( gameLevel ) {
            case PRESET:
            case EASY:
            case MEDIUM:
            case HARD:
                isValid = true;
                break;
            default:
                isValid = false;
        }
        return isValid;
    }
}