// Copyright 2002-2012, University of Colorado

/**
 * Range.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [],
        function () {

            function Range( min, max ) {
                this.min = min;
                this.max = max;
            }

            // @return {String}
            Range.prototype.toString = function () {
                return "[Range (min=" + this.min + " max=" + this.max + ")]";
            };

            return Range;
        } );
