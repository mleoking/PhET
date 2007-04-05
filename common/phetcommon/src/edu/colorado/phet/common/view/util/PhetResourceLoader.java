/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.view.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public interface PhetResourceLoader {
    InputStream getResourceAsStream( String resource ) throws IOException;

    byte[] getResource( String resource ) throws IOException;

    BufferedImage getImage( String resource );

    PhetAudioClip getAudioClip( String resource );
}
