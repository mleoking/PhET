// Copyright 2002-2012, University of Colorado

/**
 * Display object for the B-field outside the magnet, a grid of compass needles.
 * Each needle represents the B-field vector at a specific point.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [
            'easel',
            'common/Dimension2D',
            'common/Inheritance',
            'common/Point2D',
            'common/MathUtil',
            'view/FieldPointDisplay'
        ],
        function ( Easel, Dimension2D, Inheritance, Point2D, MathUtil, FieldPointDisplay ) {

            /**
             * @param {Field} field
             * @param {BarMagnet} barMagnet
             * @param {ModelViewTransform2D} mvt
             * @param {Dimension2D} canvasSize
             * @param {Dimension2D} needleSize
             * @constructor
             */
            function FieldOutsideDisplay( field, barMagnet, mvt, canvasSize, needleSize ) {

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

                function updateField() {
                    if ( that.visible ) {
                        // @param {FieldPointDisplay} item
                        grid.forEach( function ( item ) {
                            var vector = barMagnet.getFieldVector( mvt.viewToModel( item.location ) );
                            item.rotation = MathUtil.toDegrees( vector.getAngle() );
                            item.alpha = Math.min( 1, barMagnet.strength.get() / barMagnet.strengthRange.max );
                        } );
                    }
                }
                barMagnet.location.addObserver( updateField );
                barMagnet.strength.addObserver( updateField );
                barMagnet.orientation.addObserver( updateField );

                // @param {Boolean} visible
                function updateVisibility( visible ) {
                    that.visible = visible;
                    if ( visible ) {
                        updateField();
                    }
                }
                field.visible.addObserver( updateVisibility );

                // sync now
                updateVisibility( field.visible.get() );
                updateField();
            }

            // prototype chaining
            Inheritance.inheritPrototype( FieldOutsideDisplay, Easel.Container );

            return FieldOutsideDisplay;
        } );
