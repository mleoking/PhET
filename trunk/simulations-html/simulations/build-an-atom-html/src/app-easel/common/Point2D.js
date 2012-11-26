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

            // @return {String}
            Point2D.prototype.toString = function () {
                return "[Point2D (x=" + this.x + " y=" + this.y + ")]";
            };

            return Point2D;
        } );