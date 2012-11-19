// Copyright 2002-2012, University of Colorado

/**
 * Immutable dimension (size).
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [], function () {

    /**
     * @class Dimension
     * @constructor
     * @param {Number} width
     * @param {Number} height
     */
    function Dimension( width, height ) {
        this.getWidth = function() { return width; };
        this.getHeight = function() { return height; };
    }

    return Dimension;
} );
