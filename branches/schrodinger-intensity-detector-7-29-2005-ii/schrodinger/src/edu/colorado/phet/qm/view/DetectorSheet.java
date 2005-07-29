/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view;

import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.phetgraphics.GraphicLayerSet;
import edu.colorado.phet.common.view.phetgraphics.PhetImageGraphic;
import edu.colorado.phet.common.view.phetgraphics.PhetShapeGraphic;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.qm.phetcommon.IntegralModelElement;
import edu.colorado.phet.qm.view.colormaps.PhotonColorMap;
import edu.colorado.phet.qm.view.gun.Photon;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.LookupOp;
import java.awt.image.LookupTable;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 1:06:32 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class DetectorSheet extends GraphicLayerSet {
    private int width;
    private int height;
    private PhetShapeGraphic backgroundGraphic;
    private BufferedImage bufferedImage;
    private BufferedImage unfilteredImage;
    private PhetImageGraphic screenGraphic;

    private SchrodingerPanel schrodingerPanel;
    private int opacity = 255;
    private double brightness;
    private IntegralModelElement fadeElement;
    private PhotonColorMap.ColorData rootColor = new PhotonColorMap.ColorData( VisibleColor.MIN_WAVELENGTH );
    private ImageFade imageFade;
    private boolean fadeEnabled = true;
    private DetectorSheetPanel detectorSheetPanel;
    private boolean outputImageDirty = false;
    private IntegralModelElement refilterElement;

    public DetectorSheet( final SchrodingerPanel schrodingerPanel, int width, int height ) {
        super( schrodingerPanel );

        this.schrodingerPanel = schrodingerPanel;
        bufferedImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        unfilteredImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        screenGraphic = new PhetImageGraphic( getComponent(), bufferedImage );
//        screenGraphic.shear( 0.25, 0 );
        screenGraphic.shear( 0.45, 0 );
        screenGraphic.translate( -13, 20 );

        addGraphic( screenGraphic );

//        backgroundGraphic = new PhetShapeGraphic( schrodingerPanel, new Rectangle( width, height ), Color.white, new BasicStroke( 3 ), Color.black );
        backgroundGraphic = new PhetShapeGraphic( schrodingerPanel, new Rectangle( width, height ), Color.black, new BasicStroke( 3 ), Color.blue );
        backgroundGraphic.paint( bufferedImage.createGraphics() );
        backgroundGraphic.paint( unfilteredImage.createGraphics() );

        RenderingHints renderingHints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        setRenderingHints( renderingHints );

        this.width = width;
        this.height = height;


//        PhetGraphic saveGraphic = PhetJComponent.newInstance( schrodingerPanel, saveScreenJButton );
//        addGraphic( saveGraphic );
//        saveGraphic.setLocation( screenGraphic.getWidth(), screenGraphic.getY() );
//        this.saveGraphic = saveGraphic;
//        this.saveGraphic.setVisible( false );

        setBrightness( 1.0 );

        imageFade = new ImageFade();

        fadeElement = new IntegralModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                if( fadeEnabled ) {
//                    imageFade.fade( getBufferedImage() );
                    imageFade.fade( unfilteredImage );
//                    screenGraphic.setBoundsDirty();
//                    screenGraphic.repaint();
                }
            }
        }, 1 );


        refilterElement = new IntegralModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                if( outputImageDirty ) {
                    refilter();
                    outputImageDirty = false;
                }
            }
        }, 1 );
        schrodingerPanel.getSchrodingerModule().getModel().addModelElement( refilterElement );
        detectorSheetPanel = new DetectorSheetPanel( this );
//        detectorSheetPanelGraphic = PhetJComponent.newInstance( schrodingerPanel, detectorSheetPanel );
        detectorSheetPanel.setLocation( screenGraphic.getWidth(), 0 );
        addGraphic( detectorSheetPanel );
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
//        System.out.println( "add detect, x="+x+", y="+y+", tempOpacity = " + tempOpacity );
        detectorSheetPanel.setClearButtonVisible( true );

        setSaveButtonVisible( true );
        Graphics2D g2 = unfilteredImage.createGraphics();
        int tempOpacity = 158;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        if( rootColor != null ) {
            new ColoredDetectionGraphic( this, x, y, tempOpacity, rootColor ).paint( g2 );
        }
        else {
            new DetectionGraphic( this, x, y, tempOpacity ).paint( g2 );
        }

        this.outputImageDirty = true;

    }

    private void refilter() {
        final double filterOpacity = opacity / 255.0;
        BufferedImageOp op = new LookupOp( new LookupTable( 0, unfilteredImage.getRaster().getPixel( 0, 0, (int[])null ).length ) {
            public int[] lookupPixel( int[] src, int[] dest ) {
                if( dest == null ) {
                    dest = new int[src.length];
                }
                for( int i = 0; i < src.length; i++ ) {
                    dest[i] = (int)( src[i] * filterOpacity );
                }
                return dest;
            }
        }, null );
        op.filter( unfilteredImage, bufferedImage );
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
        unfilteredImage = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        backgroundGraphic.paint( bufferedImage.createGraphics() );
        screenGraphic.setImage( bufferedImage );
        detectorSheetPanel.setClearButtonVisible( false );
    }

    public int getOpacity() {
        return opacity;
    }

    public void setOpacity( int opacity ) {
        this.opacity = opacity;
        refilter();
    }

    public void clearScreen() {
        reset();
    }

    public void setSaveButtonVisible( boolean b ) {
        detectorSheetPanel.setSaveButtonVisible( b );
    }

    public void setDisplayPhotonColor( Photon photon ) {
        this.rootColor = photon == null ? null : new PhotonColorMap.ColorData( photon.getWavelengthNM() );
    }

    public SchrodingerPanel getSchrodingerPanel() {
        return schrodingerPanel;
    }

    private void addBrightnessSlider() {
        detectorSheetPanel.setBrightnessSliderVisible( true );
    }

    private void addFadeCheckBox() {
        detectorSheetPanel.setFadeCheckBoxVisible( true );
    }

    public void setHighIntensityMode() {
        addBrightnessSlider();
        addFadeCheckBox();
        detectorSheetPanel.setTypeControlVisible( true );
        detectorSheetPanel.setBrightness();
    }

    public DetectorSheetPanel getDetectorSheetPanel() {
        return detectorSheetPanel;
    }

}
