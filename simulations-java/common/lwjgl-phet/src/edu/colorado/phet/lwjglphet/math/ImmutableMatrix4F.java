// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lwjglphet.math;

import java.nio.FloatBuffer;

import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;
import edu.colorado.phet.common.phetcommon.math.vector.Vector4F;

// some code is copied from LWJGL's Matrix4f, so their license in the file follows

/*
 * Copyright (c) 2002-2008 LWJGL Project
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'LWJGL' nor the names of
 *   its contributors may be used to endorse or promote products derived
 *   from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

// TODO: add equals implementation so we can use it in GLNode
// TODO: verifications. this is cobbled together from various sources and needs to be checked thoroughly
public class ImmutableMatrix4F {
    public final float m00;
    public final float m01;
    public final float m02;
    public final float m03;
    public final float m10;
    public final float m11;
    public final float m12;
    public final float m13;
    public final float m20;
    public final float m21;
    public final float m22;
    public final float m23;
    public final float m30;
    public final float m31;
    public final float m32;
    public final float m33;

    // keep track of matrix type so we can better handle OpenGL uses (like just using translation)
    public final MatrixType type;

    public static enum MatrixType {
        IDENTITY, TRANSLATION, SCALING, OTHER
    }

    public static final ImmutableMatrix4F IDENTITY = rowMajor( 1, 0, 0, 0,
                                                               0, 1, 0, 0,
                                                               0, 0, 1, 0,
                                                               0, 0, 0, 1,
                                                               MatrixType.IDENTITY );

    /*---------------------------------------------------------------------------*
    * static constructors
    *----------------------------------------------------------------------------*/

    public static ImmutableMatrix4F translation( float x, float y, float z ) {
        return rowMajor( 1, 0, 0, x,
                         0, 1, 0, y,
                         0, 0, 1, z,
                         0, 0, 0, 1,
                         MatrixType.TRANSLATION );
    }

    public static ImmutableMatrix4F scaling( float s ) {
        return scaling( s, s, s );
    }

    public static ImmutableMatrix4F scaling( float x, float y, float z ) {
        return rowMajor( x, 0, 0, 0,
                         0, y, 0, 0,
                         0, 0, z, 0,
                         0, 0, 0, 1,
                         MatrixType.SCALING );
    }

    /**
     * Returns a matrix rotation aroudn the given axis at the specified angle
     *
     * @param angle the angle, in radians.
     * @param axis  The vector representing the rotation axis. Must be normalized.
     * @return The rotation matrix
     */
    public static ImmutableMatrix4F rotation( Vector3F axis, float angle ) {
        float c = (float) Math.cos( angle );
        float s = (float) Math.sin( angle );
        float C = 1 - c;

        float x = axis.getX();
        float y = axis.getY();
        float z = axis.getZ();

        return rowMajor( x * x * C + c, x * y * C - z * s, x * z * C + y * s, 0,
                         y * x * C + z * s, y * y * C + c, y * z * C - x * s, 0,
                         z * x * C - y * s, z * y * C + x * s, z * z * C + c, 0,
                         0, 0, 0, 1,
                         MatrixType.OTHER );
    }

    public static ImmutableMatrix4F rotationX( float angle ) {
        float c = (float) Math.cos( angle );
        float s = (float) Math.sin( angle );

        return rowMajor( 1, 0, 0, 0,
                         0, c, -s, 0,
                         0, s, c, 0,
                         0, 0, 0, 1,
                         MatrixType.OTHER );
    }

    public static ImmutableMatrix4F rotationY( float angle ) {
        float c = (float) Math.cos( angle );
        float s = (float) Math.sin( angle );

        return rowMajor( c, 0, s, 0,
                         0, 1, 0, 0,
                         -s, 0, c, 0,
                         0, 0, 0, 1,
                         MatrixType.OTHER );
    }

    public static ImmutableMatrix4F rotationZ( float angle ) {
        float c = (float) Math.cos( angle );
        float s = (float) Math.sin( angle );

        return rowMajor( c, -s, 0, 0,
                         s, c, 0, 0,
                         0, 0, 1, 0,
                         0, 0, 0, 1,
                         MatrixType.OTHER );
    }

    public static ImmutableMatrix4F rowMajor( float m00, float m10, float m20, float m30,
                                              float m01, float m11, float m21, float m31,
                                              float m02, float m12, float m22, float m32,
                                              float m03, float m13, float m23, float m33 ) {
        return rowMajor( m00, m10, m20, m30,
                         m01, m11, m21, m31,
                         m02, m12, m22, m32,
                         m03, m13, m23, m33,
                         MatrixType.OTHER );
    }

    public static ImmutableMatrix4F rowMajor( float m00, float m10, float m20, float m30,
                                              float m01, float m11, float m21, float m31,
                                              float m02, float m12, float m22, float m32,
                                              float m03, float m13, float m23, float m33,
                                              MatrixType type ) {
        return new ImmutableMatrix4F( m00, m01, m02, m03, m10, m11, m12, m13, m20, m21, m22, m23, m30, m31, m32, m33, type );
    }

    public static ImmutableMatrix4F columnMajor( float m00, float m01, float m02, float m03,
                                                 float m10, float m11, float m12, float m13,
                                                 float m20, float m21, float m22, float m23,
                                                 float m30, float m31, float m32, float m33 ) {
        return columnMajor( m00, m01, m02, m03,
                            m10, m11, m12, m13,
                            m20, m21, m22, m23,
                            m30, m31, m32, m33, MatrixType.OTHER );
    }

    public static ImmutableMatrix4F columnMajor( float m00, float m01, float m02, float m03,
                                                 float m10, float m11, float m12, float m13,
                                                 float m20, float m21, float m22, float m23,
                                                 float m30, float m31, float m32, float m33, MatrixType type ) {
        return new ImmutableMatrix4F( m00, m01, m02, m03,
                                      m10, m11, m12, m13,
                                      m20, m21, m22, m23,
                                      m30, m31, m32, m33, type );
    }

    public static ImmutableMatrix4F fromGLBuffer( FloatBuffer buffer ) {
        buffer.rewind();

        // we actually can read them out in order. Java's order of execution is guaranteed to get this right
        return new ImmutableMatrix4F( buffer.get(), buffer.get(), buffer.get(), buffer.get(),
                                      buffer.get(), buffer.get(), buffer.get(), buffer.get(),
                                      buffer.get(), buffer.get(), buffer.get(), buffer.get(),
                                      buffer.get(), buffer.get(), buffer.get(), buffer.get(), MatrixType.OTHER );
    }

    /*---------------------------------------------------------------------------*
    * constructors
    *----------------------------------------------------------------------------*/

    // column-major order
    protected ImmutableMatrix4F( float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20, float m21, float m22, float m23, float m30, float m31, float m32, float m33, MatrixType type ) {
        this.m00 = m00;
        this.m01 = m01;
        this.m02 = m02;
        this.m03 = m03;
        this.m10 = m10;
        this.m11 = m11;
        this.m12 = m12;
        this.m13 = m13;
        this.m20 = m20;
        this.m21 = m21;
        this.m22 = m22;
        this.m23 = m23;
        this.m30 = m30;
        this.m31 = m31;
        this.m32 = m32;
        this.m33 = m33;
        this.type = type;
    }

    public ImmutableMatrix4F plus( ImmutableMatrix4F m ) {
        return rowMajor( m00 + m.m00, m10 + m.m10, m20 + m.m20, m30 + m.m30,
                         m01 + m.m01, m11 + m.m11, m21 + m.m21, m31 + m.m31,
                         m02 + m.m02, m12 + m.m12, m22 + m.m22, m32 + m.m32,
                         m03 + m.m03, m13 + m.m13, m23 + m.m23, m33 + m.m33 );
    }

    public ImmutableMatrix4F minus( ImmutableMatrix4F m ) {
        return rowMajor( m00 - m.m00, m10 - m.m10, m20 - m.m20, m30 - m.m30,
                         m01 - m.m01, m11 - m.m11, m21 - m.m21, m31 - m.m31,
                         m02 - m.m02, m12 - m.m12, m22 - m.m22, m32 - m.m32,
                         m03 - m.m03, m13 - m.m13, m23 - m.m23, m33 - m.m33 );
    }

    public ImmutableMatrix4F times( ImmutableMatrix4F m ) {
        float m00 = this.m00 * m.m00 + this.m10 * m.m01 + this.m20 * m.m02 + this.m30 * m.m03;
        float m01 = this.m01 * m.m00 + this.m11 * m.m01 + this.m21 * m.m02 + this.m31 * m.m03;
        float m02 = this.m02 * m.m00 + this.m12 * m.m01 + this.m22 * m.m02 + this.m32 * m.m03;
        float m03 = this.m03 * m.m00 + this.m13 * m.m01 + this.m23 * m.m02 + this.m33 * m.m03;
        float m10 = this.m00 * m.m10 + this.m10 * m.m11 + this.m20 * m.m12 + this.m30 * m.m13;
        float m11 = this.m01 * m.m10 + this.m11 * m.m11 + this.m21 * m.m12 + this.m31 * m.m13;
        float m12 = this.m02 * m.m10 + this.m12 * m.m11 + this.m22 * m.m12 + this.m32 * m.m13;
        float m13 = this.m03 * m.m10 + this.m13 * m.m11 + this.m23 * m.m12 + this.m33 * m.m13;
        float m20 = this.m00 * m.m20 + this.m10 * m.m21 + this.m20 * m.m22 + this.m30 * m.m23;
        float m21 = this.m01 * m.m20 + this.m11 * m.m21 + this.m21 * m.m22 + this.m31 * m.m23;
        float m22 = this.m02 * m.m20 + this.m12 * m.m21 + this.m22 * m.m22 + this.m32 * m.m23;
        float m23 = this.m03 * m.m20 + this.m13 * m.m21 + this.m23 * m.m22 + this.m33 * m.m23;
        float m30 = this.m00 * m.m30 + this.m10 * m.m31 + this.m20 * m.m32 + this.m30 * m.m33;
        float m31 = this.m01 * m.m30 + this.m11 * m.m31 + this.m21 * m.m32 + this.m31 * m.m33;
        float m32 = this.m02 * m.m30 + this.m12 * m.m31 + this.m22 * m.m32 + this.m32 * m.m33;
        float m33 = this.m03 * m.m30 + this.m13 * m.m31 + this.m23 * m.m32 + this.m33 * m.m33;
        MatrixType type = MatrixType.OTHER;
        if ( this.type == MatrixType.TRANSLATION && m.type == MatrixType.TRANSLATION ) {
            type = MatrixType.TRANSLATION;
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
        return rowMajor( m00, m10, m20, m30,
                         m01, m11, m21, m31,
                         m02, m12, m22, m32,
                         m03, m13, m23, m33, type );
    }

    // regular multiplication
    public Vector4F times( Vector4F v ) {
        float x = this.m00 * v.getX() + this.m10 * v.getY() + this.m20 * v.getZ() + this.m30 * v.getW();
        float y = this.m01 * v.getX() + this.m11 * v.getY() + this.m21 * v.getZ() + this.m31 * v.getW();
        float z = this.m02 * v.getX() + this.m12 * v.getY() + this.m22 * v.getZ() + this.m32 * v.getW();
        float w = this.m03 * v.getX() + this.m13 * v.getY() + this.m23 * v.getZ() + this.m33 * v.getW();
        return new Vector4F( x, y, z, w );
    }

    // multiplication of the transpose of this matrix and v
    public Vector4F timesTranspose( Vector4F v ) {
        float x = this.m00 * v.getX() + this.m01 * v.getY() + this.m02 * v.getZ() + this.m03 * v.getW();
        float y = this.m10 * v.getX() + this.m11 * v.getY() + this.m12 * v.getZ() + this.m13 * v.getW();
        float z = this.m20 * v.getX() + this.m21 * v.getY() + this.m22 * v.getZ() + this.m23 * v.getW();
        float w = this.m30 * v.getX() + this.m31 * v.getY() + this.m32 * v.getZ() + this.m33 * v.getW();
        return new Vector4F( x, y, z, w );
    }

    // multiplication of the XYZ coordinates of the vector (not including translation, so we can preserve offsets)
    public Vector3F timesVector( Vector3F v ) {
        float x = this.m00 * v.getX() + this.m01 * v.getY() + this.m02 * v.getZ();
        float y = this.m10 * v.getX() + this.m11 * v.getY() + this.m12 * v.getZ();
        float z = this.m20 * v.getX() + this.m21 * v.getY() + this.m22 * v.getZ();
        return new Vector3F( x, y, z );
    }

    public Vector3F times( Vector3F v ) {
        return times( new Vector4F( v ) ).to3F();
    }

    public Vector3F timesTranspose( Vector3F v ) {
        return timesTranspose( new Vector4F( v ) ).to3F();
    }

    public ImmutableMatrix4F transposed() {
        return rowMajor( m00, m01, m02, m03,
                         m10, m11, m12, m13,
                         m20, m21, m22, m23,
                         m30, m31, m32, m33 );
    }

    public float determinant() {
        float f =
                m00
                * ( ( m11 * m22 * m33 + m12 * m23 * m31 + m13 * m21 * m32 )
                    - m13 * m22 * m31
                    - m11 * m23 * m32
                    - m12 * m21 * m33 );
        f -= m01
             * ( ( m10 * m22 * m33 + m12 * m23 * m30 + m13 * m20 * m32 )
                 - m13 * m22 * m30
                 - m10 * m23 * m32
                 - m12 * m20 * m33 );
        f += m02
             * ( ( m10 * m21 * m33 + m11 * m23 * m30 + m13 * m20 * m31 )
                 - m13 * m21 * m30
                 - m10 * m23 * m31
                 - m11 * m20 * m33 );
        f -= m03
             * ( ( m10 * m21 * m32 + m11 * m22 * m30 + m12 * m20 * m31 )
                 - m12 * m21 * m30
                 - m10 * m22 * m31
                 - m11 * m20 * m32 );
        return f;
    }

    private static float determinant3x3( float t00, float t01, float t02,
                                         float t10, float t11, float t12,
                                         float t20, float t21, float t22 ) {
        return t00 * ( t11 * t22 - t12 * t21 )
               + t01 * ( t12 * t20 - t10 * t22 )
               + t02 * ( t10 * t21 - t11 * t20 );
    }

    public ImmutableMatrix4F negated() {
        return rowMajor( -m00, -m10, -m20, -m30,
                         -m01, -m11, -m21, -m31,
                         -m02, -m12, -m22, -m32,
                         -m03, -m13, -m23, -m33 );
    }

    public ImmutableMatrix4F inverted() {
        float determinant = this.determinant();

        if ( determinant != 0 ) {
            // TODO: verify! this comment seems to indicate an incorrect understanding of the inverse!
            /*
                * m00 m01 m02 m03
                * m10 m11 m12 m13
                * m20 m21 m22 m23
                * m30 m31 m32 m33
                */
            float determinant_inv = 1f / determinant;

            // first row
            float t00 = determinant3x3( this.m11, this.m12, this.m13, this.m21, this.m22, this.m23, this.m31, this.m32, this.m33 );
            float t01 = -determinant3x3( this.m10, this.m12, this.m13, this.m20, this.m22, this.m23, this.m30, this.m32, this.m33 );
            float t02 = determinant3x3( this.m10, this.m11, this.m13, this.m20, this.m21, this.m23, this.m30, this.m31, this.m33 );
            float t03 = -determinant3x3( this.m10, this.m11, this.m12, this.m20, this.m21, this.m22, this.m30, this.m31, this.m32 );
            // second row
            float t10 = -determinant3x3( this.m01, this.m02, this.m03, this.m21, this.m22, this.m23, this.m31, this.m32, this.m33 );
            float t11 = determinant3x3( this.m00, this.m02, this.m03, this.m20, this.m22, this.m23, this.m30, this.m32, this.m33 );
            float t12 = -determinant3x3( this.m00, this.m01, this.m03, this.m20, this.m21, this.m23, this.m30, this.m31, this.m33 );
            float t13 = determinant3x3( this.m00, this.m01, this.m02, this.m20, this.m21, this.m22, this.m30, this.m31, this.m32 );
            // third row
            float t20 = determinant3x3( this.m01, this.m02, this.m03, this.m11, this.m12, this.m13, this.m31, this.m32, this.m33 );
            float t21 = -determinant3x3( this.m00, this.m02, this.m03, this.m10, this.m12, this.m13, this.m30, this.m32, this.m33 );
            float t22 = determinant3x3( this.m00, this.m01, this.m03, this.m10, this.m11, this.m13, this.m30, this.m31, this.m33 );
            float t23 = -determinant3x3( this.m00, this.m01, this.m02, this.m10, this.m11, this.m12, this.m30, this.m31, this.m32 );
            // fourth row
            float t30 = -determinant3x3( this.m01, this.m02, this.m03, this.m11, this.m12, this.m13, this.m21, this.m22, this.m23 );
            float t31 = determinant3x3( this.m00, this.m02, this.m03, this.m10, this.m12, this.m13, this.m20, this.m22, this.m23 );
            float t32 = -determinant3x3( this.m00, this.m01, this.m03, this.m10, this.m11, this.m13, this.m20, this.m21, this.m23 );
            float t33 = determinant3x3( this.m00, this.m01, this.m02, this.m10, this.m11, this.m12, this.m20, this.m21, this.m22 );

            // transpose and divide by the determinant
            float m00 = t00 * determinant_inv;
            float m11 = t11 * determinant_inv;
            float m22 = t22 * determinant_inv;
            float m33 = t33 * determinant_inv;
            float m01 = t10 * determinant_inv;
            float m10 = t01 * determinant_inv;
            float m20 = t02 * determinant_inv;
            float m02 = t20 * determinant_inv;
            float m12 = t21 * determinant_inv;
            float m21 = t12 * determinant_inv;
            float m03 = t30 * determinant_inv;
            float m30 = t03 * determinant_inv;
            float m13 = t31 * determinant_inv;
            float m31 = t13 * determinant_inv;
            float m32 = t23 * determinant_inv;
            float m23 = t32 * determinant_inv;
            return rowMajor( m00, m10, m20, m30,
                             m01, m11, m21, m31,
                             m02, m12, m22, m32,
                             m03, m13, m23, m33 );
        }
        else {
            throw new RuntimeException( "Matrix could not be inverted" );
        }
    }


    /**
     * Store this matrix in a float buffer. The matrix is stored in column
     * major (openGL) order.
     *
     * @param buf The buffer to store this matrix in
     */
    public void store( FloatBuffer buf ) {
        buf.rewind();
        buf.put( m00 );
        buf.put( m01 );
        buf.put( m02 );
        buf.put( m03 );
        buf.put( m10 );
        buf.put( m11 );
        buf.put( m12 );
        buf.put( m13 );
        buf.put( m20 );
        buf.put( m21 );
        buf.put( m22 );
        buf.put( m23 );
        buf.put( m30 );
        buf.put( m31 );
        buf.put( m32 );
        buf.put( m33 );
    }

    /**
     * Store this matrix in a float buffer. The matrix is stored in row
     * major (maths) order.
     *
     * @param buf The buffer to store this matrix in
     */
    public void storeTranspose( FloatBuffer buf ) {
        buf.rewind();
        buf.put( m00 );
        buf.put( m10 );
        buf.put( m20 );
        buf.put( m30 );
        buf.put( m01 );
        buf.put( m11 );
        buf.put( m21 );
        buf.put( m31 );
        buf.put( m02 );
        buf.put( m12 );
        buf.put( m22 );
        buf.put( m32 );
        buf.put( m03 );
        buf.put( m13 );
        buf.put( m23 );
        buf.put( m33 );
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        buf.append( m00 ).append( ' ' ).append( m10 ).append( ' ' ).append( m20 ).append( ' ' ).append( m30 ).append( '\n' );
        buf.append( m01 ).append( ' ' ).append( m11 ).append( ' ' ).append( m21 ).append( ' ' ).append( m31 ).append( '\n' );
        buf.append( m02 ).append( ' ' ).append( m12 ).append( ' ' ).append( m22 ).append( ' ' ).append( m32 ).append( '\n' );
        buf.append( m03 ).append( ' ' ).append( m13 ).append( ' ' ).append( m23 ).append( ' ' ).append( m33 ).append( '\n' );
        return buf.toString();
    }

    public Vector3F getTranslation() {
        return new Vector3F( m30, m31, m32 );
    }

    public Vector3F getScaling() {
        return new Vector3F( m00, m11, m22 );
    }

}
