/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info - Filename : $Source$ Branch : $Name$ Modified by : $Author$
 * Revision : $Revision$ Date modified : $Date: 2008-09-03 12:07:16
 * -0600 (Wed, 03 Sep 2008) $
 */

package edu.colorado.phet.radiowaves.view;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.common_1200.view.PhetImageGraphic;
import edu.colorado.phet.radiowaves.EmfConfig;
import edu.colorado.phet.radiowaves.model.Electron;

/**
 * WaveMdeiumGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */

/**
 * This variant of WaveMediumGraphic is the one used in the non-interefernce modules of the sound simulation.
 */
public class WaveMediumGraphic extends PhetImageGraphic implements SimpleObserver {

    //----------------------------------------------------------------
    // Class data and methods
    //----------------------------------------------------------------

    public static final int TO_LEFT = -1;
    public static final int TO_RIGHT = 1;

    // Note that larger values for the stroke slow down performance considerably
    private static double s_defaultStrokeWidth = 5;
    private static Stroke s_defaultStroke = new BasicStroke( (float) s_defaultStrokeWidth );
    public static boolean Y_GRADIENT = false;

    private static BufferedImage createBufferedImage() {
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice gs = ge.getDefaultScreenDevice();
        GraphicsConfiguration gc = gs.getDefaultConfiguration();
        return gc.createCompatibleImage( 800, 800 );
        //        return gc.createCompatibleImage( 300, 200 );
    }

    //----------------------------------------------------------------
    // Instance data and methods
    //----------------------------------------------------------------

    // TODO: This should be set by a call to initLayout, not here.
    private Electron electron;
    private Point2D origin;
    private double height = EmfConfig.WAVEFRONT_HEIGHT;
    private double stroke = 1;
    private boolean isPlanar = true;
    private float opacity = 1.0f;
    private static final double MAX_AMPLITDUE = 100;
    private double xExtent;
    private double xOffset = 15;
    private int direction;
    private Color maxAmplitudeColor = Color.red;
    private Paint[] colorForAmplitude = new Paint[255];

    //    private Color[] colorForAmplitude = new Color[255];

    /**
     * todo: rename WaveMediumGraphic
     */
    public WaveMediumGraphic( Electron electron, Component component, Point2D origin, double xExtent, int direction ) {
        super( component, createBufferedImage() );

        this.origin = origin;
        this.xExtent = xExtent;
        this.direction = direction;

        // Hook up to the WaveMedium we are observing
        this.electron = electron;
        electron.addObserver( this );

        setMaxAmplitudeColor( Color.red );
    }

    public void setMaxAmplitudeColor( Color color ) {
        for ( int i = 0; i < colorForAmplitude.length; i++ ) {
            if ( Y_GRADIENT ) {
                colorForAmplitude[i] = new GradientPaint( 0, (int) ( origin.getY() - height / 2 ), new Color( color.getRed(), color.getGreen(), color.getBlue(), 0 ), 0, (int) ( origin.getY() ), new Color( color.getRed(), color.getGreen(), color.getBlue(), i ), true );
            }
            else {
                colorForAmplitude[i] = new Color( color.getRed(), color.getGreen(), color.getBlue(), i );
            }
        }
    }

    /**
     * Gets the color corresponding to a particular amplitude at a particular point. The idea is to
     * match the zero pressure point in the wave medium to the background color reported by the
     * rgbReporter
     *
     * @param amplitude
     * @return
     */
    private Paint getColorForAmplitude( double amplitude ) {
        double normalizedAmplitude = Math.min( 1, Math.abs( amplitude / MAX_AMPLITDUE ) );
        return colorForAmplitude[(int) ( normalizedAmplitude * ( 255 - 1 ) )];
    }

    private void setGraphicsHints( Graphics2D g2 ) {
        g2.setRenderingHint( RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_SPEED );
        g2.setRenderingHint( RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR );
        g2.setRenderingHint( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED );
        //        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF );
        g2.setRenderingHint( RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE );
        g2.setRenderingHint( RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED );
        g2.setRenderingHint( RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF );
    }

    /**
     * @return
     */
    public float getOpacity() {
        return opacity;
    }

    /**
     * @param opacity
     */
    public void setOpacity( float opacity ) {
        this.opacity = opacity;
    }

    /**
     *
     */
    public void paint( Graphics2D g ) {

        this.setGraphicsHints( g );

        // Set opacity
        Composite incomingComposite = g.getComposite();
        g.setComposite( AlphaComposite.getInstance( AlphaComposite.SRC_OVER, opacity ) );

        //        g.setStroke( new BasicStroke( 1 ));

        Point2D end1 = new Point2D.Float();
        Point2D end2 = new Point2D.Float();
        Line2D line = new Line2D.Float();

        Rectangle2D rect = new Rectangle2D.Double();

        // Draw a line or arc for each value in the amplitude array of the wave front
        for ( double x = 1; x * direction < xExtent; x += s_defaultStrokeWidth * direction ) {
            g.setPaint( getColorForAmplitude( electron.getDynamicFieldAt( new Point2D.Double( origin.getX() + x, origin.getY() ) ).getMagnitude() ) );
            if ( this.isPlanar ) {
                end1.setLocation( origin.getX() + ( xOffset * direction ) + x - s_defaultStrokeWidth / 2, origin.getY() - height / 2 );
                //                end2.setLocation( origin.getX() + ( xOffset * direction ) + x, origin.getY() + height / 2 );
                rect.setRect( end1.getX(), end1.getY(), s_defaultStrokeWidth, height );
                g.fill( rect );
            }
        }
        g.setComposite( incomingComposite );
    }

    /**
     *
     */
    public Point2D getOrigin() {
        return origin;
    }

    /**
     *
     */
    public void update() {
        //        for( int i = 0; i < amplitudes.length; i++ ) {
        //            amplitudes[i] = electron.getDynamicFieldAt( (double)i );
        //        }
        this.repaint();
    }
}
