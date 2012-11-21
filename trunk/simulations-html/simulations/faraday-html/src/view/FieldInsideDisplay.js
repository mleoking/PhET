// Copyright 2002-2012, University of Colorado

/**
 * Visualization of the E-field inside the bar magnet.
 */
define( [
            'easel',
            'common/Dimension2D',
            'common/MathUtil',
            'view/CompassNeedleDisplay'
        ],
        function ( Easel, Dimension2D, MathUtil, CompassNeedleDisplay ) {

            /**
             * @param {BarMagnet} magnet
             * @param {ModelViewTransform2D} mvt
             * @constructor
             */
            function FieldInsideDisplay( magnet, mvt ) {

                // constructor stealing
                Easel.Container.call( this );

                // grid of needles, inside the magnet's bounds
                {
                    var NEEDLE_SIZE = new Dimension2D( 25, 7 );

                    var ROWS = 2;
                    var COLUMNS = 7;

                    var xDelta = magnet.size.width / ( COLUMNS + 1 );
                    var xStart = -(magnet.size.width / 2) + xDelta; // left
                    var xOffset = xStart;

                    var yDelta = magnet.size.height / ROWS;
                    var yStart = -(magnet.size.height / 2) + ( yDelta / 2 ); // top
                    var yOffset = yStart;

                    // populate top-to-bottom, left-to-right
                    for ( var row = 0; row < ROWS; row++ ) {
                        xOffset = xStart;
                        for ( var column = 0; column < COLUMNS; column++ ) {
                            var needle = new CompassNeedleDisplay( NEEDLE_SIZE, 0, 1 );
                            needle.x = xOffset;
                            needle.y = yOffset;
                            this.addChild( needle );
                            xOffset += xDelta;
                        }
                        yOffset += yDelta;
                    }
                }

                var that = this;

                // @param {Number} strength
                var updateStrength = function( strength ) {
                    that.alpha = 1; //TODO alpha = strength/maxStrength
                }
                magnet.strength.addObserver( updateStrength );

                // @param {Point2D} location
                var updateLocation = function( location ) {
                    var p = mvt.modelToView( location );
                    that.x = p.x;
                    that.y = p.y;
                };
                magnet.location.addObserver( updateLocation );

                // @param {Number} orientation in radians
                var updateOrientation = function ( orientation ) {
                    that.rotation = MathUtil.toDegrees( orientation );
                };
                magnet.orientation.addObserver( updateOrientation );

                // update now
                updateStrength( magnet.strength.get() );
                updateLocation( magnet.location.get() );
                updateOrientation( magnet.orientation.get() );
            }

            // prototype chaining
            FieldInsideDisplay.prototype = new Easel.Container();

            return FieldInsideDisplay;
        } );
