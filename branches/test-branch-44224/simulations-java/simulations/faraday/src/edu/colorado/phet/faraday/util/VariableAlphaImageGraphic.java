/* Copyright 2008, University of Colorado */

package edu.colorado.phet.faraday.util;

import java.awt.AlphaComposite;
import java.awt.Component;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;

/**
 * VariableAlphaImageGraphic is an image graphic whose alpha can be varied.
 * Note that this will not make an image opaque if the image has alpha in it;
 * it will simply modulate the existing alpha in the image.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class VariableAlphaImageGraphic extends PhetImageGraphic {

    private AlphaComposite _composite;

    /**
     * Constructs an opaque image.
     * 
     * @param component
     * @param image
     */
    public VariableAlphaImageGraphic( Component component, BufferedImage image ) {
        this( component, image, 1f );
    }
    
    /**
     * Constructs an image with specified alpha.
     * 
     * @param component
     * @param image
     * @param alpha 0 (totally transparent) to 1 (image's full alpha values)
     */
    public VariableAlphaImageGraphic( Component component, BufferedImage image, float alpha ) {
        super( component, image );
        _composite = createComposite( 1f );;
    }

    /**
     * Sets the alpha.
     * 
     * @param alpha 0 (totally transparent) to 1 (image's full alpha values)
     */
    public void setAlpha( float alpha ) {
        if ( alpha != _composite.getAlpha() ) {
            _composite = createComposite( alpha );
            setBoundsDirty();
            autorepaint();
        }
    }
    
    /**
     * Gets the alpha.
     * 
     * @return 0 (totally transparent) to 1 (image's full alpha values)
     */
    public float getAlpha() {
        return _composite.getAlpha();
    }

    /**
     * Paints the image with alpha using a compositing operator.
     * 
     * @param g2
     */
    public void paint( Graphics2D g2 ) {
        if ( isVisible() ) {
            Composite oldComposite = g2.getComposite(); // save
            g2.setComposite( _composite );
            super.paint( g2 );
            g2.setComposite( oldComposite ); // restore
        }
    }
    
    /*
     * Creates an alpha compositing operator.
     * 
     * @param alpha 0 (totally transparent) to 1 (image's full alpha values)
     */
    private AlphaComposite createComposite( float alpha ) {
        if ( alpha < 0f || alpha > 1f ) {
            throw new IllegalArgumentException( "0 <= alpha <= 1 is required: " + alpha );
        }
        return AlphaComposite.getInstance( AlphaComposite.SRC_OVER, alpha );
    }
}
