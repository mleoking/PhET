/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.dischargelamps.view;

import java.awt.Component;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.io.IOException;

import edu.colorado.phet.common.phetcommon.view.util.BufferedImageUtils;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.DischargeLampsResources;
import edu.colorado.phet.dischargelamps.model.HeatingElement;

/**
 * HeatElementGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class HeatingElementGraphic extends PhetImageGraphic implements HeatingElement.ChangeListener {
    private BufferedImage baseImage;

    public HeatingElementGraphic( Component component, boolean isRightFacing ) {
        super( component );

        baseImage = DischargeLampsResources.getImage( DischargeLampsConfig.HEATING_ELEMENT_FILE_NAME );
        baseImage = BufferedImageUtils.flipY( baseImage );
        if ( isRightFacing ) {
            baseImage = BufferedImageUtils.flipX( baseImage );
        }
        setImage( baseImage );
    }

    //-----------------------------------------------------------------
    // Image filtering
    //-----------------------------------------------------------------

    /**
     * Makes the image change from black to red, depending on the temperature of the heating element
     *
     * @param heatingElement
     */
    private void setFilteredImage( HeatingElement heatingElement ) {
        double temperature = heatingElement.getTemperature();
//        double temperature = heatingElement.getIsEnabled() ? heatingElement.getTemperature() : 0;
        BufferedImage newImg = baseImage;
        ColorModel cm = newImg.getColorModel();
        for ( int x = 0; x < newImg.getWidth(); x++ ) {
            for ( int y = 0; y < newImg.getHeight(); y++ ) {
                int rgb = newImg.getRGB( x, y );
                int alpha = cm.getAlpha( rgb );
                int red = Math.min( (int) temperature, 255 );
                int green = 0;
                int blue = 0;
                int newRGB = alpha * 0x01000000 + red * 0x00010000 + green * 0x000000100 + blue * 0x00000001;
                newImg.setRGB( x, y, newRGB );
            }
        }

        // blur the coil as it gets hotter
        float e = ( 1f / 32f ) * (float) ( temperature / 255f );
        float f = ( 1f / 9f ) * (float) ( temperature / 255f );
        float g = 1 - f - e;
        float[] blurCoeffs = new float[]{
                e, e, e, e, e,
                e, f, f, f, e,
                e, f, g, f, e,
                e, f, f, f, e,
                e, e, e, e, e,
        };
        Kernel blurKernel = new Kernel( 5, 5, blurCoeffs );
        ConvolveOp blurOp = new ConvolveOp( blurKernel, ConvolveOp.EDGE_NO_OP, new RenderingHints( RenderingHints.KEY_ANTIALIASING,
                                                                                                   RenderingHints.VALUE_ANTIALIAS_ON ) );
        newImg = blurOp.filter( newImg, null );

        setImage( newImg );
        setBoundsDirty();
        repaint();
    }

    //----------------------------------------------------------------
    // Implementation of listener interfaces
    //----------------------------------------------------------------
    public void temperatureChanged( HeatingElement.ChangeEvent event ) {
        setFilteredImage( event.getHeatingElement() );
    }

    public void isEnabledChanged( HeatingElement.ChangeEvent event ) {
        setFilteredImage( event.getHeatingElement() );
    }
}
