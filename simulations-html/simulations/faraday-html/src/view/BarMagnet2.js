// Copyright 2002-2012, University of Colorado

/**
 * Bar magnet display object.
 *
 * Uses the "Combination Inheritance" pattern to extend Easel's Bitmap type.
 * Combination Inheritance combines 2 other patterns, "Constructor Stealing" and "Prototype Chaining",
 * and is reportedly "the most frequently used inheritance pattern in JavaScript".
 * The most significant downside of this pattern is that the supertype's constructor is called twice:
 * once inside the subtype's constructor, and once to to create the subtype's prototype.
 * (Professional JavaScript for Web Developers, Zakas, Wrox Press, p 209-210.)
 *
 * Usage: var barMagnet = new BarMagnet2();
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

    // Use prototype chaining to inherit properties and methods on the prototype.
    BarMagnet2.prototype = new Easel.Bitmap();

    return BarMagnet2;
} );
