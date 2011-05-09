// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.games;

import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;


public class GamesResources {

    private static final PhetResources RESOURCES = new PhetResources( "games" );
    
    /* not intended for instantiation */
    private GamesResources() {}
    
    /**
     * Convenience method for accessing an image file from games.
     *
     * @param name the name of the image
     * @return BufferedImage
     */
    public static BufferedImage getImage( String name ) {
        return RESOURCES.getImage( name );
    }
}
