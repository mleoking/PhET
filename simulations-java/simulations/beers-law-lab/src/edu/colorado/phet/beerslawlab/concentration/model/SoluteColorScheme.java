// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.model;

import java.awt.Color;

import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;

/**
 * Color scheme for relating concentration to color.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SoluteColorScheme {

    public final Color minColor, midColor, maxColor;
    public final double minConcentration, midConcentration, maxConcentration;

    public SoluteColorScheme( double minConcentration, Color minColor, double midConcentration, Color midColor, double maxConcentration, Color maxColor ) {
        this.minColor = minColor;
        this.midColor = midColor;
        this.maxColor = maxColor;
        this.minConcentration = minConcentration;
        this.midConcentration = midConcentration;
        this.maxConcentration = maxConcentration;
    }

    public Color interpolateLinear( double concentration ) {
        if ( concentration >= maxConcentration ) {
            return maxColor;
        }
        else if ( concentration <= minConcentration ) {
            return minColor;
        }
        else if ( concentration <= midConcentration ) {
            return ColorUtils.interpolateRBGA( minColor, midColor, ( concentration - minConcentration ) / ( midConcentration - minConcentration ) );
        }
        else {
            return ColorUtils.interpolateRBGA( midColor, maxColor, ( concentration - midConcentration ) / ( maxConcentration - midConcentration ) );
        }
    }

    @Override public String toString() {
        return minConcentration + "," + minColor + "," + midConcentration + "," + midColor + "," + maxConcentration + "," + maxColor;
    }

    //TODO saturated concentration values are duplicated here and in Solute (eg, 5.96) for all solutes and color schemes
    public static class DrinkMixColorScheme extends SoluteColorScheme {
        public DrinkMixColorScheme() {
            super( 0, Color.WHITE,
                   0.1, new Color( 255, 225, 225 ),
                   5.96, Color.RED );
        }
    }

    public static class CobaltIINitrateColorScheme extends SoluteColorScheme {
        public CobaltIINitrateColorScheme() {
            super( 0, Color.WHITE,
                   0.1, new Color( 255, 225, 225 ),
                   5.64, Color.RED );
        }
    }

    public static class CobaltChlorideColorScheme extends SoluteColorScheme {
        public CobaltChlorideColorScheme() {
            super( 0, Color.WHITE,
                   0.1, new Color( 255, 242, 242 ),
                   4.33, new Color( 255, 106, 106 ) );
        }
    }

    public static class PotassiumDichromateColorScheme extends SoluteColorScheme {
        public PotassiumDichromateColorScheme() {
            super( 0, Color.WHITE,
                   0.1, new Color( 255, 232, 210 ),
                   0.51, new Color( 255, 127, 0 ) );
        }
    }

    public static class PotassiumChromateColorScheme extends SoluteColorScheme {
        public PotassiumChromateColorScheme() {
            super( 0, Color.WHITE,
                   0.1, new Color( 255, 255, 199 ),
                   3.35, Color.YELLOW );
        }
    }

    public static class NickelIIChlorideColorScheme extends SoluteColorScheme {
        public NickelIIChlorideColorScheme() {
            super( 0, Color.WHITE,
                   0.1, new Color( 234, 244, 234 ),
                   5.21, new Color( 0, 128, 0 ) );
        }
    }

    public static class CopperSulfateColorScheme extends SoluteColorScheme {
        public CopperSulfateColorScheme() {
            super( 0, Color.WHITE,
                   0.1, new Color( 222, 238, 255 ),
                   1.38, new Color( 30, 144, 255 ) );
        }
    }

    public static class PotassiumPermanganateColorScheme extends SoluteColorScheme {
        public PotassiumPermanganateColorScheme() {
            super( 0, new Color( 255, 235, 255 ),
                   0.2, new Color( 255, 0, 255 ),
                   0.48, new Color( 139, 0, 130 ) );
        }
    }
}
