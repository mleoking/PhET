// Copyright 2002-2012, University of Colorado

/**
 * Rotates the compass needle using the Verlet algorithm to mimic rotational kinematics.
 * The needle must overcome inertia, and it has angular velocity and angular acceleration.
 * This causes the needle to accelerate at it starts to move, and to wobble as it comes to rest.
 *
 * @author Chris Malley (PixelZoom, Inc.)
 */
define( [
            'common/MathUtil',
            'model/Compass',
            'model/BarMagnet'
        ],
        function ( MathUtil, Compass, BarMagnet ) {

            function CompassKinematics( compass, barMagnet ) {

                // constants
                var SENSITIVITY = 0.0001; // increase this to make the compass more sensitive to smaller fields
                var DAMPING = 0.08; // increase this to make the needle wobble less
                var THRESHOLD = MathUtil.toRadians( 0.2 ); // angle at which the needle stops wobbling and snaps to the actual field orientation

                // private fields
                var theta = 0; // Angle of needle orientation (in radians)
                var omega = 0; // Angular velocity, the change in angle over time.
                var alpha = 0; // Angular acceleration, the change in angular velocity over time.

                /**
                 * Gets the orientation of the compass needle, animated over time.
                 * @param {Number} frames number of animation frames
                 */
                this.animateOrientation = function ( frames ) {

                    var fieldVector = barMagnet.getFieldAt( compass.location.get() );
                    var magnitude = fieldVector.getMagnitude();
                    if ( magnitude === 0 ) {
                        return;
                    }

                    var angle = fieldVector.getAngle();

                    // Difference between the field angle and the compass angle.
                    var phi = ( ( magnitude == 0 ) ? 0.0 : ( angle - theta ) );

                    // If the angle difference is tiny, or the compass is inside the magnet,
                    // immediately set the needle angle and consider the compass to be at rest.
                    if ( ( Math.abs( phi ) < THRESHOLD ) || ( barMagnet.contains( compass.location.get() ) ) )  {
                        theta = angle;
                        omega = 0;
                        alpha = 0;
                    }
                    else {
                        // Use the Verlet algorithm to compute angle, angular velocity, and angular acceleration.

                        // Step 1: orientation
                        var alphaTemp = ( SENSITIVITY * Math.sin( phi ) * magnitude ) - ( DAMPING * omega );
                        theta = theta + ( omega * frames ) + ( 0.5 * alphaTemp * frames * frames );
                        theta = theta % ( 2 * Math.PI ); // normalize

                        // Step 2: angular acceleration
                        var omegaTemp = omega + ( alphaTemp * frames );
                        alpha = ( SENSITIVITY * Math.sin( phi ) * magnitude ) - ( DAMPING * omegaTemp );

                        // Step 3: angular velocity
                        omega = omega + ( 0.5 * ( alpha + alphaTemp ) * frames );
                    }
                    compass.orientation.set( theta );
                };

                /**
                 * Workaround to get the compass moving immediately.
                 * In some situations, such as when the magnet polarity is flipped,
                 * it can take quite awhile for the needle to start moving.
                 * So we give the needle a small amount of angular velocity to get it going.
                 */
                this.startMovingNow = function () {
                    omega = 0.03; // adjust as needed for desired behavior
                }
            }

            return CompassKinematics;
        } );
