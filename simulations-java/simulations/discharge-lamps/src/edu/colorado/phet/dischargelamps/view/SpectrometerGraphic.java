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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.view.util.ImageLoader;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;
import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationEvent;
import edu.colorado.phet.common.phetgraphics.view.graphics.mousecontrols.translation.TranslationListener;
import edu.colorado.phet.common.phetgraphics.view.phetcomponents.PhetButton;
import edu.colorado.phet.common.phetgraphics.view.phetgraphics.*;
import edu.colorado.phet.dischargelamps.DischargeLampsConfig;
import edu.colorado.phet.dischargelamps.DischargeLampsResources;
import edu.colorado.phet.dischargelamps.model.Spectrometer;

/**
 * SpectrometerGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class SpectrometerGraphic extends GraphicLayerSet implements Spectrometer.ChangeListener {

    private static Color UV_COLOR = new Color( 180, 180, 180 );

    // Width, in pixels, of the display area in the image;
    private int imageDisplayWidth = 410;
    private PhetImageGraphic backgroundPanel;
    private Point displayOrigin = new Point( 15, 115 );
    private int displayHeight = 100;
    private int displayWidth = 600;
    private int horizontalDisplayMargin = 80;
    private ArrayList photonMarkers = new ArrayList();
    private double minWavelength = 300; // nm
    private double maxWavelength = 800; // nm
    private boolean start = true;
    protected PhetButton startStopBtn;
    boolean startStopEnabled;

    //----------------------------------------------------------------
    // Constructor and initialization
    //----------------------------------------------------------------

    public SpectrometerGraphic( Component component, final Spectrometer spectrometer ) {
        super( component );

        spectrometer.addChangeListener( this );

        BufferedImage spectrometerImage = null;
        try {
            spectrometerImage = ImageLoader.loadBufferedImage( DischargeLampsConfig.SPECTROMETER_IMAGE_FILE_NAME );
        }
        catch( IOException e ) {
            e.printStackTrace();
        }

        // Scale the bezel image so it is the width we want it to be on the screen
        double scaleX = (double) displayWidth / imageDisplayWidth;
        AffineTransformOp op = new AffineTransformOp( AffineTransform.getScaleInstance( scaleX, 1 ),
                                                      new RenderingHints( RenderingHints.KEY_INTERPOLATION,
                                                                          RenderingHints.VALUE_INTERPOLATION_BICUBIC ) );
        spectrometerImage = op.filter( spectrometerImage, null );

        backgroundPanel = new PhetImageGraphic( component, spectrometerImage );
        backgroundPanel.setIgnoreMouse( true );
        addGraphic( backgroundPanel );

        addButtons( component, spectrometer );
        addUvIRIndicators();
        addScale();
        addTitle();

        setCursorHand();
        addTranslationListener( new DefaultTranslator( this ) );

        if ( spectrometer.isRunning() ) {
            started( null );
        }
        else {
            stopped( null );
        }
    }

    private void addScale() {
        DecimalFormat wavelengthFormat = new DecimalFormat( "000" );
        Line2D minorTickMarkShape = new Line2D.Double( 0, 0, 0, 2 );
        Line2D majorTickMarkShape = new Line2D.Double( 0, 0, 0, 5 );
        double minorTickSpacing = 10;
        double majorTickSpacing = 100;
        for ( double wavelength = minWavelength; wavelength <= maxWavelength; wavelength += minorTickSpacing ) {
            double xLoc = xLocForWavelength( wavelength ) + displayOrigin.getX() + PhotonMarker.indicatorWidth / 2;
            Line2D tickMarkShape = minorTickMarkShape;

            // Is this a major tick mark?
            if ( (int) wavelength % (int) majorTickSpacing == 0 ) {
                String label = wavelengthFormat.format( wavelength );
                PhetTextGraphic labelGraphic = new PhetTextGraphic( getComponent(),
                                                                    DischargeLampsConfig.DEFAULT_CONTROL_FONT,
                                                                    label,
                                                                    Color.white,
                                                                    (int) ( xLoc - 10 ),
                                                                    (int) ( displayOrigin.getY() + 15 ) );
                addGraphic( labelGraphic );
                labelGraphic.setIgnoreMouse( true );
                tickMarkShape = majorTickMarkShape;
            }

            PhetShapeGraphic tickMark = new PhetShapeGraphic( getComponent(), tickMarkShape, Color.white, new BasicStroke( 1 ), Color.white );
            tickMark.setLocation( (int) xLoc, (int) ( displayOrigin.getY() + 4 ) );
            tickMark.setIgnoreMouse( true );
            addGraphic( tickMark );

        }
    }

    private void addTitle() {
        String units = DischargeLampsResources.getString( "spectrometer.axis.label" );
        double xLoc = xLocForWavelength( ( minWavelength + maxWavelength ) / 2 ) - 50;
        PhetTextGraphic unitsGraphic = new PhetTextGraphic( getComponent(),
                                                            DischargeLampsConfig.DEFAULT_CONTROL_FONT,
                                                            units,
                                                            Color.white,
                                                            (int) xLoc,
                                                            (int) ( displayOrigin.getY() + 30 ) );
        unitsGraphic.setIgnoreMouse( true );
        addGraphic( unitsGraphic );
    }

    private void addUvIRIndicators() {
        double xLocUv = xLocForWavelength( minWavelength ) + displayOrigin.getX() + PhotonMarker.indicatorWidth;
        Line2D uvLine = new Line2D.Double( xLocUv, displayOrigin.getY(), xLocUv, displayOrigin.getY() - displayHeight );
        PhetShapeGraphic uvLineGraphic = new PhetShapeGraphic( getComponent(), uvLine, UV_COLOR, new BasicStroke( 1 ), UV_COLOR );
//        addGraphic( uvLineGraphic );

        PhetTextGraphic uvText = new PhetTextGraphic( getComponent(),
                                                      DischargeLampsConfig.DEFAULT_CONTROL_FONT,
                                                      "<- "+ DischargeLampsResources.getString( "spectrometer.far.uv" ),
                                                      Color.white,
                                                      (int) ( xLocUv - 80 ),
                                                      (int) ( displayOrigin.getY() + 15 ) );
        uvText.setIgnoreMouse( true );
        addGraphic( uvText );

        double xLocIr = xLocForWavelength( maxWavelength ) + displayOrigin.getX();
        Line2D irLine = new Line2D.Double( xLocIr, displayOrigin.getY(), xLocIr, displayOrigin.getY() - displayHeight );
        PhetShapeGraphic irLineGraphic = new PhetShapeGraphic( getComponent(), irLine, UV_COLOR, new BasicStroke( 1 ), UV_COLOR );
//        addGraphic( irLineGraphic );

        PhetTextGraphic irText = new PhetTextGraphic( getComponent(),
                                                      DischargeLampsConfig.DEFAULT_CONTROL_FONT,
                                                      DischargeLampsResources.getString( "spectrometer.far.ir" )+" ->",
                                                      Color.white,
                                                      (int) ( xLocIr + 30 ),
                                                      (int) ( displayOrigin.getY() + 15 ) );
        irText.setIgnoreMouse( true );
        addGraphic( irText );

    }

    private void addButtons( Component component, final Spectrometer spectrometer ) {
        // Add start/stop button
        startStopBtn = new PhetButton( component, DischargeLampsResources.getString( "spectrometer.start" )+" ");
        startStopBtn.setFont( DischargeLampsConfig.DEFAULT_CONTROL_FONT );
        startStopBtn.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                if ( start ) {
                    spectrometer.start();
                }
                else {
                    spectrometer.stop();
                }
            }
        } );
        addGraphic( startStopBtn );
        int xLocStartButton = 40;
        startStopBtn.setLocation( xLocStartButton, (int) ( backgroundPanel.getSize().height ) );
        startStopBtn.setRegistrationPoint( 0, startStopBtn.getHeight() );

        // Add reset button
        PhetButton resetBtn = new PhetButton( component, DischargeLampsResources.getString( "spectrometer.reset" ) );
        resetBtn.setFont( DischargeLampsConfig.DEFAULT_CONTROL_FONT );
        resetBtn.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                spectrometer.reset();
                startStopEnabled = true;
            }
        } );
        addGraphic( resetBtn );
        resetBtn.setLocation( xLocStartButton + startStopBtn.getWidth() + 10, (int) ( backgroundPanel.getSize().height ) );
        resetBtn.setRegistrationPoint( 0, resetBtn.getHeight() );
    }

    private int xLocForWavelength( double wavelength ) {
        double orgWavelength = wavelength;
        wavelength = Math.max( Math.min( wavelength, maxWavelength ), minWavelength );
        int wavelengthLoc = (int) ( ( wavelength - minWavelength )
                                    / ( maxWavelength - minWavelength ) * ( displayWidth - horizontalDisplayMargin * 2 )
                                    + horizontalDisplayMargin );

        if ( orgWavelength < minWavelength ) {
            wavelengthLoc -= horizontalDisplayMargin * ( minWavelength - orgWavelength ) / minWavelength;
        }
        if ( orgWavelength > maxWavelength ) {
            wavelengthLoc += horizontalDisplayMargin * ( orgWavelength - maxWavelength ) / orgWavelength;
        }
        return wavelengthLoc;
    }

    //----------------------------------------------------------------
    // Event handling
    //----------------------------------------------------------------

    public void countChanged( Spectrometer.CountChangeEvent eventCount ) {
        // Determine the y location for the wavelength of the photon in the event
        double wavelength = eventCount.getWavelength();

        // Min wavelength displays on the left
        int wavelengthLoc = xLocForWavelength( wavelength );
        double indicatorRadius = 1.5;
        int indicatorLoc = (int) ( -( eventCount.getPhotonCount() - 1 ) * indicatorRadius * 2 );

        // If we haven't filled the height of the display with indicators, add one
        if ( -indicatorLoc <= displayHeight - indicatorRadius ) {
            // Create a graphic for the photon
            Ellipse2D.Double shape = new PhotonMarker( wavelengthLoc + displayOrigin.getX(),
                                                       indicatorLoc + displayOrigin.getY() );
            Color color = VisibleColor.wavelengthToColor( wavelength );
            if ( color.getRed() == 0 && color.getGreen() == 0 & color.getBlue() == 0 ) {
                color = UV_COLOR;
            }

            PhetShapeGraphic psg = new PhetShapeGraphic( getComponent(), shape, color );
            psg.setRegistrationPoint( 0, 3 );
            photonMarkers.add( psg );
            addGraphic( psg );
            setBoundsDirty();
            repaint();
        }
    }

    public void started( Spectrometer.StateChangeEvent eventCount ) {
        startStopBtn.setText( DischargeLampsResources.getString( "spectrometer.stop" )+" " );
        start = false;
    }

    public void stopped( Spectrometer.StateChangeEvent eventCount ) {
        startStopBtn.setText( DischargeLampsResources.getString( "spectrometer.start" )+" " );
        start = true;
    }

    public void reset( Spectrometer.StateChangeEvent eventCount ) {
        while ( !photonMarkers.isEmpty() ) {
            PhetGraphic graphic = (PhetGraphic) photonMarkers.get( 0 );
            removeGraphic( graphic );
            photonMarkers.remove( 0 );
        }
    }

    //----------------------------------------------------------------
    // Inner classes
    //----------------------------------------------------------------

    private class DefaultTranslator implements TranslationListener {
        private PhetGraphic graphic;

        public DefaultTranslator( PhetGraphic graphic ) {
            this.graphic = graphic;
        }

        public void translationOccurred( TranslationEvent translationEvent ) {
            int dx = translationEvent.getDx();
            int dy = translationEvent.getDy();
            graphic.translate( dx, dy );
        }
    }

    private static class PhotonMarker extends Ellipse2D.Double {
        static double indicatorWidth = 5;
        static double indicatorHeight = 3;

        public PhotonMarker( double x, double y ) {
            super( x, y, indicatorWidth, indicatorHeight );
        }
    }
}
