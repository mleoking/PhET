// Copyright 2002-2012, University of Colorado

/**
 * 2D dimension.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [],
        function () {

            /**
             * @constructor
             * @param {Number} width
             * @param {Number} height
             */
            function Dimension2D( width, height ) {
                this.width = width;
                this.height = height;
            }

             // @return {String}
            Dimension2D.prototype.toString = function () {
                return "[Dimension2D (width=" + this.width + " height=" + this.height + ")]";
            };

            return Dimension2D;
        } );
