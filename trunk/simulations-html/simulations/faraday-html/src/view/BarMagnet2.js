// Copyright 2002-2012, University of Colorado

/**
 * Bar magnet display object.
 * Uses the "Combination Inheritance" pattern to extend Easel's Bitmap type.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel', 'image!resources/images/barMagnet.png', 'common/easel-util' ], function ( Easel, barMagnetImage, EaselUtil ) {

    function BarMagnet2() {

        // Use constructor stealing to inherit instance properties.
        Easel.Bitmap.call( this, barMagnetImage );

        // Move registration point to the center.
        this.regX = this.image.width / 2;
        this.regY = this.image.height / 2;

        // Dragging.
        EaselUtil.makeDraggable( this );
    }

    // Use prototype chaining to inherit properties and methods.
    BarMagnet2.prototype = new Easel.Bitmap();

    return BarMagnet2;
} );
