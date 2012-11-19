// Copyright 2002-2012, University of Colorado

/**
 * Bar magnet model type.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'common/Point2D', 'common/Property' ], function ( Point2D, Property ) {

    // {Point2D} location, {Number} strength
    function BarMagnet( location, strength ) {
        this.location = new Property( location );
        this.strength = new Property( strength );
    }

    return BarMagnet;
} );

