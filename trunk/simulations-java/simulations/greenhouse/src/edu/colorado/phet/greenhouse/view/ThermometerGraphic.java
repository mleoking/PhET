// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.greenhouse.view;

import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
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

import edu.colorado.phet.greenhouse.GreenhouseConfig;
import edu.colorado.phet.greenhouse.GreenhouseResources;
import edu.colorado.phet.greenhouse.common.graphics.Graphic;
import edu.colorado.phet.greenhouse.model.Thermometer;

public class ThermometerGraphic implements Graphic, Observer {
    private BufferedImage thermometerBody;
    private BufferedImage thermometerBackground;
    private double temperature;
    private Point2D.Double location = new Point2D.Double();
    private Thermometer thermometer;

    private NumberFormat formatter = new DecimalFormat( "0" );
    private Font temperatureFont = new Font( "sanserif", Font.BOLD, 14 );
    private BufferedImage thermometerBI;
    private AffineTransform scaleTx = new AffineTransform();

    public ThermometerGraphic( Thermometer thermometer ) {
        thermometer.addObserver( this );
        this.thermometer = thermometer;
        thermometerBody = GreenhouseResources.getImage( "thermometer-2.png" );
        thermometerBackground = GreenhouseResources.getImage( "thermometer-background-2.png" );
        thermometerBI = new BufferedImage( thermometerBody.getWidth(),
                                           thermometerBody.getHeight(),
                                           BufferedImage.TYPE_INT_ARGB );
        update();
    }

    public void paint( Graphics2D g2 ) {

        // Set up the transform that will be applied to the thermometer images
        AffineTransform orgTx = g2.getTransform();
        AffineTransform thermometerTx = new AffineTransform();
        // Translate the transform to the model origin
        thermometerTx.translate( orgTx.getTranslateX() / orgTx.getScaleX(), orgTx.getTranslateY() / orgTx.getScaleY() );
        if ( g2.getTransform().getDeterminant() == 0 ) {
            // the matrix is not invertible, bail.
            return;
        }
        // Concatenate the inverse of the model-to-Swing coords transform
        try {
            thermometerTx.concatenate( g2.getTransform().createInverse() );
        }
        catch( NoninvertibleTransformException e ) {
            // This should never happen because we checked the determinant above.
            throw new RuntimeException( e );
        }

        // Translate the transform so the bottom of the thermometer is now at the origin
        thermometerTx.translate( 0, -thermometerBackground.getHeight() * scaleTx.getScaleY() );
        // Move the thermometer graphic to where the thermometer model is
        thermometerTx.translate( location.getX() * orgTx.getScaleX() - thermometerBackground.getWidth() / 2,
                                 location.getY() * orgTx.getScaleY() );
        thermometerTx.concatenate( scaleTx );

        // Create the variable part of the thermometer: The red rectangle
        double temperatureHeight = Math.max( 0, Math.min( ( temperature - GreenhouseConfig.earthBaseTemperature ) / 10, 3.5 ) )
                                   *34;//increase the size so the result is visible in pixels, fine tuned for consistency with 3.01.00 version
        int redColumnXLoc = 20;
        Rectangle2D.Double temperatureRect = new Rectangle2D.Double( redColumnXLoc,
                                                                     thermometerBackground.getHeight() - 55 - temperatureHeight,
                                                                     30,
                                                                     temperatureHeight );

        // Draw everything
        Graphics2D gbi = (Graphics2D) thermometerBI.getGraphics();
        gbi.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, 1 ) );
        gbi.drawImage( thermometerBackground, 0, 0, null );
        gbi.setColor( Color.RED );
        gbi.fill( temperatureRect );
        gbi.drawImage( thermometerBody, 0, 0, null );

        // Temperatures
        gbi.setFont( temperatureFont );
        String s = formatter.format( thermometer.getTemperature() ) + GreenhouseResources.getString( "ThermometerGraphic.KelvinUnits" );
        FontMetrics fontMetrics = gbi.getFontMetrics();
        int width = thermometerBI.getWidth() - 25;
        int height = fontMetrics.getHeight();
        int centerX = thermometerBI.getWidth() / 2;
        int boxLeft = centerX - width / 2;
        int textLeft = boxLeft + width - fontMetrics.stringWidth( s ) - fontMetrics.stringWidth( " " );

        // Kelvin
        int up = 40;
        gbi.setColor( Color.white );
        gbi.fillRect( boxLeft, thermometerBI.getHeight() - up - height + fontMetrics.getDescent(), width, height );
        gbi.setColor( Color.black );
        gbi.drawRect( boxLeft, thermometerBI.getHeight() - up - height + fontMetrics.getDescent(), width, height );
        gbi.setColor( Color.black );
        gbi.drawString( s, textLeft, thermometerBI.getHeight() - up );

        // Fahrenheit
        double nonKelvinTemperature = 0;
        String units = "";
        if ( GreenhouseConfig.TEMPERATURE_UNITS == GreenhouseConfig.FAHRENHEIT ) {
            nonKelvinTemperature = kelvinToFahrenheit( thermometer.getTemperature() );
            units = GreenhouseResources.getString( "ThermometerGraphic.FahrenheitUnits" );
        }
        else if ( GreenhouseConfig.TEMPERATURE_UNITS == GreenhouseConfig.CELSIUS ) {
            nonKelvinTemperature = kelvinToCelsius( thermometer.getTemperature() );
            units = GreenhouseResources.getString( "ThermometerGraphic.CelsiusUnits" );
        }
        s = formatter.format( nonKelvinTemperature ) + units;
        up -= height;
        gbi.setColor( Color.white );
        gbi.fillRect( boxLeft, thermometerBI.getHeight() - up - height + fontMetrics.getDescent(), width, height );
        gbi.setColor( Color.black );
        gbi.drawRect( boxLeft, thermometerBI.getHeight() - up - height + fontMetrics.getDescent(), width, height );
        gbi.setColor( Color.black );
        textLeft = boxLeft + width - fontMetrics.stringWidth( s ) - fontMetrics.stringWidth( " " );
        gbi.drawString( s, textLeft, thermometerBI.getHeight() - up );
        gbi.dispose();

        // Draw it
        g2.drawImage( thermometerBI, thermometerTx, null );
    }

    private double kelvinToFahrenheit( double k ) {
        double f = ( ( k - 273.15 ) * 1.8 ) + 32;
        return f;
    }

    private double kelvinToCelsius( double k ) {
        double c = k - 273.15;
        return c;
    }

    public void update( Observable o, Object arg ) {
        if ( o instanceof Thermometer ) {
            this.update();
        }
    }

    private void update() {
        temperature = thermometer.getTemperature();
        location.setLocation( thermometer.getLocation() );
    }
}