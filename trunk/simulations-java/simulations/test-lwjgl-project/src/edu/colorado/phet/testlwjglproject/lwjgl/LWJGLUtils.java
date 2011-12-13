// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.testlwjglproject.lwjgl;

import java.awt.Dimension;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.DoubleBuffer;

public class LWJGLUtils {
    public static DoubleBuffer doubleBuffer( int size ) {
        return ByteBuffer.allocateDirect( 8 * size ).order( ByteOrder.nativeOrder() ).asDoubleBuffer();
    }

    public static int toPowerOf2( int n ) {
        int result = 1;
        while ( result < n ) {
            result *= 2;
        }
        return result;
    }

    public static Dimension toPowerOf2( Dimension dim ) {
        return new Dimension( toPowerOf2( dim.width ), toPowerOf2( dim.height ) );
    }

    public static boolean isPowerOf2( int n ) {
        return n == toPowerOf2( n );
    }
}
