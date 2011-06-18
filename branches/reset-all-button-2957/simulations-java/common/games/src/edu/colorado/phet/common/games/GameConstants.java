//  Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.games;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * Constants related to the games project
 */
public class GameConstants {
    /*---------------------------------------------------------------------------*
    * images
    *----------------------------------------------------------------------------*/
    public static final BufferedImage SOUND_ICON = GamesResources.getImage( "sound-icon.png" );
    public static final BufferedImage STOPWATCH_ICON = GamesResources.getImage( "blue-stopwatch.png" );

    /*---------------------------------------------------------------------------*
    * strings
    *----------------------------------------------------------------------------*/
    public static final String TITLE_GAME_SETTINGS = PhetCommonResources.getString( "Games.title.gameSettings" );
    public static final String LABEL_LEVEL_CONTROL = PhetCommonResources.getString( "Games.label.levelControl" );
    public static final String RADIO_BUTTON_ON = PhetCommonResources.getString( "Games.radioButton.on" );
    public static final String RADIO_BUTTON_OFF = PhetCommonResources.getString( "Games.radioButton.off" );
    public static final String BUTTON_START = PhetCommonResources.getString( "Games.button.start" );
}
