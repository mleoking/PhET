package util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Enumeration;
import java.util.StringTokenizer;
import java.util.Vector;

public class Debug {

    static final int EXCEPTION_MAGICK_OFFSET = 6;

    public static void traceln( Object str ) {
        System.out.print( str + " @<" );
        traceln( 1, EXCEPTION_MAGICK_OFFSET );
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
             at util.Debug.traceln(Debug.java:19)
             at util.Debug.traceln(Debug.java:14)
             at util.Main.debugTest(Main.java:64)
             at util.Main.mainBAK(Main.java:60)
             */
            System.out.println( v.elementAt( index ) );
        }
    }

}
