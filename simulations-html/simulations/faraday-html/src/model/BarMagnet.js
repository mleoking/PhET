// Copyright 2002-2012, University of Colorado

/**
 * Bar magnet model.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [ 'common/Logger', 'common/Property', 'common/Vector2D' ],
        function ( Logger, Property, Vector2D ) {

            /**
             * @class BarMagnet
             * @constructor
             * @param {Point2D} location
             * @param {Dimension2D} size
             * @param {Number} strength in Gauss
             * @param {Range} strengthRange in Gauss
             * @param {Number} orientation in radians
             */
            function BarMagnet( location, size, strength, strengthRange, orientation ) {

                var logger = new Logger( "BarMagnet" ); // logger for this source file

                // initialize properties
                this.location = new Property( location );
                this.size = size;
                this.strength = new Property( strength );
                this.strengthRange = strengthRange;
                this.orientation = new Property( orientation );

                // Debugging output
                if ( false ) {
                    this.location.addObserver( function ( newValue ) {
                        logger.debug( "location=" + newValue );
                    } );
                    this.strength.addObserver( function ( newValue ) {
                        logger.debug( "strength=" + newValue );
                    } );
                    this.orientation.addObserver( function ( newValue ) {
                        logger.debug( "orientation=" + newValue );
                    } );
                }
            }

            // Resets all properties
            BarMagnet.prototype.reset = function () {
                this.location.reset();
                this.strength.reset();
                this.orientation.reset();
            };

            /**
             * Determines whether a point is inside the magnet.
             *
             * @param {Point2D} point
             * @return {Boolean}
             */
            BarMagnet.prototype.contains = function ( point ) {
                return ( point.x >= this.location.get().x - this.size.width / 2 ) &&
                       ( point.x <= this.location.get().x + this.size.width / 2 ) &&
                       ( point.y >= this.location.get().y - this.size.height / 2 ) &&
                       ( point.y <= this.location.get().y + this.size.height / 2 );
            };

            /*
             * Gets the E-field vector at a point.
             * Note that this is not physically accurate.
             * It was not feasible to implement a numerical model directly, as it relies on double integrals.
             * See BarMagnet.java in simulations-java/faraday for details.
             *
             * @param {Point2D} point
             * @return {Vector2D}
             */
            BarMagnet.prototype.getFieldVector = function ( point ) {
                if ( this.contains( point ) ) {
                    // field is the same everywhere inside the magnet
                    return Vector2D.createPolar( this.strength.get(), this.orientation.get() );
                }
                else {
                    //TODO implement simple dipole model (see pg 3 of faraday-notes-2005.pdf)
                    var v = new Vector2D( this.location.get().x - point.x, this.location.get().y - point.y );
                    var magnitude = this.strength.get() / ( 2 * v.getMagnitude() );
                    var angle = v.getAngle() + this.orientation.get();
                    return Vector2D.createPolar( magnitude, angle );
                }
            };

            return BarMagnet;
        } );

