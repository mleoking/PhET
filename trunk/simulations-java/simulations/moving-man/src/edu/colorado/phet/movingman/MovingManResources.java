// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.movingman;

import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * @author Sam Reid
 */
public class MovingManResources {

    private static PhetResources INSTANCE = new PhetResources( "moving-man" );

    /* not intended for instantiation */

    private MovingManResources() {
    }

    public static PhetResources getInstance() {
        return INSTANCE;
    }

    public static BufferedImage loadBufferedImage( String url ) throws IOException {
        return INSTANCE.getImage( url );
    }

    public static String getString( String s ) {
        return INSTANCE.getLocalizedString( s );
    }

}