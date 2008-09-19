/* Copyright 2008, University of Colorado */

package edu.colorado.phet.faraday.view;

import java.awt.Color;

import edu.colorado.phet.faraday.FaradayConstants;

/**
 * NeedleColorStrategy is the base class for strategies that convert B-field strength to Colors.
 * B-field strength is a scale value between 0 and 1.
 * <p>
 * Two strategies are provided.
 * <p>
 * AlphaColorStrategy modulates the alpha channel, with stronger B-field being more opaque.
 * This strategy works on any background color, but has a performance cost associated with
 * using the alpha channel.
 * <p>
 * Saturation modulates the RGB channels, with stronger B-fields being more saturated.
 * This strategy works only on black backgrounds.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class NeedleColorStrategy {
    
    // Colors for north and south poles
    protected static final Color NORTH_COLOR = FaradayConstants.NORTH_COLOR;
    protected static final Color SOUTH_COLOR = FaradayConstants.SOUTH_COLOR;

    /**
     * Gets a color for the north pole.
     * @param scale 0 to 1
     * @return
     */
    public abstract Color getNorthColor( double scale );

    /**
     * Gets a color for the south pole.
     * @param scale 0 to 1
     * @return
     */
    public abstract Color getSouthColor( double scale );

    /**
     * Factory method that creates an appropriate strategy for a specified background color.
     * 
     * @param backgroundColor
     * @return
     */
    public static NeedleColorStrategy createStrategy( Color backgroundColor ) {
        NeedleColorStrategy strategy = null;
        if ( backgroundColor.equals( Color.BLACK ) ) {
            // use SaturationColorStrategy on black backgrounds
            strategy = new SaturationColorStrategy();
        }
        else {
            // use AlphaColorStrategy on all other backgrounds
            strategy = new AlphaColorStrategy();
        }
        return strategy;
    }
    
    /**
     * AlphaColorStrategy modulates the alpha channel to indicate strength.
     * This strategy works on all backgrounds, but has a performance cost 
     * associated with using the alpha channel. 
     */
    public static class AlphaColorStrategy extends NeedleColorStrategy {

        public Color getNorthColor( double scale ) {
            if ( scale < 0 || scale > 1 ) {
                throw new IllegalArgumentException( "scale must be between 0 and 1: " + scale );
            }
            int r = NORTH_COLOR.getRed();
            int g = NORTH_COLOR.getGreen();
            int b = NORTH_COLOR.getBlue();
            int a = scaleToAlpha( scale );
            return new Color( r, g, b, a );
        }

        public Color getSouthColor( double scale ) {
            if ( scale < 0 || scale > 1 ) {
                throw new IllegalArgumentException( "scale must be between 0 and 1: " + scale );
            }
            int r = SOUTH_COLOR.getRed();
            int g = SOUTH_COLOR.getGreen();
            int b = SOUTH_COLOR.getBlue();
            int a = scaleToAlpha( scale );
            return new Color( r, g, b, a );
        }

        private static int scaleToAlpha( double scale ) {
            return (int) ( 255 * scale );
        }
    }

    /**
     * SaturationColorStrategy modulates the RBG components to indicate strength.
     * This strategy works only on black backgrounds.
     */
    public static class SaturationColorStrategy extends NeedleColorStrategy {

        public Color getNorthColor( double scale ) {
            if ( scale < 0 || scale > 1 ) {
                throw new IllegalArgumentException( "scale must be between 0 and 1: " + scale );
            }
            int r = scaleComponent( scale, NORTH_COLOR.getRed() );
            int g = scaleComponent( scale, NORTH_COLOR.getGreen() );
            int b = scaleComponent( scale, NORTH_COLOR.getBlue() );
            return new Color( r, g, b );
        }

        public Color getSouthColor( double scale ) {
            if ( scale < 0 || scale > 1 ) {
                throw new IllegalArgumentException( "scale must be between 0 and 1: " + scale );
            }
            int r = scaleComponent( scale, SOUTH_COLOR.getRed() );
            int g = scaleComponent( scale, SOUTH_COLOR.getGreen() );
            int b = scaleComponent( scale, SOUTH_COLOR.getBlue() );
            return new Color( r, g, b );
        }

        private static int scaleComponent( double scale, double component ) {
            return (int) ( scale * component );
        }
    }
}