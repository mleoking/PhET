/**
 * Class: ThermometerView
 * Package: edu.colorado.phet.microwave
 * Author: Another Guy
 * Date: Sep 30, 2003
 */
package edu.colorado.phet.microwaves;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.io.IOException;
import java.util.Observable;
import java.util.Observer;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common_microwaves.view.graphics.Graphic;
import edu.colorado.phet.coreadditions_microwaves.graphics.ImageGraphic;

public class ThermometerView implements Graphic, ImageObserver, Observer {

    private BufferedImage thermometerBody;
    private BufferedImage thermometerBackground;
    private double temperature;
    private Point2D.Double modelLocation = new Point2D.Double( 20, 50 );
    private Point2D.Double viewLocation = modelLocation;
    private static final int keArraySize = 20;
    private double[] keArray = new double[keArraySize];
    private ImageGraphic backgroundGraphic;
    private ImageGraphic bodyGraphic;

    public ThermometerView( Thermometer thermometer ) {
        super();
        thermometer.addObserver( this );
        try {
            thermometerBody = ImageLoader.loadBufferedImage( "microwaves/images/thermometer.png" );
            bodyGraphic = new ImageGraphic( thermometerBody, modelLocation );
            thermometerBackground = ImageLoader.loadBufferedImage( "microwaves/images/thermometer-background.png" );
            backgroundGraphic = new ImageGraphic( thermometerBackground, modelLocation );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }
    }

    public boolean imageUpdate( Image img, int infoflags,
                                int x, int y, int width, int height ) {
        return false;
    }

    public void paint( Graphics2D graphics2D ) {

        // Draw the background for the thermometer
        graphics2D.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1 ) );
        backgroundGraphic.paint( graphics2D );

        // Draw the variable part of the thermometer: The red rectangle
        graphics2D.setColor( Color.RED );
        double tempHeight = Math.min( temperature, thermometerBackground.getHeight() - 100 ); // 15 is the radius of the top of the thermometer
        Rectangle2D.Double tempRect = new Rectangle2D.Double( viewLocation.x + 8,
                                                              viewLocation.y + 50,
                                                              22, tempHeight );
        graphics2D.fill( tempRect );

        // Draw the body of the thermometer
        bodyGraphic.paint( graphics2D );
    }

    public void update( Observable o, Object arg ) {
        if ( o instanceof Thermometer ) {
            double totalKe = 0;
            for ( int i = keArray.length - 2; i >= 0; i-- ) {
                double t = keArray[i];
                totalKe += keArray[i];
                keArray[i + 1] = keArray[i];
            }

            // 5 is a scaling factor
            keArray[0] = ( (Double) arg ).doubleValue() * 5;
            totalKe += keArray[0];
            temperature = totalKe / keArraySize;
        }
        viewLocation = modelLocation;
    }
}
