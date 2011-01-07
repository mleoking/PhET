// Copyright 2002-2011, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.greenhouse.view;

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.math.Function;
import edu.colorado.phet.common.phetcommon.view.util.VisibleColor;

/**
 * SpectrumImageFactory is a collection of static methods that create spectrum images
 * commonly found in PhET simulations.  All images are created programmatically.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 * @version $Revision$
 */
public class MySpectrumImageFactory {

    // Default range for wavelength
    private static final double DEFAULT_MIN_WAVELENGTH = VisibleColor.MIN_WAVELENGTH;
    private static final double DEFAULT_MAX_WAVELENGTH = VisibleColor.MAX_WAVELENGTH;

    // Default colors used to render UV and IR wavelengths
    private static final Color DEFAULT_UV_COLOR = VisibleColor.COLOR_INVISIBLE;
    private static final Color DEFAULT_IR_COLOR = VisibleColor.COLOR_INVISIBLE;

    /* Not intended for instantiation */
    private MySpectrumImageFactory() {
    }

    /**
     * Creates a horizontal image for the visible spectrum.
     *
     * @see createSpectrum
     */
    public static Image createHorizontalSpectrum( int width, int height ) {
        return createHorizontalSpectrum( width, height, DEFAULT_MIN_WAVELENGTH, DEFAULT_MAX_WAVELENGTH );
    }

    /**
     * Creates a horizontal image for a specified range of wavelengths.
     * Default color are used for any UV or IR wavelengths.
     *
     * @see createSpectrum
     */
    public static Image createHorizontalSpectrum( int width, int height, double minWavelength, double maxWavelength ) {
        return createHorizontalSpectrum( width, height, minWavelength, maxWavelength, DEFAULT_UV_COLOR, DEFAULT_IR_COLOR );
    }

    /**
     * Creates a horizontal image for a specified range of wavelengths.
     * Specified colors are used for UV and IR wavelengths.
     *
     * @see creatSpectrum
     */
    public static Image createHorizontalSpectrum( int width, int height, double minWavelength, double maxWavelength, Color uvColor, Color irColor ) {
        return createSpectrum( width, height, SwingConstants.HORIZONTAL, minWavelength, maxWavelength, uvColor, irColor );
    }

    /**
     * Creates a vertical image for the visible spectrum.
     *
     * @see createSpectrum
     */
    public static Image createVerticalSpectrum( int width, int height ) {
        return createVerticalSpectrum( width, height, DEFAULT_MIN_WAVELENGTH, DEFAULT_MAX_WAVELENGTH );
    }

    /**
     * Creates a vertical image for a specified range of wavelengths.
     * Default color are used for any UV or IR wavelengths.
     *
     * @see createSpectrum
     */
    public static Image createVerticalSpectrum( int width, int height, double minWavelength, double maxWavelength ) {
        return createVerticalSpectrum( width, height, minWavelength, maxWavelength, DEFAULT_UV_COLOR, DEFAULT_IR_COLOR );
    }

    /**
     * Creates a vertical image for a specified range of wavelengths.
     * Specified color are used for any UV or IR wavelengths.
     *
     * @see createSpectrum
     */
    public static Image createVerticalSpectrum( int width, int height, double minWavelength, double maxWavelength, Color uvColor, Color irColor ) {
        return createSpectrum( width, height, SwingConstants.VERTICAL, minWavelength, maxWavelength, uvColor, irColor );
    }

    /**
     * Creates a spectrum image.
     * For horizontal images, wavelength increases from left to right.
     * For vertical images, wavelength increases from bottom to top.
     * Visible colors are provided by the VisibleColor class.
     * UV and IR colors are provided as arguments to this method.
     *
     * @param width         desired image width
     * @param height        desired image height
     * @param orientation   SwingConstants.HORIZONTAL or SwingConstants.VERTICAL
     * @param minWavelength minimum wavelength
     * @param maxWavelength maximum wavelength
     * @param uvColor       color used for UV wavelengths
     * @param irColor       color used for IR wavelengths
     * @return Image
     */
    public static Image createSpectrum( int width, int height, int orientation,
                                        double minWavelength, double maxWavelength, Color uvColor, Color irColor ) {

        if (minWavelength <= 0 || maxWavelength <= minWavelength){
            throw( new IllegalArgumentException( "Invalid wavelength value(s)." ) );
        }

        if ( width <= 0 || height <= 0 ) {
            throw new IllegalArgumentException( "width and height must both be > 0" );
        }
        if ( minWavelength >= maxWavelength ) {
            throw new IllegalArgumentException( "minWavelength must be < maxWavelength" );
        }
        if ( orientation != SwingConstants.HORIZONTAL && orientation != SwingConstants.VERTICAL ) {
            throw new IllegalArgumentException( "invalid orientation" );
        }
        if ( uvColor == null || irColor == null ) {
            throw new NullPointerException( "uvColor or irColor is null" );
        }

        int steps = ( ( orientation == SwingUtilities.HORIZONTAL ) ? width : height );
        Function linearFunction = new Function.LinearFunction( 0, steps, minWavelength, maxWavelength );
        double expFuncBase = Math.pow( maxWavelength / minWavelength, 1 / (double)(steps - 1) );
        Function exponentialFunction = new ExponentialFunction( expFuncBase );

        BufferedImage image = new BufferedImage( width, height, BufferedImage.TYPE_INT_RGB );
        Graphics2D g2 = image.createGraphics();

        for ( int i = 0; i < steps; i++ ) {

//            double wavelength = linearFunction.evaluate( i );
            double wavelength = exponentialFunction.evaluate( i ) * minWavelength;
            System.out.println("~~~~~~~~~~~~~~~~~");
            System.out.println("Wavelength = " + wavelength);
            System.out.println("i = " + i);
            System.out.println("Linear func = " + linearFunction.evaluate( i ));
            System.out.println("Exp func = " + exponentialFunction.evaluate( i ));

            Color color = null;
            if ( wavelength < VisibleColor.MIN_WAVELENGTH ) {
                color = uvColor;
            }
            else if ( wavelength > VisibleColor.MAX_WAVELENGTH ) {
                color = irColor;
            }
            else {
                color = VisibleColor.wavelengthToColor( wavelength );
            }
            g2.setColor( color );

            if ( orientation == SwingConstants.HORIZONTAL ) {
                g2.fillRect( i, 0, 1, height ); // x,y,width,height
            }
            else {
                g2.fillRect( 0, height - i, width, 1 ); // x,y,width,height
            }
        }
        g2.dispose();

        return image;
    }

    private static class ExponentialFunction implements Function {
        private final double base;

        public ExponentialFunction( double base ) {
            this.base = base;
        }

        public double evaluate( double x ) {
            return Math.pow( base, x );
        }

        public Function createInverse() {
            throw new RuntimeException( "Not yet implemented" );
        }
    }
}
