// Copyright 2002-2012, University of Colorado

/**
 * Compass model.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'easel', 'common/Logger', 'common/Property' ],
        function ( Easel, Logger, Property ) {

            /**
             * @param {Point} location
             * @param {Boolean} visible
             * @param {BarMagnet} magnet
             * @constructor
             */
            function Compass( location, visible, magnet ) {

                var logger = new Logger( "Compass" ); // logger for this source file

                // initialize properties
                this.location = new Property( location );
                this.visible = new Property( visible );
                this.orientation = new Property( 0 ); // radians

                // Update the orientation.
                var thisInstance = this;
                var updateOrientation = function () {
                    var vector = magnet.getFieldVector( thisInstance.location.get() );
                    thisInstance.orientation.set( vector.getAngle() );
                };
                this.location.addObserver( updateOrientation );
                magnet.location.addObserver( updateOrientation );
                magnet.orientation.addObserver( updateOrientation );
                updateOrientation();

                //DEBUG
                if ( false ) {
                    this.location.addObserver( function ( newValue ) {
                        logger.debug( "location=" + newValue );
                    } );
                    this.visible.addObserver( function ( newValue ) {
                        logger.debug( "visible=" + newValue );
                    } );
                    this.orientation.addObserver( function ( newValue ) {
                        logger.debug( "orientation=" + newValue );
                    } );
                }
            }

            // Resets all properties
            Compass.prototype.reset = function () {
                this.location.reset();
                this.visible.reset();
            };

            // Animates the compass needle
            Compass.prototype.tick = function() {
                console.log( "Compass.tick " + Easel.Ticker.getInterval() );
                //TODO
            };

            return Compass;
        } );
