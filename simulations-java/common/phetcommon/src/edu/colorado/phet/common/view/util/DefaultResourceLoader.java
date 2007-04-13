/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.view.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class DefaultResourceLoader extends AbstractPhetResourceLoader {
    private static final BufferedImage NULL_IMAGE = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_RGB );

    public InputStream getResourceAsStream( String resource ) throws IOException {
        InputStream stream = Thread.currentThread().getContextClassLoader().getResourceAsStream( resource );

        if (stream == null) {
            throw new IOException( "The specified resource " + resource + " is not valid." );
        }

        return stream;
    }

    public BufferedImage getImage( String resource ) {
        try {
            return ImageLoader.loadBufferedImage( resource );
        }
        catch( IOException e ) {
            e.printStackTrace();

            return NULL_IMAGE;
        }
    }

    public PhetAudioClip getAudioClip( String resource ) {
        return new PhetAudioClip( resource );
    }

}
