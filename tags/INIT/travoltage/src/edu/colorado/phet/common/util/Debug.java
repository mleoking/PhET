package edu.colorado.phet.common.util;

import java.io.*;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

public class Debug {
    private static final BufferedReader in = new BufferedReader( new InputStreamReader( System.in ) );

    boolean debug = true;
    static final int EXCEPTION_MAGICK_OFFSET = 6;

    public static void showProgress( int i, int j ) {
        System.out.println( i + "/" + j + " at " + getTrace( 1, EXCEPTION_MAGICK_OFFSET ) );
    }

    public static void traceln() {
        /**Can't be one, since this method shows up in the stack.*/
        traceln( 1, EXCEPTION_MAGICK_OFFSET );
    }

    public static void traceln( Object str, int numLines ) {
        System.out.print( str + " @<" );
        traceln( numLines, EXCEPTION_MAGICK_OFFSET );
    }

    public static void traceln( Object str ) {
        System.out.print( str + " @<" );
        traceln( 1, EXCEPTION_MAGICK_OFFSET );
    }

    /**
     * Print numLines of the stack trace of the calling method.
     */
    public static void traceln( int numLines ) {
        traceln( numLines, EXCEPTION_MAGICK_OFFSET );
    }

    public static String getStackTrace( Throwable e ) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw, true );
        e.printStackTrace( pw );
        //System.out.println(sw);
        //System.out.println(pw);
        //System.out.println(sw.getBuffer());
        String str = sw.toString();
        return str;
    }

    private static String getTrace( int numLines, int offset ) {
        Exception e = new Exception();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw, true );
        e.printStackTrace( pw );
        //System.out.println(sw);
        //System.out.println(pw);
        //System.out.println(sw.getBuffer());
        String str = sw.toString();
        StringTokenizer stk = new StringTokenizer( str );//,"()");
        Vector v = toVector( stk );
        String line = "";
        for( int i = 0; i < Math.min( numLines, v.size() ); i++ ) {
            int index = i * 2 + offset;
            /**Since the output is of the form
             java.lang.Exception
             at edu.colorado.phet.common.util.Debug.traceln(Debug.java:19)
             at edu.colorado.phet.common.util.Debug.traceln(Debug.java:14)
             at edu.colorado.phet.common.util.Main.debugTest(Main.java:64)
             at edu.colorado.phet.common.util.Main.main(Main.java:60)
             */
            line += v.elementAt( index ).toString();
        }
        return line;
    }

    private static Vector toVector( Enumeration e ) {
        Vector v = new Vector();
        while( e.hasMoreElements() ) {
            v.add( e.nextElement() );
        }
        return v;
    }

    private static void traceln( int numLines, int offset ) {
        Exception e = new Exception();
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter( sw, true );
        e.printStackTrace( pw );
        //System.out.println(sw);
        //System.out.println(pw);
        //System.out.println(sw.getBuffer());
        String str = sw.toString();
        StringTokenizer stk = new StringTokenizer( str );//,"()");
        Vector v = toVector( stk );
        for( int i = 0; i < Math.min( numLines, v.size() ); i++ ) {
            int index = i * 2 + offset;
            /**Since the output is of the form
             java.lang.Exception
             at edu.colorado.phet.common.util.Debug.traceln(Debug.java:19)
             at edu.colorado.phet.common.util.Debug.traceln(Debug.java:14)
             at edu.colorado.phet.common.util.Main.debugTest(Main.java:64)
             at edu.colorado.phet.common.util.Main.main(Main.java:60)
             */
            System.out.println( v.elementAt( index ) );
        }
    }

    public void println( Object obj ) {
        if( debug ) {
            System.out.println( obj );
        }
    }

    public static void waitEnter( Object obj ) {
        System.out.println( obj );
        waitEnter();
    }

    public static void waitEnter() {
        System.out.println( "Press Enter..." );
        try {
            in.readLine();
        }
        catch( IOException e ) {
        }
    }

    public static void errout( Object obj ) {
        errout( obj.toString() );
    }

    public static void errout( String str ) {
        System.err.println( str );
        System.out.println( str );
    }

    public static void pause( Object obj ) {
        pause( obj.toString() );
    }

    public static void pause( String str ) {
        System.out.println( str );
        edu.colorado.phet.common.util.ThreadHelper.nap( 1000 );
    }
}
