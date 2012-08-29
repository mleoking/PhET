// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.phetcommon.math;

import lombok.EqualsAndHashCode;

import java.nio.FloatBuffer;

import edu.colorado.phet.common.phetcommon.math.vector.Vector2F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;

import static edu.colorado.phet.common.phetcommon.util.FunctionalUtils.mkString;
import static java.util.Arrays.asList;

/**
 * Immutable 3x3 matrix, with similarly-named methods as the vector classes.
 *
 * @author Jonathan Olson
 */
public @EqualsAndHashCode(callSuper = false) class Matrix3F {

    // entries in row-major order (v<row><column>, both starting from 0)
    public final float v00, v01, v02, v10, v11, v12, v20, v21, v22;

    // keep track of matrix type so we can better handle OpenGL uses (like just using translation)
    public final MatrixType type;

    public static enum MatrixType {
        IDENTITY, // strictly the identity
        TRANSLATION_2D, // identity + translation
        SCALING, // scaled identity matrix
        OTHER // anything (including one of the above)
    }

    // easily accessible identity matrix
    public static final Matrix3F IDENTITY = rowMajor( 1, 0, 0,
                                                      0, 1, 0,
                                                      0, 0, 1,
                                                      MatrixType.IDENTITY );

    /*---------------------------------------------------------------------------*
    * static constructors
    *----------------------------------------------------------------------------*/

    public static Matrix3F translation( float x, float y ) {
        return rowMajor( 1, 0, x,
                         0, 1, y,
                         0, 0, 1,
                         MatrixType.TRANSLATION_2D );
    }

    public static Matrix3F scaling( float s ) {
        return scaling( s, s, s );
    }

    public static Matrix3F scaling( float x, float y, float z ) {
        return rowMajor( x, 0, 0,
                         0, y, 0,
                         0, 0, z,
                         MatrixType.SCALING );
    }

    /**
     * Returns a matrix rotation around the given axis at the specified angle
     *
     * @param angle the angle, in radians.
     * @param axis  The vector representing the rotation axis. Must be normalized.
     * @return The rotation matrix
     */
    public static Matrix3F rotation( Vector3F axis, float angle ) {
        float c = (float) Math.cos( angle );
        float s = (float) Math.sin( angle );
        float C = 1 - c;

        float x = axis.getX();
        float y = axis.getY();
        float z = axis.getZ();

        return rowMajor( x * x * C + c, x * y * C - z * s, x * z * C + y * s,
                         y * x * C + z * s, y * y * C + c, y * z * C - x * s,
                         z * x * C - y * s, z * y * C + x * s, z * z * C + c,
                         MatrixType.OTHER );
    }

    public static Matrix3F rotation( QuaternionF quaternion ) {
        return quaternion.toRotationMatrix();
    }

    public static Matrix3F rotationX( float angle ) {
        float c = (float) Math.cos( angle );
        float s = (float) Math.sin( angle );

        return rowMajor( 1, 0, 0,
                         0, c, -s,
                         0, s, c,
                         MatrixType.OTHER );
    }

    public static Matrix3F rotationY( float angle ) {
        float c = (float) Math.cos( angle );
        float s = (float) Math.sin( angle );

        return rowMajor( c, 0, s,
                         0, 1, 0,
                         -s, 0, c,
                         MatrixType.OTHER );
    }

    public static Matrix3F rotationZ( float angle ) {
        float c = (float) Math.cos( angle );
        float s = (float) Math.sin( angle );

        return rowMajor( c, -s, 0,
                         s, c, 0,
                         0, 0, 1,
                         MatrixType.OTHER );
    }

    public static Matrix3F rowMajor( float v00, float v01, float v02,
                                     float v10, float v11, float v12,
                                     float v20, float v21, float v22 ) {
        return rowMajor( v00, v01, v02,
                         v10, v11, v12,
                         v20, v21, v22,
                         MatrixType.OTHER );
    }

    public static Matrix3F rowMajor( float v00, float v01, float v02,
                                     float v10, float v11, float v12,
                                     float v20, float v21, float v22,
                                     MatrixType type ) {
        return new Matrix3F( v00, v01, v02, v10, v11, v12, v20, v21, v22, type );
    }

    public static Matrix3F columnMajor( float v00, float v10, float v20,
                                        float v01, float v11, float v21,
                                        float v02, float v12, float v22 ) {
        return columnMajor( v00, v10, v20,
                            v01, v11, v21,
                            v02, v12, v22,
                            MatrixType.OTHER );
    }

    public static Matrix3F columnMajor( float v00, float v10, float v20,
                                        float v01, float v11, float v21,
                                        float v02, float v12, float v22,
                                        MatrixType type ) {
        return new Matrix3F( v00, v01, v02,
                             v10, v11, v12,
                             v20, v21, v22,
                             type );
    }

    /*---------------------------------------------------------------------------*
    * constructors
    *----------------------------------------------------------------------------*/

    // row-major order
    protected Matrix3F( float v00, float v01, float v02,
                        float v10, float v11, float v12,
                        float v20, float v21, float v22,
                        MatrixType type ) {
        this.v00 = v00;
        this.v10 = v10;
        this.v20 = v20;
        this.v01 = v01;
        this.v11 = v11;
        this.v21 = v21;
        this.v02 = v02;
        this.v12 = v12;
        this.v22 = v22;
        this.type = type;
    }

    public Matrix4F toMatrix4f() {
        return Matrix4F.rowMajor(
                v00, v01, v02, 0,
                v10, v11, v12, 0,
                v20, v21, v22, 0,
                0, 0, 0, 1
        );
    }

    public Matrix3F plus( Matrix3F m ) {
        return rowMajor( v00 + m.v00, v01 + m.v01, v02 + m.v02,
                         v10 + m.v10, v11 + m.v11, v12 + m.v12,
                         v20 + m.v20, v21 + m.v21, v22 + m.v22 );
    }

    public Matrix3F minus( Matrix3F m ) {
        return rowMajor( v00 - m.v00, v01 - m.v01, v02 - m.v02,
                         v10 - m.v10, v11 - m.v11, v12 - m.v12,
                         v20 - m.v20, v21 - m.v21, v22 - m.v22 );
    }

    public Matrix3F times( Matrix3F m ) {
        MatrixType type = MatrixType.OTHER;
        if ( this.type == MatrixType.TRANSLATION_2D && m.type == MatrixType.TRANSLATION_2D ) {
            type = MatrixType.TRANSLATION_2D;
        }
        if ( this.type == MatrixType.SCALING && m.type == MatrixType.SCALING ) {
            type = MatrixType.SCALING;
        }
        if ( this.type == MatrixType.IDENTITY ) {
            type = m.type;
        }
        if ( m.type == MatrixType.IDENTITY ) {
            type = this.type;
        }
        return rowMajor( this.v00 * m.v00 + this.v01 * m.v10 + this.v02 * m.v20,
                         this.v00 * m.v01 + this.v01 * m.v11 + this.v02 * m.v21,
                         this.v00 * m.v02 + this.v01 * m.v12 + this.v02 * m.v22,
                         this.v10 * m.v00 + this.v11 * m.v10 + this.v12 * m.v20,
                         this.v10 * m.v01 + this.v11 * m.v11 + this.v12 * m.v21,
                         this.v10 * m.v02 + this.v11 * m.v12 + this.v12 * m.v22,
                         this.v20 * m.v00 + this.v21 * m.v10 + this.v22 * m.v20,
                         this.v20 * m.v01 + this.v21 * m.v11 + this.v22 * m.v21,
                         this.v20 * m.v02 + this.v21 * m.v12 + this.v22 * m.v22,
                         type );
    }

    // regular multiplication
    public Vector3F times( Vector3F v ) {
        return new Vector3F(
                this.v00 * v.getX() + this.v01 * v.getY() + this.v02 * v.getZ(),
                this.v10 * v.getX() + this.v11 * v.getY() + this.v12 * v.getZ(),
                this.v20 * v.getX() + this.v21 * v.getY() + this.v22 * v.getZ()
        );
    }

    // multiplication of the transpose of this matrix and v
    public Vector3F timesTranspose( Vector3F v ) {
        return new Vector3F(
                this.v00 * v.getX() + this.v10 * v.getY() + this.v20 * v.getZ(),
                this.v01 * v.getX() + this.v11 * v.getY() + this.v21 * v.getZ(),
                this.v02 * v.getX() + this.v12 * v.getY() + this.v22 * v.getZ()
        );
    }

    public Matrix3F transposed() {
        return rowMajor( v00, v10, v20,
                         v01, v11, v21,
                         v02, v12, v22 );
    }

    public float determinant() {
        return v00 * v11 * v22 + v01 * v12 * v20 + v02 * v10 * v21 - v02 * v11 * v20 - v01 * v10 * v22 - v00 * v12 * v21;
    }

    public Matrix3F negated() {
        return rowMajor( -v00, -v01, -v02,
                         -v10, -v11, -v12,
                         -v20, -v21, -v22 );
    }

    public Matrix3F inverted() {
        float determinant = determinant();

        if ( determinant != 0 ) {
            return rowMajor(
                    ( -v12 * v21 + v11 * v22 ) / determinant,
                    ( v02 * v21 - v01 * v22 ) / determinant,
                    ( -v02 * v11 + v01 * v12 ) / determinant,
                    ( v12 * v20 - v10 * v22 ) / determinant,
                    ( -v02 * v20 + v00 * v22 ) / determinant,
                    ( v02 * v10 - v00 * v12 ) / determinant,
                    ( -v11 * v20 + v10 * v21 ) / determinant,
                    ( v01 * v20 - v00 * v21 ) / determinant,
                    ( -v01 * v10 + v00 * v11 ) / determinant
            );
        }
        else {
            throw new RuntimeException( "Matrix could not be inverted" );
        }
    }


    /**
     * Store this matrix in a float buffer. The matrix is stored in column
     * major (openGL) order.
     * <p/>
     * Additionally, it is written in the 4x4 matrix format
     *
     * @param buf The buffer to store this matrix in
     */
    public void writeToBuffer( FloatBuffer buf ) {
        buf.rewind();
        buf.put( new float[]{
                v00, v10, v20, 0,
                v01, v11, v21, 0,
                v02, v12, v22, 0,
                0, 0, 0, 1
        } );
    }

    /**
     * Store this matrix in a float buffer. The matrix is stored in row
     * major (maths) order.
     * <p/>
     * Additionally, it is written in the 4x4 matrix format
     *
     * @param buf The buffer to store this matrix in
     */
    public void writeTransposeToBuffer( FloatBuffer buf ) {
        buf.rewind();
        buf.put( new float[]{
                v00, v01, v02, 0,
                v10, v11, v12, 0,
                v20, v21, v22, 0,
                0, 0, 0, 1
        } );
    }

    public String toString() {
        return mkString( asList(
                mkString( asList( v00, v01, v02 ), " " ),
                mkString( asList( v10, v11, v12 ), " " ),
                mkString( asList( v20, v21, v22 ), " " )
        ), "\n" );
    }

    public Vector2F getTranslation() {
        return new Vector2F( v02, v12 );
    }

    public Vector3F getScaling() {
        return new Vector3F( v00, v11, v22 );
    }
}
