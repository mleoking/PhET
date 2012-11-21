// Copyright 2002-2012, University of Colorado

/**
 * Immutable 2D vector.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [],
        function () {

            /**
             * @param {Number} x
             * @param {Number} y
             * @constructor
             */
            function Vector( x, y ) {

                // Cartesian coordinates
                this.getX = function () {
                    return x;
                };
                this.getY = function () {
                    return y;
                };

                // Polar coordinates
                this.getAngle = function () {
                    return Math.atan2( y, x );
                };
                this.getMagnitude = function () {
                    return Math.sqrt( x * x + y * y );
                };
            }

            Vector.prototype.toString = function () {
                return "[Vector (x=" + this.getX() + " y=" + this.getY() + ")]";
            };

            /**
             * Creates a vector using polar coordinates.
             * @param {Number} magnitude
             * @param {Number} angle in radians
             * @return {Vector$object}
             */
            Vector.createPolar = function ( magnitude, angle ) {
                var x = magnitude * Math.cos( angle );
                var y = magnitude * Math.sin( angle );
                return new Vector( x, y );
            };

            return Vector;
        } );
