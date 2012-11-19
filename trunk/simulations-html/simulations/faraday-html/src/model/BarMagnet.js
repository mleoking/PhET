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
     * @param {Number} strength
     */
    function BarMagnet( location, strength ) {
        this.location = new Property( location );
        this.strength = new Property( strength );
    }

    return BarMagnet;
} );

