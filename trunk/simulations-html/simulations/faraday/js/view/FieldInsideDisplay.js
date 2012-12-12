// Copyright 2002-2012, University of Colorado

/**
 * Visualization of the E-field inside the bar magnet.
 */
define( [
            'easel',
            'phetcommon/math/Dimension2D',
            'common/Inheritance',
            'phetcommon/math/MathUtil',
            'view/CompassNeedleDisplay'
        ],
        function ( Easel, Dimension2D, Inheritance, MathUtil, CompassNeedleDisplay ) {

            /**
             * @param {BarMagnet} barMagnet
             * @param {ModelViewTransform2D} mvt
             * @param {Dimension2D} needleSize
             * @constructor
             */
            function FieldInsideDisplay( barMagnet, mvt, needleSize ) {

                // constructor stealing
                Easel.Container.call( this );

                // grid of needles, inside the barMagnet's bounds
                {
                    var ROWS = 2;
                    var COLUMNS = 7;

                    var xDelta = barMagnet.size.width / ( COLUMNS + 1 );
                    var xStart = -(barMagnet.size.width / 2) + xDelta; // left
                    var xOffset = xStart;

                    var yDelta = barMagnet.size.height / ROWS;
                    var yStart = -(barMagnet.size.height / 2) + ( yDelta / 2 ); // top
                    var yOffset = yStart;

                    // populate top-to-bottom, left-to-right
                    for ( var row = 0; row < ROWS; row++ ) {
                        xOffset = xStart;
                        for ( var column = 0; column < COLUMNS; column++ ) {
                            var needle = new CompassNeedleDisplay( needleSize );
                            needle.x = xOffset;
                            needle.y = yOffset;
                            this.addChild( needle );
                            xOffset += xDelta;
                        }
                        yOffset += yDelta;
                    }
                }

                // sync with model
                var that = this;
                barMagnet.strength.addObserver( function ( strength ) {
                    // Set the alpha of this container, not the individual needles.
                    that.alpha = strength / barMagnet.strengthRange.max;
                } );
                barMagnet.location.addObserver( function ( /* Point2D */ location ) {
                    var p = mvt.modelToView( location );
                    that.x = p.x;
                    that.y = p.y;
                } );
                barMagnet.orientation.addObserver( function ( orientation /* radians */ ) {
                    that.rotation = MathUtil.toDegrees( orientation );
                } );

                // Cache to improve performance.
                this.cache( -(barMagnet.size.width / 2), -(barMagnet.size.height / 2), barMagnet.size.width, barMagnet.size.height );
            }

            // prototype chaining
            Inheritance.inheritPrototype( FieldInsideDisplay, Easel.Container );

            return FieldInsideDisplay;
        } );
