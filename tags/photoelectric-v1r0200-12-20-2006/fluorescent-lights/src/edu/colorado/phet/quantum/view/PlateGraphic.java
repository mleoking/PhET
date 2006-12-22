/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.quantum.view;

import edu.colorado.phet.common.view.phetgraphics.CompositePhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.dischargelamps.model.HeatingElement;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.IOException;

/**
 * PlateGraphic
 * <p>
 * A graphic for an anode or cathode plate
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PlateGraphic extends CompositePhetGraphic implements HeatingElement.ChangeListener {

    //-----------------------------------------------------------------
    // Class data
    //-----------------------------------------------------------------

    // An identity lookup table operation.
    private static short[] redLut = new short[256];
    private static short[] greenLut = new short[256];
    private static short[] blueLut = new short[256];
    private static short[] alphaLut = new short[256];
    private static short[][] lutArray = {redLut, greenLut, blueLut, alphaLut};
    private static LookupTable table = new ShortLookupTable( 0, lutArray );
    private LookupOp temperatureOp = new LookupOp( table, null );

    static {
        for( int i = 0; i < 256; i++ ) {
            redLut[i] = (short)i;
            greenLut[i] = (short)i;
            blueLut[i] = (short)i;
            alphaLut[i] = (short)i;
        }
    }

    //----------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------

    private BufferedImage image;
    private PhetImageGraphic baseImageGraphic;
    private PhetImageGraphic thermalImageGraphic;
    private BufferedImage thermalImage;

    //-----------------------------------------------------------------
    // Constructors and instance methods
    //-----------------------------------------------------------------

    public PlateGraphic( Component component, double plateLength ) {
        super( component );

        try {
            image = ImageLoader.loadBufferedImage( "images/electrode-2.png" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
        double scaleX = 1;
        double scaleY = plateLength / image.getHeight();
        AffineTransformOp scaleOp = new AffineTransformOp( AffineTransform.getScaleInstance( scaleX, scaleY ),
                                                           AffineTransformOp.TYPE_BILINEAR );
        image = ( scaleOp.filter( image, null ) );

        baseImageGraphic = new PhetImageGraphic( component, image );
        // The thermal image starts out as the base image. It will change as soon
        // as the temerature of the plate changes
        thermalImage = image;
        thermalImageGraphic = new PhetImageGraphic( component, thermalImage );

        addGraphic( baseImageGraphic );
        addGraphic( thermalImageGraphic );
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
        double temperature = heatingElement.getIsEnabled() ? heatingElement.getTemperature() : 0;

        // Compute the thermal image. Red is full-on, and the alpha is proportional
        // to the temperature.
        for( int i = 0; i < 256; i++ ) {
            alphaLut[i] = (short)temperature;
            redLut[i] = 255;
        }
        BufferedImage newImg = new BufferedImage( image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
//        BufferedImage newImg = new BufferedImage( image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB_PRE);
        temperatureOp.filter( image, newImg );

        // The LookupOp gave every pixel in the new image the same alpha. If there are any pixels in the
        // new image that are less transparent than their corresponding pixels in the original image,
        // reduce the alpha in the new image to that level.
        ColorModel cm = newImg.getColorModel();
        for( int x = 0; x < newImg.getWidth(); x++ ) {
            for( int y = 0; y < newImg.getHeight(); y++ ) {
                int rgbOrg = image.getRGB( x, y );
                int rgbNew = newImg.getRGB( x, y );
                short alpha = (short)Math.min( cm.getAlpha( rgbOrg ), cm.getAlpha( rgbNew ) );
                int newPixel = ( rgbNew & 0x00FFFFFF ) | ( alpha * 0x01000000 );
                newImg.setRGB( x, y, newPixel );
            }
        }

        thermalImageGraphic.setImage( newImg );
        setBoundsDirty();
        repaint();
    }

    //----------------------------------------------------------------
    // HeatingElement.ChangeListener implementation
    //----------------------------------------------------------------

    public void temperatureChanged( HeatingElement.ChangeEvent event ) {
        setFilteredImage( event.getHeatingElement() );
    }

    public void isEnabledChanged( HeatingElement.ChangeEvent event ) {
        setFilteredImage( event.getHeatingElement() );
    }
}