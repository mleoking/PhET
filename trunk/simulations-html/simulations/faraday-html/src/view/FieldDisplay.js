// Copyright 2002-2012, University of Colorado

/**
 * Display object for the B-field, a grid of compass needles.
 * Each needle represents the B-field vector at a specific point.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [
            'easel',
            'common/Dimension2D',
            'common/Point2D',
            'common/MathUtil',
            'view/FieldPointDisplay'
        ],
        function ( Easel, Dimension2D, Point2D, MathUtil, FieldPointDisplay ) {

            /**
             * @param {Field} field
             * @param {BarMagnet} barMagnet
             * @param {ModelViewTransform2D} mvt
             * @param {Dimension2D} canvasSize
             * @constructor
             */
            function FieldDisplay( field, barMagnet, mvt, canvasSize ) {

                // constructor stealing
                Easel.Container.call( this );

                // create a grid of compass needles
                var grid = [];
                {
                    var NEEDLE_SIZE = new Dimension2D( 25, 7 ); //TODO duplicated from FieldInsideDisplay
                    var X_SPACING = 20;
                    var Y_SPACING = 20;

                    var deltaX = 100; //TODO compute
                    var deltaY = 100; //TODO compute

                    var y = deltaY / 4;
                    while ( y <= canvasSize.height ) {
                        var x = deltaX / 2;
                        while ( x <= canvasSize.width ) {
                            var needle = new FieldPointDisplay( NEEDLE_SIZE, new Point2D( x, y ) );
                            needle.x = x;
                            needle.y = y;
                            this.addChild( needle );
                            grid.push( needle );
                            x += deltaX;
                        }
                        y += deltaY;
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
                            item.alpha = barMagnet.strength.get() / barMagnet.strengthRange.max;
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
            FieldDisplay.prototype = new Easel.Container();

            return FieldDisplay;
        } );
