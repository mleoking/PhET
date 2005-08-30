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

import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.MakeDuotoneImageOp;
import edu.colorado.phet.common.view.util.BufferedImageUtils;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.model.HeatingElement;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.io.IOException;

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

        try {
            baseImage = ImageLoader.loadBufferedImage( DischargeLampsConfig.HEATING_ELEMENT_FILE_NAME );
            if( isRightFacing ) {
                baseImage = BufferedImageUtils.flipX( baseImage );
            }
            setImage( baseImage );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    //----------------------------------------------------------------
    // Implementation of listener interfaces
    //----------------------------------------------------------------
    public void temperatureChanged( HeatingElement.ChangeEvent event ) {

        double temperature = event.getHeatingElement().getTemperature();
        BufferedImage newImg = baseImage;
        ColorModel cm = newImg.getColorModel();
        for( int x = 0; x < newImg.getWidth(); x++ ) {
            for( int y = 0; y < newImg.getHeight(); y++ ) {
                int rgb = newImg.getRGB( x, y );
                int alpha = cm.getAlpha( rgb );
                int red = Math.min( (int)temperature, 255 );
                int green = 0;
                int blue = 0;
                int newRGB = alpha * 0x01000000 + red * 0x00010000 + green * 0x000000100 + blue * 0x00000001;
                newImg.setRGB( x, y, newRGB );
            }
        }
        setImage( newImg );
    }

    public static int getDuoToneRGB( int red, int green, int blue, int alpha, Color baseColor ) {
        double gray = ( red + green + blue ) / ( 3 );
        int newRed = (int)( (double)gray / 255 * (double)baseColor.getRed() );
        int newGreen = (int)( (double)gray / 255 * (double)baseColor.getRed() );
        int newBlue = (int)( (double)gray / 255 * (double)baseColor.getRed() );
        int newRGB = alpha * 0x01000000 + newRed * 0x00010000 + newGreen * 0x000000100 + newBlue * 0x00000001;
        return newRGB;
    }
}
