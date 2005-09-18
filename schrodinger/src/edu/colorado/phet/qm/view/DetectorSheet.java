/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.qm.phetcommon.IntegralModelElement;
import edu.colorado.phet.qm.view.colormaps.PhotonColorMap;
import edu.colorado.phet.qm.view.gun.Photon;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 1:06:32 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class DetectorSheet extends PNode {
    private SchrodingerPanel schrodingerPanel;

//    private int width;
//    private int height;
    private PhetShapeGraphic backgroundGraphic;
    private BufferedImage bufferedImage;
    private PImage screenGraphic;
    private int opacity = 255;
    private double brightness;
    private IntegralModelElement fadeElement;
    private PhotonColorMap.ColorData rootColor = new PhotonColorMap.ColorData( VisibleColor.MIN_WAVELENGTH );
    private ImageFade imageFade;
    private boolean fadeEnabled = true;
    private DetectorSheetControlPanelPNode detectorSheetControlPanelPNode;
    private DetectionIntensityCounter detectionIntensityCounter = new DetectionIntensityCounter();
    private WavefunctionGraphic wavefunctionGraphic;
    private int detectorSheetHeight;
//    private int detectorSheetHeight;

    public DetectorSheet( final SchrodingerPanel schrodingerPanel, WavefunctionGraphic wavefunctionGraphic, final int detectorSheetHeight ) {
        this.wavefunctionGraphic = wavefunctionGraphic;
        this.detectorSheetHeight = detectorSheetHeight;
        this.schrodingerPanel = schrodingerPanel;
        bufferedImage = new BufferedImage( wavefunctionGraphic.getWavefunctionGraphicWidth(), detectorSheetHeight, BufferedImage.TYPE_INT_RGB );
        screenGraphic = new PImage( bufferedImage );
        addChild( screenGraphic );

        backgroundGraphic = new PhetShapeGraphic( schrodingerPanel, new Rectangle( wavefunctionGraphic.getWavefunctionGraphicWidth(), detectorSheetHeight ), Color.black, new BasicStroke( 3 ), Color.blue );
        backgroundGraphic.paint( bufferedImage.createGraphics() );

//        PRenderingHints renderingHints = new PRenderingHints();
//        renderingHints.putRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
//        setRenderingHints( renderingHints );

//        this.width = width;
//        this.height = detectorSheetHeight;

        setBrightness( 1.0 );
        imageFade = new ImageFade();
        fadeElement = new IntegralModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                if( fadeEnabled ) {
                    imageFade.fade( getBufferedImage() );
                    screenGraphic.repaint();
                }
            }
        }, 1 );
        detectorSheetControlPanelPNode = new DetectorSheetControlPanelPNode( this );
        addChild( detectorSheetControlPanelPNode );
        PropertyChangeListener changeListener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                bufferedImage = new BufferedImage( getWavefunctionGraphic().getWavefunctionGraphicWidth(), detectorSheetHeight, BufferedImage.TYPE_INT_RGB );
                screenGraphic.setImage( bufferedImage );
            }
        };
        wavefunctionGraphic.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, changeListener );
        wavefunctionGraphic.addPropertyChangeListener( PNode.PROPERTY_BOUNDS, changeListener );
    }

    private WavefunctionGraphic getWavefunctionGraphic() {
        return getSchrodingerPanel().getWavefunctionGraphic();
    }

    protected void layoutChildren() {
        detectorSheetControlPanelPNode.setOffset( screenGraphic.getFullBounds().getWidth(), 0 );
        screenGraphic.setTransform( new AffineTransform() );
        screenGraphic.getTransformReference( true ).shear( 0.45, 0 );
        screenGraphic.translate( -30, 40 );
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
        this.fadeEnabled = fade;
    }

    public void setBrightness( double value ) {
        System.out.println( "brightness = " + value + ", opacity=" + toOpacity( value ) );
        this.brightness = value;
        setOpacity( toOpacity( brightness ) );
    }

    private int toOpacity( double brightness ) {
        return (int)( brightness * 255 );
    }

    public BufferedImage copyScreen() {
        BufferedImage image = new BufferedImage( bufferedImage.getWidth(), bufferedImage.getHeight(), bufferedImage.getType() );
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.drawRenderedImage( bufferedImage, new AffineTransform() );
        return image;
    }

    public void addDetectionEvent( int x, int y ) {
        if( detectionIntensityCounter != null ) {
            detectionIntensityCounter.addDetectionEvent();
        }
//        System.out.println( "add detect, x="+x+", y="+y+", opacity = " + opacity );
        detectorSheetControlPanelPNode.setClearButtonVisible( true );

        setSaveButtonVisible( true );
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        if( rootColor != null ) {
            new ColoredDetectionGraphic( this, x, y, opacity, rootColor ).fullPaint( new PPaintContext( g2 ) );
        }
        else {
            new DetectionGraphic( this, x, y, opacity ).fullPaint( new PPaintContext( g2 ) );
        }
        repaint();
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void reset() {
        bufferedImage = new BufferedImage( wavefunctionGraphic.getWavefunctionGraphicWidth(), detectorSheetHeight, BufferedImage.TYPE_INT_RGB );
        backgroundGraphic.paint( bufferedImage.createGraphics() );
        screenGraphic.setImage( bufferedImage );
        detectorSheetControlPanelPNode.setClearButtonVisible( false );
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
        detectorSheetControlPanelPNode.setSaveButtonVisible( b );
    }

    public void setDisplayPhotonColor( Photon photon ) {
        this.rootColor = photon == null ? null : new PhotonColorMap.ColorData( photon.getWavelengthNM() );
    }

    public SchrodingerPanel getSchrodingerPanel() {
        return schrodingerPanel;
    }

    private void addBrightnessSlider() {
        detectorSheetControlPanelPNode.setBrightnessSliderVisible( true );
    }

    private void addFadeCheckBox() {
        detectorSheetControlPanelPNode.setFadeCheckBoxVisible( true );
    }

    public void setHighIntensityMode() {
        addBrightnessSlider();
        addFadeCheckBox();
        detectorSheetControlPanelPNode.setTypeControlVisible( true );
        detectorSheetControlPanelPNode.setBrightness();
    }

    public DetectorSheetControlPanelPNode getDetectorSheetPanel() {
        return detectorSheetControlPanelPNode;
    }
}
