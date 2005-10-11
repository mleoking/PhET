/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.piccolo;

import edu.umd.cs.piccolo.nodes.PImage;
import edu.colorado.phet.common.view.util.ImageLoader;

import java.awt.image.BufferedImage;
import java.io.IOException;

/**
 * PImageFactory
 * <p>
 * Creates PImage instances given names for the images files specified as resources
 * in the classpath
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PImageFactory {

    public static PImage create( String imageName ) {
        PImage pImage = null;
        try {
            BufferedImage bImg = ImageLoader.loadBufferedImage( imageName );
            pImage = new PImage( bImg );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return pImage;
    }
}
