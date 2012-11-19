// Copyright 2002-2012, University of Colorado

/**
 * Bar magnet model type.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'common/Property' ], function ( Property, assert ) {

    function BarMagnet( x, y, strength ) {
        this.x = new Property( x );
        this.y = new Property( y );
        this.strength = new Property( strength );
    }

    return BarMagnet;
} );

