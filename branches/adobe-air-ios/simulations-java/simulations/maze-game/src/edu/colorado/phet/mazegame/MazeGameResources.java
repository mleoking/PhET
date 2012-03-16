// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.mazegame;

import java.awt.*;

import edu.colorado.phet.common.phetcommon.audio.PhetAudioClip;
import edu.colorado.phet.common.phetcommon.resources.PhetResources;

public class MazeGameResources {
    public static final PhetResources INSTANCE = new PhetResources( "maze-game" );

    public static String getString( String str ) {
        return INSTANCE.getLocalizedString( str );
    }

    public static PhetAudioClip getAudioClip( String s ) {
        return INSTANCE.getAudioClip( s );
    }

    public static Image loadBufferedImage( String s ) {
        return INSTANCE.getImage( s );
    }
}
