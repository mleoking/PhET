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
            function Point2D( x, y ) {
                this.x = x;
                this.y = y;
            }

            return Point2D;
        } );