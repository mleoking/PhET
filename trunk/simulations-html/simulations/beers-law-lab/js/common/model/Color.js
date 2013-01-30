// Copyright 2002-2013, University of Colorado

/**
 * RGBA color.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
define( [
            'easel',
            'common/util/StringUtils'
        ],
        function ( Easel, StringUtils ) {

            /**
             * Constructor.
             * @param r red color component, 0-255
             * @param g green color component, 0-255
             * @param b blue color component, 0-255
             * @param a alpha color component, 0-1, optional (defaults to 1)
             * @constructor
             */
            function Color( r, g, b, a ) {
                //TODO validate args
                this.r = r;
                this.g = g;
                this.b = b;
                this.a = a || 1;
            }

            // Gets a CSS-compatible color string.
            Color.prototype.toCSS = function () {
                return Easel.Graphics.getRGB( this.r, this.g, this.b, this.a );
            }

            /**
             * Interpolates between 2 colors in RGBA space. When distance is 0, color1
             * is returned. When distance is 1, color2 is returned. Other values of
             * distance return a color somewhere between color1 and color2. Each color
             * component is interpolated separately.
             *
             * @param {Color} color1
             * @param {Color} color2
             * @param {Number} distance distance between color1 and color2, 0 <= distance <= 1
             */
            Color.interpolateRBGA = function ( color1, color2, distance ) {
                //TODO validate distance
                var r = Color.interpolate( color1.r, color2.r, distance );
                var g = Color.interpolate( color1.g, color2.g, distance );
                var b = Color.interpolate( color1.b, color2.b, distance );
                var a = Color.interpolate( color1.a, color2.a, distance );
                return new Color( r, g, b, a );
            }

            /*
             * Interpolates between 2 numbers.
             * @param {Number} number1
             * @param {Number} number2
             * @param {Number} distance distance between number1 and number2, 0 <= distance <= 1
             * @return value, such that number1 <= value <= number2
             */
            Color.interpolate = function ( number1, number2, distance ) {
                //TODO validate distance
                return number1 + ( distance * ( number2 - number1 ) );
            }

            return Color;
        } );
