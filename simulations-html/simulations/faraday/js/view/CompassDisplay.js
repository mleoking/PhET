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
            'common/Inheritance',
            'common/MathUtil',
            'common/Vector2D',
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

                var outsideRadius = 40;
                var ringThickness = 10;

                // ring
                var ring = new Easel.Shape();
                ring.graphics
                        .beginFill( Easel.Graphics.getRGB( 0, 0, 0, 0.05 ) )
                        .setStrokeStyle( ringThickness )
                        .beginStroke( Easel.Graphics.getRGB( 153, 153, 153 ) )
                        .drawCircle( 0, 0, outsideRadius - ( ringThickness / 2 ) );
                this.addChild( ring );

                // indicators on the ring
                var angle = 0;
                while ( angle < 360 ) {

                    var vector = Vector2D.createPolar( outsideRadius - ( ringThickness / 2 ), MathUtil.toRadians( angle ) );

                    var indicator = new Easel.Shape();
                    indicator.graphics
                            .beginFill( 'black' )
                            .setStrokeStyle( 0 )
                            .drawCircle( vector.x, vector.y, 3 );
                    this.addChild( indicator );

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
            }

            // prototype chaining
            Inheritance.inheritPrototype( CompassDisplay, Easel.Container );

            return CompassDisplay;
        } );
