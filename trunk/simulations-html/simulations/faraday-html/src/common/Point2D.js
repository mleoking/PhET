// Copyright 2002-2012, University of Colorado

/**
 * An immutable 2D point.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [], function() {

    function Point2D( x, y ) {
        this.getX = function() { return x; };
        this.getY = function() { return y; };
    }

    Point2D.prototype.toString = function() {
        return "x=" + this.x + ", y=" + this.y;
    };

    return Point2D;
});
