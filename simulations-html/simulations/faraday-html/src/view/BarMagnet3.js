// Copyright 2002-2012, University of Colorado

/**
 * Bar magnet display object.
 *
 * Uses the "Parasitic Combination Inheritance" pattern to extend Easel's Bitmap type.
 * Similar advantages to "Combination Inheritance", but more optimal in that it involves
 * only one call to the supertype's constructor.
 * (Professional JavaScript for Web Developers, Zakas, Wrox Press, p 212-214.)
 *
 * Usage: var barMagnet = new BarMagnet3();
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel', 'image!resources/images/barMagnet.png', 'common/easel-util' ], function ( Easel, barMagnetImage, EaselUtil ) {

    //TODO move this method to common code
    // Performs parasitic inheritance
    function inheritPrototype( subType, superType ) {
        var prototype = Object( superType.prototype );
        prototype.constructor = subType;
        subType.prototype =  prototype;
    }

    function BarMagnet3() {

        // Use constructor stealing to inherit instance properties.
        Easel.Bitmap.call( this, barMagnetImage );

        // Move registration point to the center.
        this.regX = this.image.width / 2;
        this.regY = this.image.height / 2;

        // Dragging.
        EaselUtil.makeDraggable( this );
    }

    inheritPrototype( BarMagnet3, Easel.Bitmap );

    return BarMagnet3;
} );
