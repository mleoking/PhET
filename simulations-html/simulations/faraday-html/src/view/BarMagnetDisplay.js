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
define( [ 'easel',
          'view/DragHandler',
          'image!resources/images/barMagnet.png'
        ],
        function ( Easel, DragHandler, barMagnetImage ) {

    /**
     * @class BarMagnetDisplay
     * @constructor
     * @param {BarMagnet} barMagnet
     * @param {ModelViewTransform} mvt
     */
    function BarMagnetDisplay( barMagnet, mvt ) {

        // Use constructor stealing to inherit instance properties.
        Easel.Bitmap.call( this, barMagnetImage );

        // Compute scale factors to match model.
        this.scaleX = mvt.modelToViewScalar( barMagnet.size.getWidth() ) / this.image.width;
        this.scaleY = mvt.modelToViewScalar( barMagnet.size.getHeight() ) / this.image.height;

        // Move registration point to the center.
        this.regX = this.image.width / 2;
        this.regY = this.image.height / 2;

         // Dragging.
        new DragHandler( this, function( point ) {
            barMagnet.location.set( mvt.viewToModel( point ) );
        });

        // sync with model
        var thisDisplayObject = this;
        function updateLocation( location ) {
            var point = mvt.modelToView( location );
            thisDisplayObject.x = point.x;
            thisDisplayObject.y = point.y;
        }

        barMagnet.location.addObserver( updateLocation );
        updateLocation( barMagnet.location.get() );
    }

    // Use prototype chaining to inherit properties and methods on the prototype.
    BarMagnetDisplay.prototype = new Easel.Bitmap();

    return BarMagnetDisplay;
} );
