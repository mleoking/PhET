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
define( [ 'easel',
          'common/easel-util',
          'model/BarMagnet',
          'image!resources/images/barMagnet.png'
        ],
        function ( Easel, EaselUtil, BarMagnet, barMagnetImage ) {

    function BarMagnet3( /* BarMagnet */ magnet ) {

        // Use constructor stealing to inherit instance properties.
        Easel.Bitmap.call( this, barMagnetImage );

        // Move registration point to the center.
        this.regX = this.image.width / 2;
        this.regY = this.image.height / 2;

        // Dragging.
        EaselUtil.makeDraggable( this );

        // sync with model
        this.x = magnet.x.get(); //TODO mvt
        this.y = magnet.y.get(); //TODO mvt
    }

    //TODO move this method to common code
    // Performs parasitic inheritance
    function inheritPrototype( subType, superType ) {
        var prototype = Object( superType.prototype );
        prototype.constructor = subType;
        subType.prototype = prototype;
    }

    inheritPrototype( BarMagnet3, Easel.Bitmap );

    return BarMagnet3;
} );
