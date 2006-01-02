/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo.detectorscreen;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.piccolo.pswing.PSwing;
import edu.colorado.phet.qm.modules.intensity.HighIntensitySchrodingerPanel;
import edu.colorado.phet.qm.phetcommon.IntegralModelElement;
import edu.colorado.phet.qm.view.colormaps.ColorData;
import edu.colorado.phet.qm.view.gun.Photon;
import edu.colorado.phet.qm.view.piccolo.WavefunctionGraphic;
import edu.colorado.phet.qm.view.swing.DetectorSheetControlPanel;
import edu.colorado.phet.qm.view.swing.SchrodingerPanel;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Jun 23, 2005
 * Time: 1:06:32 PM
 * Copyright (c) Jun 23, 2005 by Sam Reid
 */

public class DetectorSheetPNode extends PhetPNode {
    private SchrodingerPanel schrodingerPanel;

    private BufferedImage bufferedImage;
    private ScreenGraphic screenGraphic;
    private int opacity = 255;
    private double brightness;
    private IntegralModelElement fadeElement;
    private ColorData rootColor = new ColorData( VisibleColor.MIN_WAVELENGTH );
    private ImageFade imageFade;
    private DetectionRateDebugger detectionRateDebugger = new DetectionRateDebugger();
    private WavefunctionGraphic wavefunctionGraphic;
    private int detectorSheetHeight;
    private DetectorSheetControlPanel detectorSheetControlPanel;
    private PNode detectorSheetControlPanelPNode;
    private MyConnectorGraphic connectorGraphic;

    public DetectorSheetPNode( final SchrodingerPanel schrodingerPanel, WavefunctionGraphic wavefunctionGraphic, final int detectorSheetHeight ) {
        this.wavefunctionGraphic = wavefunctionGraphic;
        this.detectorSheetHeight = detectorSheetHeight;
        this.schrodingerPanel = schrodingerPanel;
        bufferedImage = new BufferedImage( wavefunctionGraphic.getWavefunctionGraphicWidth(), detectorSheetHeight, BufferedImage.TYPE_INT_RGB );
        screenGraphic = new ScreenGraphic( bufferedImage );

        setBrightness( 1.0 );
        imageFade = new ImageFade();
        fadeElement = new IntegralModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                if( isFadeEnabled() && !isSmoothScreen() ) {
                    imageFade.fade( getBufferedImage() );
                    screenGraphic.repaint();
                }
            }

            private boolean isSmoothScreen() {
                if( schrodingerPanel instanceof HighIntensitySchrodingerPanel ) {
                    HighIntensitySchrodingerPanel ip = (HighIntensitySchrodingerPanel)schrodingerPanel;
                    return ip.isSmoothScreen();
                }
                return false;
            }
        }, 10 );
        this.detectorSheetControlPanel = new DetectorSheetControlPanel( this );
        detectorSheetControlPanelPNode = new PSwing( schrodingerPanel, detectorSheetControlPanel ) {
            public void setPaintInvalid( boolean paintInvalid ) {
                super.setPaintInvalid( paintInvalid );
            }

            public void invalidateLayout() {
                super.invalidateLayout();
            }

            public void invalidateFullBounds() {
                super.invalidateFullBounds();
            }

            public void invalidatePaint() {
                super.invalidatePaint();
            }

            protected boolean validateFullBounds() {
                return super.validateFullBounds();
            }

            public void validateFullPaint() {
                super.validateFullPaint();
            }

            public void repaintFrom( PBounds localBounds, PNode childOrThis ) {
                super.repaintFrom( localBounds, childOrThis );
            }

        };
