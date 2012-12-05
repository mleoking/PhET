// Copyright 2002-2012, University of Colorado

/**
 * Bar magnet model.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [
            'common/Logger',
            'common/Point2D',
            'common/Property',
            'common/Vector2D'
        ],
        function ( Logger, Point2D, Property, Vector2D ) {

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

                this.logger = new Logger( "BarMagnet" ); // logger for this source file

                // initialize properties
                this.location = new Property( location );
                this.size = size;
                this.strength = new Property( strength );
                this.strengthRange = strengthRange;
                this.orientation = new Property( orientation );

                // Debugging output
                var that = this;
                if ( false ) {
                    this.location.addObserver( function ( newValue ) {
                        that.logger.debug( "location=" + newValue );
                    } );
                    this.strength.addObserver( function ( newValue ) {
                        that.logger.debug( "strength=" + newValue );
                    } );
                    this.orientation.addObserver( function ( newValue ) {
                        that.logger.debug( "orientation=" + newValue );
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
             * Assumes the point has been normalized for a magnet at (0,0) with orientation=0.
             *
             * @param {Point2D} point
             * @return {Boolean}
             */
            BarMagnet.prototype.contains = function ( point ) {
                return ( point.x >= -this.size.width / 2 ) && ( point.x <= this.size.width / 2 ) &&
                       ( point.y >= -this.size.height / 2 ) && ( point.y <= this.size.height / 2 );
            };

            /**
             * Transforms the point so that it's relative to a magnet at (0,0) with orientation=0.
             * @param {Point2D} point
             * @return {Point2D}
             */
            BarMagnet.prototype.normalizePoint = function ( point ) {
                var v = new Vector2D( point.x - this.location.get().x, point.y - this.location.get().y ); // translate
                v = v.rotate( -this.orientation.get() ); // rotate
                return new Point2D( v.x, v.y );
                return point;
            }

            /*
             * Gets the B-field vector at a point.
             * Note that this is not physically accurate.
             * It was not feasible to implement a numerical model directly, as it relies on double integrals.
             * See BarMagnet.java in simulations-java/faraday for details.
             *
             * @param {Point2D} point
             * @return {Vector2D}
             */
            BarMagnet.prototype.getFieldAt = function ( point ) {

                // All of our calculations are based a magnet located at the origin,
                // with the north pole pointing down the X-axis.
                // The point we received is based on the magnet's actual location and origin.
                // So transform the point accordingly, adjusting for location and rotation of the magnet.
                var normalizedPoint = this.normalizePoint( point );

                var vector;
                if ( this.contains( normalizedPoint ) ) {
                    vector = this.getFieldInside( normalizedPoint );
                }
                else {
                    vector = this.getFieldOutside( normalizedPoint );
                }

                // Adjust the field vector to match the magnet's direction.
                vector = vector.rotate( this.orientation.get() );

                //TODO Why is this necessary? If I remove it, the max strength is exceeded often, sometime by a large amount.
                // Clamp magnitude to magnet strength.
                var magnitude = vector.getMagnitude();
                var magnetStrength = this.strength.get();
                if ( magnitude > magnetStrength ) {
                    vector = Vector2D.createPolar( magnetStrength, vector.getAngle() );
                    this.logger.warn( "BarMagnet.getFieldAt: magnitude exceeds magnet strength by " + (magnitude - magnetStrength ) ); // TODO use Logger
                }

                return vector;
            };

            /*
             * Gets the magnetic field vector at a point inside the magnet.
             * Algorithm courtesy of Mike Dubson (dubson@spot.colorado.edu).
             * <p>
             * B-field varies as a gradual curve inside the magnet.
             * Full magnet strength is at the magnet center.
             * B-field drops off more rapidly the further you get from the center,
             * and is half magnet strength at the magnet ends.
             *
             * @param {Point2D} point the point, relative to the magnet's origin
             * @return {Vector2D}
             */
            BarMagnet.prototype.getFieldInside = function ( point ) {
                var strength = this.strength.get();
                var w = this.size.width;
                var h = this.size.height;
                var x = point.x;
                var C = ( strength / 2 ) * ( Math.sqrt( ( w * w ) + ( h * h ) ) / w );
                var denominator1 = Math.sqrt( ( ( x - w / 2 ) * ( x - w / 2 ) ) + ( h / 2 * h / 2 ) );
                var denominator2 = Math.sqrt( ( ( x + w / 2 ) * ( x + w / 2 ) ) + ( h / 2 * h / 2 ) );
                var Bx = C * ( ( ( w / 2 - x ) / denominator1 ) + ( ( w / 2 + x ) / denominator2 ) );
                var By = 0;
                return new Vector2D( Bx, By );
            }

            /*
             * Gets the magnetic field strength at a point outside the magnet.
             * Algorithm courtesy of Mike Dubson (dubson@spot.colorado.edu).
             * <point>
             * The magnitude is guaranteed to be >=0 and <= the magnet strength.
             * This algorithm makes the following assumptions:
             * <ul>
             * <li>the magnet's location is (0,0)
             * <li>the magnet's direction is 0.0 (north pole pointing down the positive X axis)
             * <li>the magnet's physical center is at (0,0)
             * <li>the magnet's width > height
             * <li>the point is guaranteed to be outside the magnet
             * <li>the point has been transformed so that it is relative to above magnet assumptions
             * </ul>
             * <point>
             * Terminology:
             * <ul>
             * <li>axes oriented with +X right, +Y up
             * <li>origin is the center of the coil, at (0,0)
             * <li>(x,y) is the point of interest where we are measuring the magnetic field
             * <li>h is the height of the magnet
             * <li>w is the width of the magnet
             * <li>L is the distance between the dipoles
             * <li>C is a fudge factor
             * <li>rN is the distance from the north dipole to (x,y)
             * <li>rS is the distance from the south dipole to (x,y)
             * <li>B is the field vector at (x,y) due to the entire magnet
             * <li>BN is the field vector at (x,y) due to the north dipole
             * <li>BS is the field vector at (x,y) due to the south dipole
             * <li>e is the exponent that specifies how the field decreases with distance (3 in reality)
             * </ul>
             * <point>
             * The dipole locations are:
             * <ul>
             * <li>north: w/2 - h/2
             * <li>south: -w/2 + h/2
             * </ul>
             * The algorithm for outside the magnet:
             * <ul>
             * <li>BN = ( +C / rN^e ) [ ( x - L/2 ), y ]
             * <li>BS = ( -C / rS^e ) [ ( x + L/2 ), y ]
             * <li>B = BN + BS
             *
             * @param {Point2D} point the point, relative to the magnet's origin
             */
            BarMagnet.prototype.getFieldOutside = function ( point ) {

                /*
                 * Arbitrary "fudge factor" that controls the B-field transitions between
                 * inside and outside the magnet.  Adjust this using the provided developer control.
                 */
                var INSIDE_OUTSIDE_TRANSITION_FACTOR = 321;

                var DISTANCE_EXPONENT = 2; // determines how the field strength decreases with the distance from the magnet

                // Magnet strength.
                var magnetStrength = this.strength.get();

                // Dipole locations.
                var northPoint = new Point2D( ( this.size.width / 2 ) - ( this.size.height / 2 ), 0 ); // north dipole
                var southPoint = new Point2D( (-this.size.width / 2 ) + ( this.size.height / 2 ), 0 ); // south dipole

                // Distances.
                var rN = northPoint.distance( point ); // north dipole to point
                if ( rN == 0 ) {
                    rN = 0.001; // must be non-zero or later calculations will have divide-by-zero problem
                }
                var rS = southPoint.distance( point ); // south dipole to point
                if ( rS == 0 ) {
                    rS = 0.001; // must be non-zero or later calculations will have divide-by-zero problem
                }
                var L = southPoint.distance( northPoint ); // dipole to dipole

                // Fudge factor
                var C = INSIDE_OUTSIDE_TRANSITION_FACTOR * magnetStrength;

                // North dipole field strength vector.
                var cN = +( C / Math.pow( rN, DISTANCE_EXPONENT ) ); // constant multiplier
                var xN = cN * ( point.x - ( L / 2 ) ); // X component
                var yN = cN * point.y; // Y component
                var northVector = new Vector2D( xN, yN ); // north dipole vector

                // South dipole field strength vector.
                var cS = -( C / Math.pow( rS, DISTANCE_EXPONENT ) ); // constant multiplier
                var xS = cS * ( point.x + ( L / 2 ) ); // X component
                var yS = cS * point.y; // Y component
                var southVector = new Vector2D( xS, yS ); // south dipole vector

                // Total field strength is the vector sum.
                return northVector.plus( southVector );
            }

            return BarMagnet;
        } );

