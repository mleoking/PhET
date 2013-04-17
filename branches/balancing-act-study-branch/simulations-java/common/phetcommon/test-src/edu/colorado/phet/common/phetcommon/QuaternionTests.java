// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.common.phetcommon;

import junit.framework.TestCase;

import edu.colorado.phet.common.phetcommon.math.Matrix3F;
import edu.colorado.phet.common.phetcommon.math.QuaternionF;
import edu.colorado.phet.common.phetcommon.math.vector.Vector3F;

public class QuaternionTests extends TestCase {
    public void testMultiply() {
        assertEquals( new QuaternionF( -120, 108, 70, 44 ), new QuaternionF( 5, 2, 7, 4 ).times( new QuaternionF( 3, 8, 9, 14 ) ) );
    }

    // tests validated by using the similar JME representations and spot-checking operations with Mathematica
    public void testBattery() {
        QuaternionF q = new QuaternionF( -5, -3.64f, 154, 5 ).normalized();
        Vector3F v = new Vector3F( -105, 23, 10 );
        Vector3F vqq = new Vector3F( 4, 76, -23 );
        Matrix3F m = Matrix3F.rotateAToB( vqq.normalized(), v.normalized() );

        assertEquals( q.times( v ), new Vector3F( 102.44118, -30.337223, 15.474403 ) );

        assertEquals( new QuaternionF().toRotationMatrix(), Matrix3F.IDENTITY );
        assertEquals( QuaternionF.fromEulerAngles( 0, 0, 0 ).toRotationMatrix(), Matrix3F.IDENTITY );

        assertEquals( QuaternionF.fromRotationMatrix( m ), new QuaternionF( 0.099992275f, 0.18423715f, 0.6261735f, 0.75097597f ) );

        assertEquals( QuaternionF.slerp( new QuaternionF(), q, 0.38f ), new QuaternionF( -0.017902726f, -0.013033186f, 0.551404f, 0.8339444f ) );

        float a = 0.47942555f;
        float b = 0.87758255f;

        assertEquals( QuaternionF.fromEulerAngles( 0, 0, 0 ), new QuaternionF( 0, 0, 0, 1 ) );
        assertEquals( QuaternionF.fromEulerAngles( 1, 0, 0 ), new QuaternionF( a, 0, 0, b ) );
        assertEquals( QuaternionF.fromEulerAngles( 0, 1, 0 ), new QuaternionF( 0, a, 0, b ) );
        assertEquals( QuaternionF.fromEulerAngles( 0, 0, 1 ), new QuaternionF( 0, 0, a, b ) );
        assertEquals( QuaternionF.fromEulerAngles( 23, 654, -24.1f ), new QuaternionF( -0.66818273f, -0.30219668f, 0.43596053f, 0.52167755f ) );

        assertEquals( q.toRotationMatrix(), Matrix3F.rowMajor(
                -0.99579453f, -0.063231595f, -0.06629308f,
                0.06629308f, -0.9967829f, -0.045044314f,
                -0.063231595f, -0.04924966f, 0.99678296f
        ) );

        // make sure that the to/from rotation matrix operations are completely reversible
        assertTrue( m.equalsWithEpsilon( QuaternionF.fromRotationMatrix( m ).toRotationMatrix(), 0.000001f ) );
        assertEquals( QuaternionF.fromRotationMatrix( q.toRotationMatrix() ), q );

        // TODO: decide on what style of multiplication we want here (mathematics standard, or JME-style). see details in QuaternionF
        QuaternionF aa = new QuaternionF( -0.0f, -0.0024999974f, 0.0f, 0.9999969f );
        QuaternionF bb = new QuaternionF( -0.9864071f, 0.0016701065f, -0.0050373166f, 0.16423558f );


//        System.out.println( "aa.normalized() = " + aa.normalized() );
//        System.out.println( "bb.normalized() = " + bb.normalized() );
//        System.out.println( "BLAH " + aa.times( bb ) );
    }
}
