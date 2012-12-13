// Copyright 2002-2012, University of Colorado

/**
 * 2D vector.
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
            function Vector2D( x, y ) {
                this.x = x;
                this.y = y;
            }

            // @return {Number}
            Vector2D.prototype.getAngle = function () {
                return Math.atan2( this.y, this.x );
            };

            // @return {Number}
            Vector2D.prototype.getMagnitude = function () {
                return Math.sqrt( this.x * this.x + this.y * this.y );
            };

            // @return {String}
            Vector2D.prototype.toString = function () {
                return "[Vector2D (x=" + this.x + " y=" + this.y + ")]";
            };

            /**
             * Creates a vector using polar coordinates.
             * @param {Number} magnitude
             * @param {Number} angle in radians
             * @return {Vector$object}
             */
            Vector2D.createPolar = function ( magnitude, angle ) {
                var x = magnitude * Math.cos( angle );
                var y = magnitude * Math.sin( angle );
                return new Vector2D( x, y );
            };

            return Vector2D;
        } );