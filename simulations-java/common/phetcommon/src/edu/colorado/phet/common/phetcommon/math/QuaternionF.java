// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.phetcommon.math;

import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;

/**
 * Floating-point version of a quaternion. Very helpful for rotation-based computations. See http://en.wikipedia.org/wiki/Quaternion
 *
 * @author Jonathan Olson
 */
public class QuaternionF {
    public final float x;
    public final float y;
    public final float z;
    public final float w;

    public QuaternionF() {
        x = 0;
        y = 0;
        z = 0;
        w = 1;
    }

    public QuaternionF( float x, float y, float z, float w ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    // addition
    public QuaternionF plus( QuaternionF quat ) {
        return new QuaternionF( x + quat.x, y + quat.y, z + quat.z, w + quat.w );
    }

    // scalar multiplication
    public QuaternionF times( float f ) {
        return new QuaternionF( x * f, y * f, z * f, w * f );
    }

    // standard quaternion multiplication (hamilton product)
    public QuaternionF times( QuaternionF quat ) {
        return new QuaternionF(
                x * quat.x - y * quat.y - z * quat.z - w * quat.w,
                x * quat.y + y * quat.x + z * quat.w - w * quat.z,
                x * quat.z - y * quat.w + z * quat.x + w * quat.y,
                x * quat.w + y * quat.z - z * quat.y + w * quat.x
        );
    }

    // multiplication that transforms the vector by the quaternion
    public Vector3F times( Vector3F v ) {
        if ( v.magnitude() == 0 ) {
            return new Vector3F();
        }

        return new Vector3F(
                w * w * v.x + 2 * y * w * v.z - 2 * z * w * v.y + x * x * v.x + 2 * y * x * v.y + 2 * z * x * v.z - z * z * v.x - y * y * v.x,
                2 * x * y * v.x + y * y * v.y + 2 * z * y * v.z + 2 * w * z * v.x - z * z * v.y + w * w * v.y - 2 * x * w * v.z - x * x * v.y,
                2 * x * z * v.x + 2 * y * z * v.y + z * z * v.z - 2 * w * y * v.x - y * y * v.z + 2 * w * x * v.y - x * x * v.z + w * w * v.z
        );
    }

    public float magnitude() {
        return (float) Math.sqrt( magnitudeSquared() );
    }

    public float magnitudeSquared() {
        return x * x + y * y + z * z + w * w;
    }

    public QuaternionF normalized() {
        float magnitude = magnitude();
        if ( magnitude == 0 ) {
            throw new UnsupportedOperationException( "Cannot normalize a zero-magnitude quaternion." );
        }
        return times( 1 / magnitude );
    }

    public static QuaternionF fromEulerAngles( float yaw, float roll, float pitch ) {
        float sinYaw = (float) Math.sin( yaw / 2 );
        float cosYaw = (float) Math.cos( yaw / 2 );
        float sinRoll = (float) Math.sin( roll / 2 );
        float cosRoll = (float) Math.cos( roll / 2 );
        float sinPitch = (float) Math.sin( pitch / 2 );
        float cosPitch = (float) Math.cos( pitch / 2 );

        float a = cosRoll * cosPitch;
        float b = sinRoll * sinPitch;
        float c = cosRoll * sinPitch;
        float d = sinRoll * cosPitch;

        return new QuaternionF(
                a * cosYaw - b * sinYaw,
                a * sinYaw + b * cosYaw,
                d * cosYaw + c * sinYaw,
                c * cosYaw - d * sinYaw
        ).normalized();
    }

    public Matrix3F toRotationMatrix() {
        // see http://en.wikipedia.org/wiki/Rotation_matrix#Quaternion

        float norm = magnitudeSquared();
        float flip = ( norm == 1f ) ? 2f : ( norm > 0f ) ? 2f / norm : 0;

        float xx = x * x * flip;
        float xy = x * y * flip;
        float xz = x * z * flip;
        float xw = w * x * flip;
        float yy = y * y * flip;
        float yz = y * z * flip;
        float yw = w * y * flip;
        float zz = z * z * flip;
        float zw = w * z * flip;

        return Matrix3F.rowMajor(
                1 - ( yy + zz ), ( xy + zw ), ( xz - yw ),
                ( xy - zw ), 1 - ( xx + zz ), ( yz + xw ),
                ( xz + yw ), ( yz - xw ), 1 - ( xx + yy )
        );
    }
}
