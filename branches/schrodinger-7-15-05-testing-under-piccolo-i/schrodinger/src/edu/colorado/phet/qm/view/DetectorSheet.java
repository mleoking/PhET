/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.components.ModelSlider;
import edu.colorado.phet.common.view.phetcomponents.PhetJComponent;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.qm.phetcommon.IntegralModelElement;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 1:06:32 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class DetectorSheet extends GraphicLayerSet {
    private int width;
    private int height;
    private PhetShapeGraphic phetShapeGraphic;
    private BufferedImage bufferedImage;
    private PhetImageGraphic screenGraphic;
    private PhetGraphic clearButtonJC;
    private JButton clearButton;
    private SchrodingerPanel schrodingerPanel;
    private int opacity = 255;
    private Font buttonFont = new Font( "Lucida Sans", Font.BOLD, 10 );
    private Insets buttonInsets = new Insets( 2, 2, 2, 2 );
    private PhetGraphic saveGraphic;
    private double brightness;
    private IntegralModelElement fadeElement;

    public DetectorSheet( final SchrodingerPanel schrodingerPanel, int width, int height ) {
        super( schrodingerPanel );

        this.schrodingerPanel = schrodingerPanel;
        bufferedImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        screenGraphic = new PhetImageGraphic( getComponent(), bufferedImage );
//        screenGraphic.shear( 0.25, 0 );
        screenGraphic.shear( 0.45, 0 );
        screenGraphic.translate( -13, 20 );

        addGraphic( screenGraphic );

        phetShapeGraphic = new PhetShapeGraphic( schrodingerPanel, new Rectangle( width, height ), Color.white, new BasicStroke( 3 ), Color.black );
        phetShapeGraphic.paint( bufferedImage.createGraphics() );

        RenderingHints renderingHints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        setRenderingHints( renderingHints );

        clearButton = new JButton( "<html>Clear<br>Screen</html>" );

        clearButton.setMargin( buttonInsets );

        clearButton.setFont( buttonFont );
        clearButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                reset();
            }
        } );
        clearButtonJC = PhetJComponent.newInstance( schrodingerPanel, clearButton );
        addGraphic( clearButtonJC );
        clearButtonJC.setVisible( false );
        clearButtonJC.setLocation( -5 - clearButtonJC.getWidth(), 5 );
        this.width = width;
        this.height = height;

        JButton saveScreenJButton = new JButton( "<html>Save<br>Screen</html>" );
        saveScreenJButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                BufferedImage image = copyScreen();
                SavedScreenGraphic savedScreenGraphic = new SavedScreenGraphic( schrodingerPanel, image );
                schrodingerPanel.addGraphic( savedScreenGraphic );
            }
        } );
        saveScreenJButton.setMargin( buttonInsets );
        saveScreenJButton.setFont( buttonFont );
        PhetGraphic saveGraphic = PhetJComponent.newInstance( schrodingerPanel, saveScreenJButton );
        addGraphic( saveGraphic );
        saveGraphic.setLocation( screenGraphic.getWidth(), screenGraphic.getY() );
        this.saveGraphic = saveGraphic;
        this.saveGraphic.setVisible( false );

//        JSlider brightnessSlider = new JSlider( JSlider.HORIZONTAL, 0, 1000, 500 );
//
//        JLabel brightness=new JLabel( "Brightness");

        final ModelSlider brightnessModelSlider = new ModelSlider( "Screen Brightness", "", 0, 1, 0.5, new DecimalFormat( "0.000" ) );
        brightnessModelSlider.setModelTicks( new double[]{0, 0.25, 0.5, 0.75, 1.0} );
        PhetGraphic brightnessSliderGraphic = PhetJComponent.newInstance( schrodingerPanel, brightnessModelSlider );
        addGraphic( brightnessSliderGraphic );
        brightnessSliderGraphic.setLocation( saveGraphic.getX(), saveGraphic.getY() + saveGraphic.getHeight() + 10 );
        brightnessModelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                setBrightness( brightnessModelSlider.getValue() );
            }
        } );
        fadeElement = new IntegralModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                new ImageFade().fade( getBufferedImage() );
                screenGraphic.setBoundsDirty();
                screenGraphic.repaint();
            }
        }, 1 );

    }

    public void setFadeEnabled( boolean fade ) {
        if( fade ) {
            schrodingerPanel.getSchrodingerModule().getModel().addModelElement( fadeElement );
        }
        else {
            while( schrodingerPanel.getSchrodingerModule().getModel().containsModelElement( fadeElement ) ) {
                schrodingerPanel.getSchrodingerModule().getModel().removeModelElement( fadeElement );
            }
        }
    }


    private void setBrightness( double value ) {
        this.brightness = value;
        setOpacity( toOpacity( brightness ) );
    }

    private int toOpacity( double brightness ) {
        return (int)( brightness * 255 );
    }

    private BufferedImage copyScreen() {
        BufferedImage image = new BufferedImage( bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType() );
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.drawRenderedImage( bufferedImage, new AffineTransform() );
        return image;
    }

    public void addDetectionEvent( int x, int y ) {
        clearButtonJC.setVisible( true );
        setSaveButtonVisible( true );
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        new DetectionGraphic( this, x, y, opacity ).paint( g2 );
        repaint();
    }

//    public void showSaveButton() {
//        saveGraphic.setVisible( true );
//    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void reset() {
        bufferedImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        phetShapeGraphic.paint( bufferedImage.createGraphics() );
        screenGraphic.setImage( bufferedImage );
        clearButtonJC.setVisible( false );
    }

    public int getOpacity() {
        return opacity;
    }

    public void setOpacity( int opacity ) {
        this.opacity = opacity;
    }


    public void clearScreen() {
        reset();
    }

    public void setSaveButtonVisible( boolean b ) {
        saveGraphic.setVisible( b );
    }
}
