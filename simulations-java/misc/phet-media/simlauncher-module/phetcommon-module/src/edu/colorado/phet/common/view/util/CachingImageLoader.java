/* Copyright 2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source: /cvsroot/phet/phetcommon/src/edu/colorado/phet/common/view/util/CachingImageLoader.java,v $
 * Branch : $Name:  $
 * Modified by : $Author: ronlemaster $
 * Revision : $Revision: 1.8 $
 * Date modified : $Date: 2006/01/03 23:37:18 $
 */
package edu.colorado.phet.common.view.util;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Hashtable;

/**
 * Decorates ImageLoader with buffering.
 *
 * @author ?
 * @version $Revision: 1.8 $
 */
public class CachingImageLoader extends ImageLoader {
    Hashtable buffer = new Hashtable();

    public BufferedImage loadImage( String image ) throws IOException {
        if( buffer.containsKey( image ) ) {
            return (BufferedImage)buffer.get( image );
        }
        else {
            BufferedImage imageLoad = super.loadImage( image );
            buffer.put( image, imageLoad );
            return imageLoad;
        }
    }
}
