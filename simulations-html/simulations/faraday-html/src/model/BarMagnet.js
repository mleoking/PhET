// Copyright 2002-2012, University of Colorado

/**
 * Bar magnet model type.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'common/Property' ], function ( Property ) {

    /**
     * @class BarMagnet
     * @constructor
     * @param {Point} location
     * @param {Dimension} size
     * @param {Number} strength in Gauss
     * @param {Number} orientation in radians
     */
    function BarMagnet( location, size, strength, orientation ) {
        this.location = new Property( location );
        this.size = size;
        this.strength = new Property( strength );
        this.orientation = new Property( orientation );
    }

    return BarMagnet;
} );

