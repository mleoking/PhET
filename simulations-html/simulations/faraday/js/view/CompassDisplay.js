// Copyright 2002-2012, University of Colorado

/**
 * Display object for the compass.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [
            'easel',
            'phetcommon/math/Dimension2D',
            'common/DragHandler',
            'common/Inheritance',
            'phetcommon/math/MathUtil',
            'phetcommon/math/Vector2D',
            'view/CompassNeedleDisplay'
        ],
        function ( Easel, Dimension2D, DragHandler, Inheritance, MathUtil, Vector2D, CompassNeedleDisplay ) {

            /**
             * @param {Compass} compass
             * @param {ModelViewTransform2D} mvt
             * @constructor
             */
            function CompassDisplay( compass, mvt ) {

                // constructor stealing
                Easel.Container.call( this );

                var OUTSIDE_RADIUS = 40;
                var RING_THICKNESS = 10;

                // the body of the compass, the part that doesn't change
                var body = new Easel.Container();
                this.addChild( body );

                // ring
                var ring = new Easel.Shape();
                ring.graphics
                        .beginFill( Easel.Graphics.getRGB( 0, 0, 0, 0.05 ) )
                        .setStrokeStyle( RING_THICKNESS )
                        .beginStroke( Easel.Graphics.getRGB( 153, 153, 153 ) )
                        .drawCircle( 0, 0, OUTSIDE_RADIUS - ( RING_THICKNESS / 2 ) );
                body.addChild( ring );

                // indicators on the ring, spaced 45-degrees apart
                var angle = 0;
                while ( angle < 360 ) {

                    var vector = Vector2D.createPolar( OUTSIDE_RADIUS - ( RING_THICKNESS / 2 ), MathUtil.toRadians( angle ) );

                    var indicator = new Easel.Shape();
                    indicator.graphics
                            .beginFill( 'black' )
                            .setStrokeStyle( 0 )
                            .drawCircle( vector.x, vector.y, 3 );
                    body.addChild( indicator );

                    angle += 45;
                }

                // needle
                var needle = new CompassNeedleDisplay( new Dimension2D( 50, 14 ) );
                this.addChild( needle );

                // center pin
                var pin = new Easel.Shape();
                pin.graphics
                        .beginFill( 'black' )
                        .setStrokeStyle( 0 )
                        .drawCircle( 0, 0, 3 );
                this.addChild( pin );

                // @param {Point2D} point
                DragHandler.register( this, function ( point ) {
                    compass.location.set( mvt.viewToModel( point ) );
                } );

                // Register for synchronization with model.
                var that = this;
                compass.location.addObserver( function updateLocation( /* Point2D */ location ) {
                    var point = mvt.modelToView( location );
                    that.x = point.x;
                    that.y = point.y;
                } );
                compass.orientation.addObserver( function updateOrientation( orientation /* radians */ ) {
                    needle.rotation = MathUtil.toDegrees( compass.orientation.get() );
                } );
                compass.visible.addObserver( function updateVisibility( visible ) {
                    that.visible = visible;
                } );

                // Cache the part of the compass that doesn't change, to improve performance.
                body.cache( -OUTSIDE_RADIUS, -OUTSIDE_RADIUS, 2 * OUTSIDE_RADIUS, 2 * OUTSIDE_RADIUS );
            }

            // prototype chaining
            Inheritance.inheritPrototype( CompassDisplay, Easel.Container );

            return CompassDisplay;
        } );
