// Copyright 2002-2012, University of Colorado

/**
 * Bar magnet display object.
 *
 * Uses a factory pattern to instantiate and config an Easel Bitmap.
 *
 * Usage: var barMagnet = createBarMagnet();
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel', 'image!resources/images/barMagnet.png', 'common/easel-util' ], function ( Easel, barMagnetImage, EaselUtil ) {

    function createBarMagnet() {

        // create a bitmap display object
        var barMagnet = new Easel.Bitmap( barMagnetImage );

        // move registration point to the center
        barMagnet.regX = barMagnet.image.width / 2;
        barMagnet.regY = barMagnet.image.height / 2;

        return barMagnet;
    }

    return createBarMagnet;
} );
