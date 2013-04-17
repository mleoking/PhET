// Copyright 2002-2012, University of Colorado
/**
 * 2D point.
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
            function Point2D( x, y ) {
                this.x = x;
                this.y = y;
            }

            /**
             * Distance between 2 points.
             * @param {Point2D} point
             * @return {number}
             */
            Point2D.prototype.distance = function( point ) {
                var dx = this.x - point.x;
                var dy = this.y - point.y;
                return  Math.sqrt( ( dx * dx ) + ( dy * dy ) );
            }

            // @return {String}
            Point2D.prototype.toString = function () {
                return "[Point2D (x=" + this.x + " y=" + this.y + ")]";
            };

            // @return {Boolean}
            Point2D.prototype.equals = function( object ) {
                return ( object instanceof Point2D) && ( object.x == this.x ) && ( object.y == this.y );
            }

            return Point2D;
        } );