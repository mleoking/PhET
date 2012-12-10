// Copyright 2002-2012, University of Colorado

/**
 * Display object for the B-field outside the magnet, a grid of compass needles.
 * Each needle represents the B-field vector at a specific point.
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
                var grid = [];
                {
                    var SPACING = 20;

                    // delta is the same for both dimensions, because needles rotate
                    var delta = Math.max( needleSize.width, needleSize.height ) + SPACING;

                    var y = delta / 4;
                    while ( y <= canvasSize.height ) {
                        var x = delta / 2;
                        while ( x <= canvasSize.width ) {
                            var needle = new FieldPointDisplay( needleSize, new Point2D( x, y ) );
                            needle.x = x;
                            needle.y = y;
                            this.addChild( needle );
                            grid.push( needle );
                            x += delta;
                        }
                        y += delta;
                    }
                }

                // Register for synchronization with model.
                var that = this;

                this.updateField = function() {
                    if ( that.visible ) {
                        // @param {FieldPointDisplay} item
                        grid.forEach( function ( item ) {
                            var vector = barMagnet.getFieldAt( mvt.viewToModel( item.location ) );
                            item.rotation = MathUtil.toDegrees( vector.getAngle() );
                            item.alpha = Math.min( 1, vector.getMagnitude() / barMagnet.strengthRange.max );
                        } );
                    }
                }
                barMagnet.location.addObserver( this.updateField );
                barMagnet.strength.addObserver( this.updateField );
                barMagnet.orientation.addObserver( this.updateField );
            }

            // prototype chaining
            Inheritance.inheritPrototype( FieldOutsideDisplay, Easel.Container );

            return FieldOutsideDisplay;
        } );
