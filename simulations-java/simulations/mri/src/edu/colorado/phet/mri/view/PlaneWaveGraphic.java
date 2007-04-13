/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.mri.view;

import edu.colorado.phet.common.util.SimpleObserver;
import edu.colorado.phet.mri.MriConfig;
import edu.colorado.phet.mri.model.PlaneWaveMedium;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;

import java.awt.*;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;

/**
 * PlaneWaveGraphic
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PlaneWaveGraphic extends PNode implements SimpleObserver, PlaneWaveMedium.Listener {

    //----------------------------------------------------------------
    // Class data and methods
    //----------------------------------------------------------------
    public static final int TO_LEFT = -1;
    public static final int TO_RIGHT = 1;

    private static final double MAX_AMPLITDUE = 100;
    private static Map maxColorToShades = new HashMap();


    public static boolean Y_GRADIENT = false;

    //----------------------------------------------------------------
    // Instance data and methods
    //----------------------------------------------------------------

    private PlaneWaveMedium waveMedium;
    private double maxOpacity;
    // TODO: This should be set by a call to initLayout, not here.
    private Point2D origin;
    private double width;
    private float opacity = 1.0f;
    private double xExtent;
    private Paint[] colorForAmplitude = new Paint[255];

    private PPath[] components;
    // Number of linear units represented by a single color in the graphic
    private double stepSize;

    /**
     * Constructor
     *
     * @param waveMedium
     * @param maxOpacity
     * @param color
     */
    public PlaneWaveGraphic( PlaneWaveMedium waveMedium,
                             double maxOpacity,
                             Color color ) {
        this.waveMedium = waveMedium;
        this.maxOpacity = maxOpacity;
        waveMedium.addObserver( this );
        stepSize = waveMedium.getSpeed();

        // Get a reference to the shades of the color we are using
        colorForAmplitude = (Paint[])maxColorToShades.get( color );
        if( colorForAmplitude == null ) {
            colorForAmplitude = computePaintArray( color );
            maxColorToShades.put( color, colorForAmplitude );
        }

        this.origin = waveMedium.getOrigin();
        this.xExtent = waveMedium.getLongitudinalExtent();
        this.width = waveMedium.getLateralExtent();
        this.stepSize = waveMedium.getSpeed();

        int numComponents = (int)( xExtent / stepSize );
        components = new PPath[ numComponents ];
        double x = 0;
        double y = 0;
        double dx = stepSize;
        double dy = stepSize;
        double fx = 0;
        double fy = 0;
        if( waveMedium.getDirection() == PlaneWaveMedium.EAST ) {
            x = stepSize;

            // TODO: HACK!!!!!
            dy = width * MriConfig.SCALE_FOR_ORG;
            fy = 0.5;
        }
        else if( waveMedium.getDirection() == PlaneWaveMedium.WEST ) {
            x = -stepSize;
            dy = width;
            fy = 0.5;
        }
        else if( waveMedium.getDirection() == PlaneWaveMedium.NORTH ) {
            y = -stepSize;
            dx = width;
            fx = 0.5;
        }
        else if( waveMedium.getDirection() == PlaneWaveMedium.SOUTH ) {
            y = stepSize;
            dx = width;
            fy = 0.5;
        }

        for( int i = 0; i < components.length; i++ ) {
            components[i] = new PPath( new Rectangle2D.Double( origin.getX() + i * x - fx * width,
                                                               origin.getY() + i * y - fy * width,
                                                               dx,
                                                               dy ) );
            components[i].setStrokePaint( null );
            addChild( components[i] );
        }

        // Compute the colors we'll need for the wave graphic
        computePaintArray( color );
    }

    /**
     * Computes the array of Paints that are needed to build the wave graphic
     *
     * @param color
     * @return an array of Paint
     */
    public Paint[] computePaintArray( Color color ) {
        int numShades = 255;
        Paint[] shades = new Paint[ numShades ];
        for( int i = 0; i < shades.length; i++ ) {
            if( Y_GRADIENT ) {
                shades[i] = new GradientPaint( 0,
                                               (int)( origin.getY() - width / 2 ),
                                               new Color( color.getRed(),
                                                          color.getGreen(),
                                                          color.getBlue(), 0 ),
                                               0,
                                               (int)( origin.getY() ),
                                               new Color( color.getRed(),
                                                          color.getGreen(),
                                                          color.getBlue(), i ),
                                               true );
            }
            else {
                shades[i] = new Color( color.getRed(),
                                       color.getGreen(),
                                       color.getBlue(),
                                       i );
            }
        }
        return shades;
    }

    /**
     * Gets the color corresponding to a particular amplitude at a particular point. The idea is to
     * match the zero pressure point in the wave medium to the background color reported by the
     * rgbReporter
     *
     * @param amplitude
     * @return the Paint for the specified amplitude
     */
    private Paint getColorForAmplitude( double amplitude ) {
        double normalizedAmplitude = Math.min( 1, Math.abs( maxOpacity * amplitude / MAX_AMPLITDUE ) );
        return colorForAmplitude[(int)( normalizedAmplitude * ( 255 - 1 ) )];
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
     * @return opacity
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
    public Point2D getOrigin() {
        return origin;
    }

    /**
     *
     */
    public void update() {
        for( int i = components.length - 1; i > 0; i-- ) {
            Paint paint = getColorForAmplitude( waveMedium.getAmplitudeAt( i * stepSize ) );
            components[i].setPaint( paint );
        }
    }

    public void leftSystem( PlaneWaveMedium planeWaveMedium ) {
        

    }
}
