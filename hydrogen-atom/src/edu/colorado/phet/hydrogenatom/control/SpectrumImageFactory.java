/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.control;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.math.Function;
import edu.colorado.phet.common.view.util.VisibleColor;


public class SpectrumImageFactory {

    private SpectrumImageFactory() {}
    
    public static Image createImage( int width, int height, double minWavelength, double maxWavelength ) {
        BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        Graphics2D g2 = image.createGraphics();
        Function linearFunction = new Function.LinearFunction( 0, image.getWidth(), minWavelength, maxWavelength );
        for ( int i = 0; i < image.getWidth(); i++ ) {
            double wavelength = linearFunction.evaluate( i );
            VisibleColor visibleColor = new VisibleColor( wavelength );
            g2.setColor( visibleColor );
            g2.fillRect( i, 0, 1, image.getHeight() );
        }
        g2.dispose();
        return image;
    }
}
