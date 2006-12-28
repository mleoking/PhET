/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.util;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.view.util.VisibleColor;

/**
 * ImageFactory creates common Images programmatically.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class ImageFactory {

    /* Not intended for instantiation */
    private ImageFactory() {}
    
    /**
     * Creates a spectrum image.
     * 
     * @param width desired image width
     * @param height desired image height
     * @param minWavelength min wavelength, typically VisibleColor.MIN_WAVELENGTH
     * @param maxWavelength max wavelength, typically VisibleColor.MAX_WAVELENGTH
     * @return Image
     */
    public static Image createSpectrum( int width, int height, double minWavelength, double maxWavelength ) {
        BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        Graphics2D g2 = image.createGraphics();
        Function linearFunction = new Function.LinearFunction( 0, image.getWidth(), minWavelength, maxWavelength );
        for ( int i = 0; i < image.getWidth(); i++ ) {
            double wavelength = linearFunction.evaluate( i );
            VisibleColor visibleColor = new VisibleColor( wavelength );
            g2.setColor( visibleColor );
            g2.fillRect( i, 0, 1, image.getHeight() ); // x,y,width,height
        }
        g2.dispose();
        return image;
    }
}
