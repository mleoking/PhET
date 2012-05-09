// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.beerslawlab.concentration.model;

import java.awt.Color;

import edu.colorado.phet.beerslawlab.common.model.Solvent.Water;
import edu.colorado.phet.common.phetcommon.view.util.ColorUtils;

/**
 * Color scheme for relating concentration to color.
 * The scheme also defines the concentration range for the solute, where maxConcentration
 * is synonymous with "saturated".
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

    // Converts a concentration value (in M) to a Color, using a linear interpolation of RGB colors.
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

    public static class DrinkMixColorScheme extends SoluteColorScheme {
        public DrinkMixColorScheme() {
            super( 0, Water.COLOR,
                   0.05, new Color( 255, 225, 225 ),
                   5.96, new Color( 255, 0, 0 ) );
        }
    }

    public static class CobaltIINitrateColorScheme extends SoluteColorScheme {
        public CobaltIINitrateColorScheme() {
            super( 0, Water.COLOR,
                   0.05, new Color( 255, 225, 225 ),
                   5.64, new Color( 255, 0, 0 ) );
        }
    }

    public static class CobaltChlorideColorScheme extends SoluteColorScheme {
        public CobaltChlorideColorScheme() {
            super( 0, Water.COLOR,
                   0.05, new Color( 255, 242, 242 ),
                   4.33, new Color( 255, 106, 106 ) );
        }
    }

    public static class PotassiumDichromateColorScheme extends SoluteColorScheme {
        public PotassiumDichromateColorScheme() {
            super( 0, Water.COLOR,
                   0.01, new Color( 255, 204, 153 ),
                   0.51, new Color( 255, 127, 0 ) );
        }
    }

    public static class PotassiumChromateColorScheme extends SoluteColorScheme {
        public PotassiumChromateColorScheme() {
            super( 0, Water.COLOR,
                   0.05, new Color( 255, 255, 153 ),
                   3.35, new Color( 255, 255, 0 ) );
        }
    }

    public static class NickelIIChlorideColorScheme extends SoluteColorScheme {
        public NickelIIChlorideColorScheme() {
            super( 0, Water.COLOR,
                   0.2, new Color( 170, 255, 170 ),
                   5.21, new Color( 0, 128, 0 ) );
        }
    }

    public static class CopperSulfateColorScheme extends SoluteColorScheme {
        public CopperSulfateColorScheme() {
            super( 0, Water.COLOR,
                   0.2, new Color( 200, 225, 255 ),
                   1.38, new Color( 30, 144, 255 ) );
        }
    }

    public static class PotassiumPermanganateColorScheme extends SoluteColorScheme {
        public PotassiumPermanganateColorScheme() {
            super( 0, Water.COLOR,
                   0.01, new Color( 255, 0, 255 ),
                   0.48, new Color( 80, 0, 120 ) );
        }
    }
}
