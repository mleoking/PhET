/* Copyright 2007, University of Colorado */
package edu.colorado.phet.common.view.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class DefaultResourceLoader extends AbstractPhetResourceLoader {
    private static final BufferedImage NULL_IMAGE = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_RGB );
    
    private static final InputStream NULL_STREAM = new InputStream() {
        public int read() throws IOException {
            return -1;
        }
    };

    public InputStream getResourceAsStream( String resource ) {
        InputStream stream = getClass().getResourceAsStream( resource );

        if (stream == null) {
            return NULL_STREAM;
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
}
