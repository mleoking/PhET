// Copyright 2002-2012, University of Colorado

/**
 * Display object for the B-field outside the magnet, a grid of compass needles.
 * Each needle represents the B-field vector at a specific point.
 * Assumes that the origin is at the center of the canvas, and resizes itself
 * to fill the canvas.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [
            'easel',
            'phetcommon/math/Dimension2D',
            'common/Inheritance',
            'phetcommon/math/Point2D',
            'phetcommon/math/MathUtil',
            'view/FieldPointDisplay'
        ],
        function ( Easel, Dimension2D, Inheritance, Point2D, MathUtil, FieldPointDisplay ) {

            /**
             * @param {BarMagnet} barMagnet
             * @param {ModelViewTransform2D} mvt
             * @param {Dimension2D} canvasSize
             * @param {Dimension2D} needleSize
             * @constructor
             */
            function FieldOutsideDisplay( barMagnet, mvt, canvasSize, needleSize ) {

                // constructor stealing
                Easel.Container.call( this );

                // create a grid of compass needles
                var needles = [];
                var that = this;
                this.resize = function ( canvasSize ) {

                    that.removeAllChildren();
                    needles.length = 0; // clears the array

                    var SPACING = 20;

                    // delta is the same for both dimensions, because needles rotate
                    var delta = Math.max( needleSize.width, needleSize.height ) + SPACING;

                    var gridWidth = canvasSize.width + delta;
                    var gridHeight = canvasSize.height + delta;
                    var y = -gridHeight / 2;
                    while ( y <= gridHeight / 2 ) {
                        var x = -gridWidth / 2;
                        while ( x <= gridWidth / 2 ) {
                            var needle = new FieldPointDisplay( needleSize, new Point2D( x, y ) );
                            needle.x = x;
                            needle.y = y;
                            this.addChild( needle );
                            needles.push( needle );
                            x += delta;
                        }
                        y += delta;
                    }

                    this.updateField();
                };

                // Register for synchronization with model.
                this.updateField = function() {
                    if ( that.visible ) {
                        // @param {FieldPointDisplay} item
                        needles.forEach( function ( needle ) {
                            var vector = barMagnet.getFieldAt( mvt.viewToModel( needle.location ) );
                            needle.rotation = MathUtil.toDegrees( vector.getAngle() );
                            needle.alpha = Math.min( 1, vector.getMagnitude() / barMagnet.strengthRange.max );
                        } );
                    }
                }
                barMagnet.location.addObserver( this.updateField );
                barMagnet.strength.addObserver( this.updateField );
                barMagnet.orientation.addObserver( this.updateField );

                this.resize( canvasSize ); // initial size
            }

            // prototype chaining
            Inheritance.inheritPrototype( FieldOutsideDisplay, Easel.Container );

            return FieldOutsideDisplay;
        } );
