// Copyright 2002-2012, University of Colorado

define( [], function () {

    // Not meant to be instantiated.
    var Utils = { };

    /**
     * Returns the distance between two points, p1 and p2.
     *
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     */
    Utils.distanceBetweenPoints = function ( x1, y1, x2, y2 ) {
        return Math.sqrt( ( x1 - x2 ) * ( x1 - x2 ) + ( y1 - y2 ) * ( y1 - y2 ) );
    };

    return Utils;
} );