// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.fourier.enums;


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
    public static final GameLevel LEVEL1    = new GameLevel( "1" );
    public static final GameLevel LEVEL2    = new GameLevel( "2" );
    public static final GameLevel LEVEL3    = new GameLevel( "3" );
    public static final GameLevel LEVEL4    = new GameLevel( "4" );
    public static final GameLevel LEVEL5    = new GameLevel( "5" );
    public static final GameLevel LEVEL6    = new GameLevel( "6" );
    public static final GameLevel LEVEL7    = new GameLevel( "7" );
    public static final GameLevel LEVEL8    = new GameLevel( "8" );
    public static final GameLevel LEVEL9    = new GameLevel( "9" );
    public static final GameLevel LEVEL10   = new GameLevel( "10" );
    public static final GameLevel PRESET    = new GameLevel( "preset" );
    
    private static final GameLevel[] LEVELS = {
            UNDEFINED,
            LEVEL1, LEVEL2, LEVEL3, LEVEL4, LEVEL5,
            LEVEL6, LEVEL7, LEVEL8, LEVEL9, LEVEL10,
            PRESET
    };
        
    /**
     * Retrieves a game level by name.
     * This is used primarily in XML encoding.
     * 
     * @param name
     * @return
     */
    public static GameLevel getByName( String name ) {
        GameLevel gameLevel = UNDEFINED;
        for  (int i = 0; i < LEVELS.length; i++ ) {
            if ( LEVELS[i].getName().equals( name ) ) {
                gameLevel = LEVELS[i];
                break;
            }
        }
        return gameLevel;
    }
}