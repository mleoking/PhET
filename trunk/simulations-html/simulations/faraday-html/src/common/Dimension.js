// Copyright 2002-2012, University of Colorado

/**
 * Dimension (size) 2D.
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
            function Dimension( width, height ) {
                this.width = width;
                this.height = height;
            }

            return Dimension;
        } );
