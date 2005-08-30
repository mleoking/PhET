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
import edu.colorado.phet.dischargelamps.model.HeatingElement;

import java.awt.*;
import java.awt.image.*;
import java.io.IOException;

/**
 * PlateGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PlateGraphic extends PhetImageGraphic implements HeatingElement.ChangeListener {

    private static BufferedImage image;
    static {
        try {
            image = ImageLoader.loadBufferedImage( "images/electrode-2.png" );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    private static short[] redLut = new short[256];
    private static short[] greenLut = new short[256];
    private static short[] blueLut = new short[256];
    private static short[] alphaLut = new short[256];
    private static short[][] lutArray = {redLut, greenLut, blueLut, alphaLut };
    private static LookupTable table  = new ShortLookupTable( 0, lutArray );
    private LookupOp temperatureOp = new LookupOp( table, null );
    static {
        for( int i = 0; i < 256; i++ ){
            redLut[i] = (short)i;
            greenLut[i] = (short)i;
            blueLut[i] = (short)i;
            alphaLut[i] = (short)i;
        }
    }

    public PlateGraphic( Component component ) {
        super( component, image );
    }

    public void setImage( BufferedImage image ) {
        super.setImage( image );
        PlateGraphic.image = image;
    }

    //----------------------------------------------------------------
    // HeatingElement.ChangeListener implementation
    //----------------------------------------------------------------


    public void temperatureChanged( HeatingElement.ChangeEvent event ) {
        double temperature = event.getHeatingElement().getTemperature();
        for( int i = 0; i < 256; i++ ){
            redLut[i] = (short)temperature;
        }
        BufferedImage newImg = new BufferedImage( image.getWidth(), image.getHeight(), image.getType() );
        temperatureOp.filter(image, newImg );
        setImage( newImg );
    }
}