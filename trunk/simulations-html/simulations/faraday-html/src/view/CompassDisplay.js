// Copyright 2002-2012, University of Colorado

/**
 * Display object for the compass.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [
            'easel',
            'common/Dimension2D',
            'common/DragHandler',
            'common/MathUtil',
            'common/Vector2D',
            'view/CompassNeedleDisplay'
        ],
        function ( Easel, Dimension2D, DragHandler, MathUtil, Vector2D, CompassNeedleDisplay ) {

            /**
             * @param {Compass} compass
             * @param {ModelViewTransform2D} mvt
             * @constructor
             */
            function CompassDisplay( compass, mvt ) {

                // constructor stealing
                Easel.Container.call( this );

                var outsideRadius = 40;
                var ringThickness = 10;

                // ring
                var ring = new Easel.Shape();
                ring.graphics.beginFill( Easel.Graphics.getRGB( 0, 0, 0, 0.05 ) ); // transparent
                ring.graphics.setStrokeStyle( ringThickness );
                ring.graphics.beginStroke( Easel.Graphics.getRGB( 153, 153, 153 ) ); // gray
                ring.graphics.drawCircle( 0, 0, outsideRadius - ( ringThickness / 2 ) );
                this.addChild( ring );

                // indicators on the ring
                var angle = 0;
                while ( angle < 360 ) {

                    var vector = Vector2D.createPolar( outsideRadius - ( ringThickness / 2 ), MathUtil.toRadians( angle ) );

                    var indicator = new Easel.Shape();
                    indicator.graphics.beginFill( 'black' );
                    indicator.graphics.setStrokeStyle( 0 );
                    indicator.graphics.drawCircle( vector.x, vector.y, 3 );
                    this.addChild( indicator );

                    angle += 45;
                }

                // needle
                var needle = new CompassNeedleDisplay( new Dimension2D( 50, 14 ) );
                this.addChild( needle );

                // center pin
                var pin = new Easel.Shape();
                pin.graphics.beginFill( 'black' );
                pin.graphics.setStrokeStyle( 0 );
                pin.graphics.drawCircle( 0, 0, 3 );
                this.addChild( pin );

                // @param {Point2D} point
                DragHandler.register( this, function ( point ) {
                    compass.location.set( mvt.viewToModel( point ) );
                } );

                // Register for synchronization with model.
                var that = this;

                // @param {Point2D} location
                function updateLocation( location ) {
                    var point = mvt.modelToView( location );
                    that.x = point.x;
                    that.y = point.y;
                }
                compass.location.addObserver( updateLocation );

                // @param {Number} orientation
                function updateOrientation( orientation ) {
                    needle.rotation = MathUtil.toDegrees( compass.orientation.get() );
                }
                compass.orientation.addObserver( updateOrientation );

                // @param {Boolean} visible
                function updateVisibility( visible ) {
                    that.visible = visible;
                }
                compass.visible.addObserver( updateVisibility );

                // sync now
                updateLocation( compass.location.get() );
                updateOrientation( compass.orientation.get() );
                updateVisibility( compass.visible.get() );
            }

            // prototype chaining
            CompassDisplay.prototype = new Easel.Container();

            return CompassDisplay;
        } );
