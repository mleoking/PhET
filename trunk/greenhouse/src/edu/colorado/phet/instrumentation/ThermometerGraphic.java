/**
 * Class: ThermometerGraphic
 * Package: edu.colorado.phet.microwave
 * Author: Another Guy
 * Date: Sep 30, 2003
 */
package edu.colorado.phet.instrumentation;

import edu.colorado.phet.common.view.graphics.Graphic;
import edu.colorado.phet.common.view.util.graphics.ImageLoader;
import edu.colorado.phet.greenhouse.GreenhouseConfig;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Observable;
import java.util.Observer;

public class ThermometerGraphic implements Graphic, ImageObserver, Observer {


    private BufferedImage thermometerBody;
    private BufferedImage thermometerBackground;
    private double temperature;
    private Point2D.Double location = new Point2D.Double();
    private Thermometer thermometer;

    private NumberFormat formatter = new DecimalFormat( "0" );
    private Font temperatureFont = new Font( "sanserif", Font.BOLD, 14 );
    private BufferedImage thermometerBI;


    public ThermometerGraphic( Thermometer thermometer ) {
        thermometer.addObserver( this );
        this.thermometer = thermometer;
        thermometerBody = ImageLoader.fetchBufferedImage( "images/thermometer.png" );
        thermometerBackground = ImageLoader.fetchBufferedImage( "images/thermometer-background.png" );
        thermometerBI = new BufferedImage( thermometerBackground.getWidth(),
                                           thermometerBackground.getHeight(),
                                           BufferedImage.TYPE_INT_ARGB );
        update();
    }

    public boolean imageUpdate( Image img, int infoflags,
                                int x, int y, int width, int height ) {
        return false;
    }


    public void paint( Graphics2D g2 ) {

        // Set up the transform that will be applied to the thermometer images
        AffineTransform orgTx = g2.getTransform();
        AffineTransform thermometerTx = new AffineTransform();
        // Translate the transform to the model origin
        thermometerTx.translate( orgTx.getTranslateX() / orgTx.getScaleX(), orgTx.getTranslateY() / orgTx.getScaleY() );
        // Concatenate the inverse of the model-to-Swing coords transform
        try {
            thermometerTx.concatenate( g2.getTransform().createInverse() );
        }
        catch( NoninvertibleTransformException e ) {
            throw new RuntimeException( e );
        }
        // Translate the transform so the bottom of the thermometer is now at the origin
        thermometerTx.translate( 0, -thermometerBackground.getHeight() );
        // Move the thermometer graphic to where the thermometer model is
        thermometerTx.translate( location.getX() * orgTx.getScaleX() - thermometerBackground.getWidth() / 2,
                                 location.getY() * orgTx.getScaleY() );

        // Create the variable part of the thermometer: The red rectangle
        double temperatureHeight = Math.max( 0, Math.min( ( temperature - GreenhouseConfig.earthBaseTemperature ) / 10, 3.5 ) );
        temperatureHeight *= Math.abs( orgTx.getScaleY() );
        Rectangle2D.Double temperatureRect = new Rectangle2D.Double( 10,
                                                                     thermometerBackground.getHeight() - 55 - temperatureHeight,
                                                                     30,
                                                                     temperatureHeight );

        // Draw everything
        Graphics2D gbi = (Graphics2D)thermometerBI.getGraphics();
        gbi.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1 ) );
        gbi.drawImage( thermometerBackground, 0, 0, this );
        gbi.setColor( Color.RED );
        gbi.fill( temperatureRect );
        gbi.drawImage( thermometerBody, 0, 0, this );

        gbi.setFont( temperatureFont );
        String s = formatter.format( thermometer.getTemperature() ) + "K";
        FontMetrics fontMetrics = gbi.getFontMetrics();
        int width = thermometerBI.getWidth() - 14;
        int height = fontMetrics.getHeight();
        int centerX = thermometerBI.getWidth() / 2;
        int textLeft = centerX - fontMetrics.stringWidth( s ) / 2;
        int boxLeft = centerX - width / 2;
        int up = 20;
        gbi.setColor( Color.white );
        gbi.fillRect( boxLeft, thermometerBI.getHeight() - up - height + fontMetrics.getDescent(), width, height  );
        gbi.setColor( Color.black );
        gbi.drawRect( boxLeft, thermometerBI.getHeight() - up - height + fontMetrics.getDescent(), width, height  );
        gbi.setColor( Color.black );
        gbi.drawString( s, textLeft, thermometerBI.getHeight() - up );
        g2.drawImage( thermometerBI, thermometerTx, this );
    }


    // Attempt to get things to draw properly by delaying this to the end of the Swing thread cycle.
    private class Drawer implements Runnable {
        private Graphics2D g2;
        private BufferedImage background;
        private Shape var;
        private BufferedImage body;
        private AffineTransform tx;
        private ImageObserver io;

        Drawer( Graphics2D g2, BufferedImage background, Shape var, BufferedImage body, AffineTransform tx, ImageObserver io ) {

            this.g2 = g2;
            this.background = background;
            this.var = var;
            this.body = body;
            this.tx = tx;
            this.io = io;
        }

        public void run() {
            g2.drawImage( background, tx, io );
            g2.fill( tx.createTransformedShape( var ) );
            g2.drawImage( body, tx, io );
        }
    }

    public void update( Observable o, Object arg ) {
        if( o instanceof Thermometer ) {
            this.update();
        }
    }

    private void update() {
        temperature = thermometer.getTemperature();
        location.setLocation( thermometer.getLocation() );
    }
}