//        detectorSheetControlPanelPNode = new PhetPNode( new PPath( new Ellipse2D.Double( 50, 50, 50, 50 ) ) );

        PropertyChangeListener changeListener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                bufferedImage = new BufferedImage( getWavefunctionGraphic().getWavefunctionGraphicWidth(), detectorSheetHeight, BufferedImage.TYPE_INT_RGB );
                screenGraphic.setImage( bufferedImage );
            }
        };
        wavefunctionGraphic.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, changeListener );
        wavefunctionGraphic.addPropertyChangeListener( PNode.PROPERTY_BOUNDS, changeListener );
        schrodingerPanel.addListener( new SchrodingerPanel.Adapter() {
            public void fadeStateChanged() {
                synchronizeFadeState();
            }
        } );
        synchronizeFadeState();

        connectorGraphic = new MyConnectorGraphic();
        addChild( connectorGraphic );
        addChild( screenGraphic );
        addChild( detectorSheetControlPanelPNode );
    }

    private WavefunctionGraphic getWavefunctionGraphic() {
        return getSchrodingerPanel().getWavefunctionGraphic();
    }

    protected void layoutChildren() {
        screenGraphic.setTransform( new AffineTransform() );
        screenGraphic.getTransformReference( true ).shear( 0.45, 0 );
        //todo Don't hard code this translation.
        screenGraphic.translate( -42, 62 );
        detectorSheetControlPanelPNode.setOffset( screenGraphic.getFullBounds().getWidth() + 12, screenGraphic.getFullBounds().getY() );
        connectorGraphic.update();
    }

    public void synchronizeFadeState() {
        if( schrodingerPanel.isFadeEnabled() ) {
            getBaseModel().addModelElement( fadeElement );
        }
        else {
            while( getBaseModel().containsModelElement( fadeElement ) ) {
                getBaseModel().removeModelElement( fadeElement );
            }
        }
    }

    private BaseModel getBaseModel() {
        return schrodingerPanel.getSchrodingerModule().getModel();
    }

    public boolean isFadeEnabled() {
        return schrodingerPanel.isFadeEnabled();
    }

    public void setBrightness( double value ) {
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
        if( detectionRateDebugger != null ) {
            detectionRateDebugger.addDetectionEvent();
        }
        detectorSheetControlPanel.setClearButtonVisible( true );

        setSaveButtonVisible( true );
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        PNode detectionGraphic = createDetectionGraphic( x, y, opacity );
        detectionGraphic.fullPaint( new PPaintContext( g2 ) );
        repaint();
    }

    private PNode createDetectionGraphic( int x, int y, int opacity ) {
        if( rootColor != null ) {
            return new HitGraphic( x, y, opacity, rootColor );
        }
        else {
            return new HitGraphic( x, y, opacity );
        }
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void reset() {
        bufferedImage = new BufferedImage( wavefunctionGraphic.getWavefunctionGraphicWidth(), detectorSheetHeight, BufferedImage.TYPE_INT_RGB );
        screenGraphic.setImage( bufferedImage );
        detectorSheetControlPanel.setClearButtonVisible( false );
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
        detectorSheetControlPanel.setSaveButtonVisible( b );
    }

    public void setDisplayPhotonColor( Photon photon ) {
        this.rootColor = photon == null ? null : new ColorData( photon.getWavelengthNM() );
    }

    public SchrodingerPanel getSchrodingerPanel() {
        return schrodingerPanel;
    }

    private void addBrightnessSlider() {
        detectorSheetControlPanel.setBrightnessSliderVisible( true );
    }

    private void addFadeCheckBox() {
        detectorSheetControlPanel.setFadeCheckBoxVisible( true );
    }

    public void setHighIntensityMode() {
        addBrightnessSlider();
        addFadeCheckBox();
        detectorSheetControlPanel.setTypeControlVisible( true );
        detectorSheetControlPanel.setBrightness();
    }

    public DetectorSheetControlPanel getDetectorSheetControlPanel() {
        return detectorSheetControlPanel;
    }

    public PNode getDetectorSheetPanel() {
        return detectorSheetControlPanelPNode;
    }

    public int getDetectorHeight() {
        return detectorSheetHeight;
    }

    public void histogramChanged() {
        screenGraphic.repaint();
    }

    static class ScreenGraphic extends PNode {
        PImage screenGraphic;
        PPath borderGraphic;

        public ScreenGraphic( BufferedImage bufferedImage ) {
            screenGraphic = new PImage();
            borderGraphic = new PPath();
            borderGraphic.setStrokePaint( Color.lightGray );
            borderGraphic.setStroke( new BasicStroke( 2 ) );
            addChild( screenGraphic );
            addChild( borderGraphic );
            setImage( bufferedImage );
        }

        public void setImage( BufferedImage bufferedImage ) {
            screenGraphic.setImage( bufferedImage );
            borderGraphic.setPathTo( new Rectangle2D.Double( 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight() ) );
        }
    }

    private class MyConnectorGraphic extends PNode {
        private PPath path;
        private BufferedImage txtr;

        public MyConnectorGraphic() {
            path = new PPath();
            path.setStroke( new BasicStroke( 2 ) );
            path.setStrokePaint( Color.darkGray );
            addChild( path );
            try {
                txtr = ImageLoader.loadBufferedImage( "images/computertexture.gif" );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }

        public void update() {
            double connectorHeight = detectorSheetHeight - 10;
            double width = detectorSheetControlPanelPNode.getFullBounds().getCenterX() - screenGraphic.getFullBounds().getCenterX();
            Rectangle2D.Double aShape = new Rectangle2D.Double( screenGraphic.getFullBounds().getCenterX(), screenGraphic.getFullBounds().getCenterY() - connectorHeight / 2, width, connectorHeight );
            path.setPathTo( aShape );
            path.setPaint( new TexturePaint( txtr, new Rectangle2D.Double( 0, 0, txtr.getWidth() / 3.0, txtr.getHeight() / 3.0 ) ) );
        }
    }

}
