// Copyright 2002-2013, University of Colorado

/**
 * Color scheme for relating concentration to color.
 * The scheme also defines the concentration range for the solute, where maxConcentration
 * is synonymous with "saturated".
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
define( [
            'common/model/Color'
        ],
        function ( Color ) {

            function SoluteColorScheme( minConcentration, minColor, midConcentration, midColor, maxConcentration, maxColor ) {
                this.minColor = minColor;
                this.midColor = midColor;
                this.maxColor = maxColor;
                this.minConcentration = minConcentration;
                this.midConcentration = midConcentration;
                this.maxConcentration = maxConcentration;
            };

            /**
             * Converts a concentration value (in M) to a Color, using a linear interpolation of RGB colors.
             * @param {Number} concentration
             * @return {Color} color
             */
            SoluteColorScheme.prototype.interpolateLinear = function ( concentration ) {
                if ( concentration >= this.maxConcentration ) {
                    return this.maxColor;
                }
                else if ( concentration <= this.minConcentration ) {
                    return this.minColor;
                }
                else if ( concentration <= this.midConcentration ) {
                    return Color.interpolateRBGA( this.minColor, this.midColor, ( concentration - this.minConcentration ) / ( this.midConcentration - this.minConcentration ) );
                }
                else {
                    return Color.interpolateRBGA( this.midColor, this.maxColor, ( concentration - this.midConcentration ) / ( this.maxConcentration - this.midConcentration ) );
                }
            }

            return SoluteColorScheme;
        } );
