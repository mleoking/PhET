// Copyright 2002-2012, University of Colorado

define( [ 'common/MathUtil', 'model/Compass', 'model/BarMagnet' ],
        function ( MathUtil, Compass, BarMagnet ) {

            function CompassKinematics( compass, barMagnet ) {

                // constants
                var SENSITIVITY = 0.01; // increase this to make the compass more sensitive to smaller fields
                var DAMPING = 0.08; // increase this to make the needle wobble less
                var THRESHOLD = MathUtil.toRadians( 0.2 ); // angle at which the needle stops wobbling and snaps to the actual field orientation

                // private fields
                var _theta = 0; // Angle of needle orientation (in radians)
                var _omega = 0; // Angular velocity, the change in angle over time.
                var _alpha = 0; // Angular accelaration, the change in angular velocity over time.

                /**
                 * Gets the orientation of the compass needle, animated over time.
                 * @param {Number} dt
                 */
                this.animateOrientation = function ( dt ) {

                    var fieldVector = barMagnet.getFieldVector( compass.location.get() );
                    var magnitude = fieldVector.getMagnitude();
                    var angle = fieldVector.getAngle();

                    // Difference between the field angle and the compass angle.
                    var phi = ( ( magnitude == 0 ) ? 0.0 : ( angle - _theta ) );

                    if ( Math.abs( phi ) < THRESHOLD ) {
                        // When the difference between the field angle and the compass angle is insignificant,
                        // simply set the angle and consider the compass to be at rest.
                        _theta = angle;
                        _omega = 0;
                        _alpha = 0;
                    }
                    else {
                        // Use the Verlet algorithm to compute angle, angular velocity, and angular acceleration.

                        // Step 1: orientation
                        var alphaTemp = ( SENSITIVITY * Math.sin( phi ) * magnitude ) - ( DAMPING * _omega );
                        _theta = _theta + ( _omega * dt ) + ( 0.5 * alphaTemp * dt * dt );

                        // Step 2: angular acceleration
                        var omegaTemp = _omega + ( alphaTemp * dt );
                        _alpha = ( SENSITIVITY * Math.sin( phi ) * magnitude ) - ( DAMPING * omegaTemp );

                        // Step 3: angular velocity
                        _omega = _omega + ( 0.5 * ( _alpha + alphaTemp ) * dt );
                    }
                    compass.orientation.set( _theta );
                };

                /**
                 * Workaround to get the compass moving immediately.
                 * In some situations, such as when the magnet polarity is flipped,
                 * it can take quite awhile for the magnet to start moving.
                 * So we give the compass needle a small amount of angular velocity to get it going.
                 */
                this.startMovingNow = function () {
                    _omega = 0.03; // adjust as needed for desired behavior
                }
            }

            return CompassKinematics;
        } );
