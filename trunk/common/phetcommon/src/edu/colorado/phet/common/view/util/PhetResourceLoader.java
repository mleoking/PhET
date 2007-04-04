/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.view.util;

import java.awt.image.BufferedImage;
import java.io.InputStream;

public interface PhetResourceLoader {
    InputStream getResourceAsStream( String resource );

    byte[] getResource( String resource );

    BufferedImage getImage( String resource );
}
