/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.piccolophet.util;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * PImageFactory
 * <p/>
 * Creates PImage instances given names for the images files specified as resources
 * in the classpath.
 *
 * @author Ron LeMaster
 * @version $Revision$
 * @deprecated this class should not be used, the imageName args require knowledge of where the image resources live in the JAR file, use PhetResources instead
 */
public class PImageFactory {

    /**
     * Creates a PImage given a path to an image file in the classpath
     *
     * @param imageName
     * @return
     */
    public static PImage create( String imageName ) {
        return new PImage( loadImage( imageName ) );
    }

    /**
     * Creates a PImage given a path to an image file in the classpath. The image
     * is scaled to a specified size.
     *
     * @param imageName
     * @param size
     * @return a buffered image
     */
    public static PImage create( String imageName, Dimension size ) {
        BufferedImage image = loadImage( imageName );
        double scaleX = size.getWidth() / image.getWidth();
        double scaleY = size.getHeight() / image.getHeight();
        AffineTransform atx = AffineTransform.getScaleInstance( scaleX, scaleY );
        BufferedImageOp op = new AffineTransformOp( atx, AffineTransformOp.TYPE_BILINEAR );
        image = op.filter( image, null );
        return new PImage( image );
    }

    private static BufferedImage loadImage( String imageName ) {
        BufferedImage bImg = null;
        try {
            bImg = ImageLoader.loadBufferedImage( imageName );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        return bImg;
    }
}
