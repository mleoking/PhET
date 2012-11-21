// Copyright 2002-2012, University of Colorado

/**
 * Display object for the E-field.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel', 'common/Point2D' ],
        function ( Easel, Point2D ) {

            /**
             * @param {Field} field
             * @param {BarMagnet} barMagnet
             * @param {ModelViewTransform2D} mvt
             * @constructor
             */
            function FieldDisplay( field, barMagnet, mvt ) {

                // constructor stealing
                Easel.Text.call( this, "field", "bold 100px Arial", 'white' );
                this.textAlign = 'center';
                this.textBaseline = 'middle';

                // move to the origin
                var point = mvt.modelToView( new Point2D( 0, 0 ) );
                this.x = point.x;
                this.y = point.y;

                // Register for synchronization with model.
                var that = this;

                // @param {Boolean} visible
                function updateVisibility( visible ) {
                    that.visible = visible;
                    if ( visible ) {
                        updateField();
                    }
                }
                field.visible.addObserver( updateVisibility );

                function updateField() {
                    if ( that.visible ) {
                        //TODO
                    }
                }
                barMagnet.location.addObserver( updateField );
                barMagnet.strength.addObserver( updateField );

                // sync now
                updateVisibility( field.visible.get() );
                updateField();
            }

            // prototype chaining
            FieldDisplay.prototype = new Easel.Text();

            return FieldDisplay;
        } );
