// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.resources;

import java.awt.image.BufferedImage;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.audio.PhetAudioClip;
import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;

/**
 * DefaultResourceLoader is the default loader for JAR resources.
 */
/* package private */
public class DefaultResourceLoader extends AbstractResourceLoader {

    private static final BufferedImage NULL_IMAGE = new BufferedImage( 1, 1, BufferedImage.TYPE_INT_RGB );

    /**
     * Gets the image having the specified resource name.
     *
     * @param resourceName
     * @return BufferedImage
     */
    public BufferedImage getImage( String resource ) {
        if ( resource == null || resource.length() == 0 ) {
            throw new IllegalArgumentException( "null or zero-length resource name" );
        }
        BufferedImage image = null;
        try {
            image = ImageLoader.loadBufferedImage( resource );
        }
        catch ( IOException e ) {
            e.printStackTrace();
            image = NULL_IMAGE;
        }
        return image;
    }

    /**
     * Gets the audio clip having the specified resource name.
     *
     * @param resourceName
     * @return PhetAudioClip
     */
    public PhetAudioClip getAudioClip( String resource ) {
        if ( resource == null || resource.length() == 0 ) {
            throw new IllegalArgumentException( "null or zero-length resource name" );
        }
        return new PhetAudioClip( resource );
    }
}
