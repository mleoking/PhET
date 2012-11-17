// Copyright 2002-2012, University of Colorado

/**
 * Bar magnet model type.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [], function () {

    //TODO x,y should be a Point2D or Vector2D
    //TODO minStrength,maxStrength should be a Range, immutable, read-only
    function BarMagnet( x, y, strength, minStrength, maxStrength ) {
        this.x = x;
        this.y = y;
        this.strength = strength;
        this.minStrength = minStrength;
        this.maxStrength = maxStrength;
    }

    return BarMagnet;
} );
