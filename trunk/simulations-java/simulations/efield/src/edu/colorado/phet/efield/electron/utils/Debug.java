// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.electron.utils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

// Referenced classes of package util:
//            ThreadHelper

public class Debug {

    public static void traceln( Object obj ) {
        System.out.print( obj + " @<" );
        traceln( 1, 6 );
    }

    private static Vector toVector( Enumeration enumeration ) {
        Vector vector = new Vector();
        for ( ; enumeration.hasMoreElements(); vector.add( enumeration.nextElement() ) ) {
            ;
        }
        return vector;
    }

    private static void traceln( int i, int j ) {
        Exception exception = new Exception();
        StringWriter stringwriter = new StringWriter();
        PrintWriter printwriter = new PrintWriter( stringwriter, true );
        exception.printStackTrace( printwriter );
        String s = stringwriter.toString();
        StringTokenizer stringtokenizer = new StringTokenizer( s );
        Vector vector = toVector( stringtokenizer );
        for ( int k = 0; k < Math.min( i, vector.size() ); k++ ) {
            int l = k * 2 + j;
            System.out.println( vector.elementAt( l ) );
        }

    }

//    static
//    {
//        in = new BufferedReader(new InputStreamReader(System.in));
//    }
}
