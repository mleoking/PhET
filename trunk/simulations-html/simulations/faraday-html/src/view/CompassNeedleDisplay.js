// Copyright 2002-2012, University of Colorado

/**
 * A compass needle.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel', 'common/Dimension' ],
        function ( Easel, Dimension ) {

            /**
             * @param {Dimension} size
             * @param {Number} orientation in degrees
             * @param {Number} alpha 0 (transparent) to 1 (opaque)
             * @constructor
             */
            function CompassNeedleDisplay( size, orientation, alpha ) {

                // constructor stealing
                Easel.Container.call( this );

                var northShape = new Easel.Shape();
                northShape.graphics.beginFill( Easel.Graphics.getRGB( 255, 0, 0, alpha ) ); // red
                northShape.graphics.moveTo( 0, -size.height / 2 )
                        .lineTo( size.width / 2, 0 )
                        .lineTo( 0, size.height / 2 )
                        .closePath();

                var southShape = new Easel.Shape();
                southShape.graphics.beginFill( Easel.Graphics.getRGB( 255, 255, 255, alpha ) ); // white
                southShape.graphics.moveTo( 0, -size.height / 2 )
                        .lineTo( -size.width / 2, 0 )
                        .lineTo( 0, size.height / 2 )
                        .closePath();

                this.addChild( northShape );
                this.addChild( southShape );

                this.rotation = orientation;
            }

            // prototype chaining
            CompassNeedleDisplay.prototype = new Easel.Container();

            return CompassNeedleDisplay;
        } );