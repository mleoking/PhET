/* Copyright 2010, University of Colorado */

package edu.colorado.phet.acidbasesolutions.view;

import java.awt.Image;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * An image node whose alpha channel can be varied.
 * Note that this will not make an image opaque if the image has alpha in it;
 * it will simply modulate the existing alpha in the image.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VariableAlphaImageNode extends PImage {

    private BufferedImage bufferedImage;
    private float alpha;
    
    /**
     * Constructs an opaque image.
     * 
     * @param component
     * @param image
     */
    public VariableAlphaImageNode( BufferedImage image ) {
        this( image, 1f );
    }
    
    /**
     * Constructs an image with specified alpha.
     * 
     * @param component
     * @param image
     * @param alpha 0 (totally transparent) to 1 (image's full alpha values)
     */
    public VariableAlphaImageNode( BufferedImage image, float alpha ) {
        super( BufferedImageUtils.scaleAlpha( image, alpha ) );
        this.bufferedImage = image;
    }
    
    @Override
    public void setImage( Image image ) {
        if ( image != this.bufferedImage ) {
            this.bufferedImage = BufferedImageUtils.toBufferedImage( image );
            super.setImage( BufferedImageUtils.scaleAlpha( this.bufferedImage, alpha ) );
        }
    }

    /**
     * Sets the alpha.
     * 
     * @param alpha 0 (totally transparent) to 1 (image's full alpha values)
     */
    public void setAlpha( float alpha ) {
        if ( alpha != this.alpha ) {
            this.alpha = alpha;
            setImage( BufferedImageUtils.scaleAlpha( bufferedImage, alpha ) );
        }
    }
    
    /**
     * Gets the alpha.
     * 
     * @return 0 (totally transparent) to 1 (image's full alpha values)
     */
    public float getAlpha() {
        return alpha;
    }
}
