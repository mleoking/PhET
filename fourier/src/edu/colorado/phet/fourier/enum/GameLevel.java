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
 * GameLevel is a typesafe enumueration of "game level" values.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class GameLevel extends FourierEnum {

    /* This class is not intended for instantiation. */
    private GameLevel( String name ) {
        super( name );
    }

    // Game levels
    public static final GameLevel UNDEFINED = new GameLevel( "undefined" );
    public static final GameLevel PRESET = new GameLevel( "preset" );
    public static final GameLevel EASY   = new GameLevel( "easy" );
    public static final GameLevel MEDIUM = new GameLevel( "medium" );
    public static final GameLevel HARD   = new GameLevel( "hard" );
}