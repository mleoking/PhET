// Copyright 2002-2012, University of Colorado

/**
 * Display object for the bar magnet.
 * <p>
 * Uses the "Combination Inheritance" pattern to extend Easel's Bitmap type.
 * Combination Inheritance combines 2 other patterns, "Constructor Stealing" and "Prototype Chaining",
 * and is reportedly "the most frequently used inheritance pattern in JavaScript".
 * The most significant downside of this pattern is that the supertype's constructor is called twice:
 * once inside the subtype's constructor, and once to to create the subtype's prototype.
 * (Professional JavaScript for Web Developers, Zakas, Wrox Press, p 209-210.)
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [
            'easel',
            'common/DragHandler',
            'common/Inheritance',
            'common/MathUtil',
            'image!images/barMagnet.png'
        ],
        function ( Easel, DragHandler, Inheritance, MathUtil, barMagnetImage ) {

            /**
             * @class BarMagnetDisplay
             * @constructor
             * @param {BarMagnet} barMagnet
             * @param {ModelViewTransform2D} mvt
             */
            function BarMagnetDisplay( barMagnet, mvt ) {

                // Use constructor stealing to inherit instance properties.
                Easel.Bitmap.call( this, barMagnetImage );

                // Compute scale factors to match model.
                this.scaleX = mvt.modelToView( barMagnet.size.width ) / this.image.width;
                this.scaleY = mvt.modelToView( barMagnet.size.height ) / this.image.height;

                // Move registration point to the center.
                this.regX = this.image.width / 2;
                this.regY = this.image.height / 2;

                // @param {Point2D} point
                DragHandler.register( this, function ( point ) {
                    barMagnet.location.set( mvt.viewToModel( point ) );
                } );

                // Register for synchronization with model.
                var that = this;

                // @param {Point2D} location
                function updateLocation( location ) {
                    var point = mvt.modelToView( location );
                    that.x = point.x;
                    that.y = point.y;
                }

                barMagnet.location.addObserver( updateLocation );

                // @param {Number} orientation in radians
                function updateOrientation( orientation ) {
                    that.rotation = MathUtil.toDegrees( orientation );
                }

                barMagnet.orientation.addObserver( updateOrientation );

                // sync now
                updateLocation( barMagnet.location.get() );
                updateOrientation( barMagnet.orientation.get() );
            }

            // prototype chaining
            Inheritance.inheritPrototype( BarMagnetDisplay, Easel.Bitmap );

            return BarMagnetDisplay;
        } );
