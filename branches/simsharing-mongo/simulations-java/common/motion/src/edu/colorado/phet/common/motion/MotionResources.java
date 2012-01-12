// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.motion;

import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.resources.PhetResources;

/**
 * Author: Sam Reid
 * May 23, 2007, 1:04:58 AM
 */
public class MotionResources {
    private static PhetResources INSTANCE = new PhetResources( "motion" );

    public static PhetResources getInstance() {
        return INSTANCE;
    }

    public static BufferedImage loadBufferedImage( String url ) throws IOException {
        return INSTANCE.getImage( url );
    }
}
