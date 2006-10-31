/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.view.piccolo.detectorscreen;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.ModelElement;
import edu.colorado.phet.common.view.util.ImageLoader;
import edu.colorado.phet.common.view.util.VisibleColor;
import edu.colorado.phet.piccolo.PhetPNode;
import edu.colorado.phet.qm.controls.DetectorSheetControlPanel;
import edu.colorado.phet.qm.modules.intensity.IntensityBeamPanel;
import edu.colorado.phet.qm.phetcommon.IntegralModelElement;
import edu.colorado.phet.qm.phetcommon.ShinyPanel;
import edu.colorado.phet.qm.view.QWIPanel;
import edu.colorado.phet.qm.view.colormaps.ColorData;
import edu.colorado.phet.qm.view.piccolo.WavefunctionGraphic;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.pswing.PSwing;

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
    private QWIPanel QWIPanel;

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
    private PSwing detectorSheetControlPanelPNode;
    private MyConnectorGraphic connectorGraphic;
    private final double shearAngle = 0.4636;
    private PText title = new PText();
    //    public static final int DEFAULT_FADE_DELAY = 10;//before edits on 10-31-06
    public static final int DEFAULT_FADE_DELAY = 50;

    public DetectorSheetPNode( final QWIPanel QWIPanel, WavefunctionGraphic wavefunctionGraphic, final int detectorSheetHeight ) {
        this.wavefunctionGraphic = wavefunctionGraphic;
        this.detectorSheetHeight = detectorSheetHeight;

        this.QWIPanel = QWIPanel;
        recreateImage();
//        title.setShadowColor( Color.black );
        title.setTextPaint( Color.black );
        title.setFont( new Font( "Lucida Sans", Font.BOLD, 14 ) );
        screenGraphic = new ScreenGraphic( bufferedImage );

        setBrightness( 1.0 );
        imageFade = new ImageFade();
        fadeElement = new IntegralModelElement( new ModelElement() {
            public void stepInTime( double dt ) {
                if( isFadeEnabled() && !isContinuousDisplay() ) {
                    imageFade.fade( getBufferedImage() );
                    screenGraphic.repaint();
                }
            }

            private boolean isContinuousDisplay() {
                return DetectorSheetPNode.this.isContinuousDisplay();
            }
        }, DEFAULT_FADE_DELAY );
        this.detectorSheetControlPanel = new DetectorSheetControlPanel( this );
        detectorSheetControlPanelPNode = new PSwing( QWIPanel, new ShinyPanel( detectorSheetControlPanel ) );
//        detectorSheetControlPanelPNode = new PSwing( schrodingerPanel,  detectorSheetControlPanel  );

//        FontSetter.setFont( new Font( "Lucida Sans",Font.PLAIN, 8 ),detectorSheetControlPanel );
//        detectorSheetControlPanelPNode = new PhetPNode( new PPath( new Ellipse2D.Double( 50, 50, 50, 50 ) ) );

        PropertyChangeListener changeListener = new PropertyChangeListener() {
            public void propertyChange( PropertyChangeEvent evt ) {
                recreateImage();
                screenGraphic.setImage( bufferedImage );
            }
        };
        wavefunctionGraphic.addPropertyChangeListener( PNode.PROPERTY_FULL_BOUNDS, changeListener );
        wavefunctionGraphic.addPropertyChangeListener( PNode.PROPERTY_BOUNDS, changeListener );
        QWIPanel.addListener( new QWIPanel.Adapter() {
            public void fadeStateChanged() {
                synchronizeFadeState();
            }
        } );
        synchronizeFadeState();

        connectorGraphic = new MyConnectorGraphic();
        addChild( connectorGraphic );
        addChild( screenGraphic );
        addChild( detectorSheetControlPanelPNode );

        addChild( title );
    }

    public void setFadeDelay( int delay ) {
        fadeElement.setInterval( delay );
    }

    private boolean isContinuousDisplay() {
        if( QWIPanel instanceof IntensityBeamPanel ) {
            IntensityBeamPanel ip = (IntensityBeamPanel)QWIPanel;
            return ip.isContinuousMode();
        }
        return false;
    }

    protected void layoutChildren() {
        screenGraphic.setTransform( new AffineTransform() );
        double shearfac = Math.tan( shearAngle );
        screenGraphic.getTransformReference( true ).shear( shearfac, 0 );
        detectorSheetControlPanelPNode.setOffset( screenGraphic.getFullBounds().getWidth() + 12, screenGraphic.getFullBounds().getY() );
        title.setOffset( 0, screenGraphic.getFullBounds().getY() - title.getFullBounds().getHeight() );
        connectorGraphic.update();
    }

    public void setAlignment( WavefunctionGraphic wavefunctionGraphic ) {
        double offsetDX = screenGraphic.getFullBounds().getHeight() / 2.0 * Math.tan( shearAngle );
        setOffset( wavefunctionGraphic.getFullBounds().getX() - offsetDX,
                   wavefunctionGraphic.getFullBounds().getY() - screenGraphic.getFullBounds().getHeight() / 2.0 );
    }

    public void synchronizeFadeState() {
        if( QWIPanel.isFadeEnabled() ) {
            getBaseModel().addModelElement( fadeElement );
        }
        else {
            while( getBaseModel().containsModelElement( fadeElement ) ) {
                getBaseModel().removeModelElement( fadeElement );
            }
        }
    }

    private BaseModel getBaseModel() {
        return QWIPanel.getSchrodingerModule().getModel();
    }

    public boolean isFadeEnabled() {
        return QWIPanel.isFadeEnabled();
    }

    public void setBrightness( double value ) {
        this.brightness = value;
        setOpacity( toOpacity( brightness ) );
    }

    private int toOpacity( double brightness ) {
        return (int)( brightness * 255 );
    }

    public BufferedImage copyScreen() {
//        int h = isSmoothScreen() ? bufferedImage.getHeight() : bufferedImage.getHeight() / 2;
        int h = bufferedImage.getHeight() / 2;

        BufferedImage image = new BufferedImage( bufferedImage.getWidth(), h, bufferedImage.getType() );
        Graphics2D g2 = image.createGraphics();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.drawRenderedImage( bufferedImage, new AffineTransform() );
//        g2.drawRenderedImage( bufferedImage, AffineTransform.getTranslateInstance( 0, bufferedImage.getHeight() / 2 ) );
        g2.dispose();
        return image;
    }

    public void addDetectionEvent( double x, double y ) {
        if( detectionRateDebugger != null ) {
            detectionRateDebugger.addDetectionEvent();
        }
        detectorSheetControlPanel.setClearButtonVisible( true );

        setSaveButtonVisible( true );
        Graphics2D g2 = bufferedImage.createGraphics();
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        PNode detectionGraphic = createDetectionGraphic( x, y, opacity );
        detectionGraphic.fullPaint( new PPaintContext( g2 ) );
        g2.dispose();
        repaint();
    }

    private PNode createDetectionGraphic( double x, double y, int opacity ) {
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
        recreateImage();
        screenGraphic.setImage( bufferedImage );
        //clear button should always be enabled
//        detectorSheetControlPanel.setClearButtonVisible( false );
    }

    private void recreateImage() {
        int w = wavefunctionGraphic.getWavefunctionGraphicWidth();
        int h = detectorSheetHeight;

        if( bufferedImage != null && bufferedImage.getWidth() == w && bufferedImage.getHeight() == h ) {
            //noop
//            System.out.println( "Avoiding recreate: same size." );
            Graphics2D g2 = bufferedImage.createGraphics();
            g2.setPaint( Color.black );
            g2.fillRect( 0, 0, bufferedImage.getWidth(), bufferedImage.getHeight() );
        }
        else {
//            System.out.println( "DetectorSheetPNode.recreateImage, w=" + w + ", h=" + h );
            bufferedImage = new BufferedImage( w, h, BufferedImage.TYPE_INT_RGB );
        }
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

    public void setDisplayPhotonColor( ColorData colorData ) {
        this.rootColor = colorData;
//        System.out.println( "rootColor = " + rootColor );
    }

    public QWIPanel getSchrodingerPanel() {
        return QWIPanel;
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

    public PNode getDetectorSheetControlPanelPNode() {
        return detectorSheetControlPanelPNode;
    }

    public int getDetectorHeight() {
        return detectorSheetHeight;
    }

    public void histogramChanged() {
        screenGraphic.repaint();
    }

    public void updatePSwing() {
        detectorSheetControlPanelPNode.computeBounds();
    }

    public void setTitle( String s ) {
        title.setText( s );
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
//            path.setStroke( new BasicStroke( 2 ) );
            path.setStrokePaint( Color.darkGray );
            addChild( path );
            try {
                txtr = ImageLoader.loadBufferedImage( "images/wire.png" );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }

        public void update() {
            double connectorHeight = txtr.getHeight();

            double width = detectorSheetControlPanelPNode.getFullBounds().getCenterX() - screenGraphic.getFullBounds().getCenterX();
            Rectangle2D.Double aShape = new Rectangle2D.Double( screenGraphic.getFullBounds().getCenterX(), screenGraphic.getFullBounds().getCenterY() - connectorHeight / 2, width, connectorHeight );
            path.setPathTo( aShape );
            path.setPaint( new TexturePaint( txtr, new Rectangle2D.Double( 0, aShape.getY(), txtr.getWidth(), txtr.getHeight() ) ) );
        }
    }

}
