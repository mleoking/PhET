// Copyright 2002-2012, University of Colorado

define( [],
        function () {

            // Not meant to be instantiated.
            var Utils = { };

            // @return {String}
            Utils.distanceBetweenPoints = function ( x1, y1, x2, y2 ) {
                return Math.sqrt( ( x1 - x2 ) * ( x1 - x2 ) + ( y1 - y2 ) * ( y1 - y2 ) );
            };

            return Utils;
        } );