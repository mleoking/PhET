// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.moleculeshapes.dev;

import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;

public class TemporaryTest {
    public static void main( String[] args ) {
        Quaternion q = new Quaternion( -5, -3.64f, 154, 5 );
        q.normalizeLocal();
        Vector3f v = new Vector3f( -105, 23, 10 );
        Matrix3f m = new Matrix3f();
        Vector3f vqq = new Vector3f( 4, 76, -23 );
        m.fromStartEndVectors( vqq.normalize(),
                               v.normalize() );
        Quaternion nq = q.mult( FastMath.invSqrt( q.norm() ) );

        System.out.println( "q = " + q );
        System.out.println( "nq = " + nq );
        System.out.println( "v = " + v );
        System.out.println( "vqq = " + vqq );
        System.out.println( "m = " + m );

        System.out.println( "q.mult( v ) = " + q.mult( v ) );
        System.out.println( "nq.mult( v ) = " + nq.mult( v ) );

        System.out.println( " new Quaternion(  ).fromAngles( 0,0,0 ) = " + new Quaternion().fromAngles( 0, 0, 0 ) );
        System.out.println( " new Quaternion(  ).fromAngles( 1,0,0 ) = " + new Quaternion().fromAngles( 1, 0, 0 ) );
        System.out.println( " new Quaternion(  ).fromAngles( 0,1,0 ) = " + new Quaternion().fromAngles( 0, 1, 0 ) );
        System.out.println( " new Quaternion(  ).fromAngles( 0,0,1 ) = " + new Quaternion().fromAngles( 0, 0, 1 ) );
        System.out.println( " new Quaternion(  ).fromAngles( 23,654,-24.1f  ) = " + new Quaternion().fromAngles( 23,654,-24.1f ) );

        System.out.println( "new Quaternion(  ).toRotationMatrix() = " + new Quaternion().toRotationMatrix() );
        System.out.println( "q.toRotationMatrix() = " + q.toRotationMatrix() );
        System.out.println( "nq.toRotationMatrix() = " + nq.toRotationMatrix() );

        System.out.println( "new Quaternion( ).fromRotationMatrix( m ) = " + new Quaternion().fromRotationMatrix( m ) );
        System.out.println( "new Quaternion( ).fromRotationMatrix( m ).toRotationMatrix() = " + new Quaternion().fromRotationMatrix( m ).toRotationMatrix() );

        System.out.println( "new Quaternion( ).slerp( new Quaternion( ), nq, 0.38f ) ) = " + new Quaternion().slerp( new Quaternion(), nq, 0.38f ) );

        System.out.println( "BLAH " + new Quaternion( -0.0f, -0.0024999974f, 0.0f, 0.9999969f ).mult( new Quaternion( -0.9864071f, 0.0016701065f, -0.0050373166f, 0.16423558f ) ) );
    }
}
