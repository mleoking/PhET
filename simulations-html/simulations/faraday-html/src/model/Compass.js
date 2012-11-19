// Copyright 2002-2012, University of Colorado

/**
 * Compass model type.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'common/Property' ], function( Property ) {

    function Compass( location ) {
        this.location = new Property( location );
    }

    return Compass;
} );
