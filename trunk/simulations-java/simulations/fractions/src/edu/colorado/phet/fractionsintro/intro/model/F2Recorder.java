// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import fj.F2;
import lombok.Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Arrays;

import edu.colorado.phet.common.phetcommon.util.FileUtils;

import com.thoughtworks.xstream.XStream;

/**
 * Function wrapper that also records the results of invocation for later testing.
 *
 * @author Sam Reid
 */
public class F2Recorder<A, B, C> extends F2<A, B, C> {
    private final F2<A, B, C> function;

    public F2Recorder( F2<A, B, C> function ) {
        this.function = function;
    }

    //Convenience method to get rid of type parameter boilerplate
    public static <X, Y, Z> F2Recorder record( F2<X, Y, Z> function ) {
        return new F2Recorder<X, Y, Z>( function );
    }

    @Override public C f( final A a, final B b ) {
        final C result = function.f( a, b );

        Entry<A, B, C> e = new Entry<A, B, C>( function, a, b, result );
        System.out.println( "Recorded function application: " + count + ", function = " + function + ", inputType = " + Arrays.asList( b.getClass().getTypeParameters() ) );

        //Could do in a thread if too expensive
        writeObject( e );

        return result;
    }

    public static @Data class Entry<A, B, C> implements Serializable {
        final F2<A, B, C> function;
        final A init;
        final B value;
        final C result;
    }

    public static void main( String[] args ) {
        try {
            for ( int index = 0; index < 100; index++ ) {
                final File xmlFile = new File( "C:\\Users\\Sam\\Desktop\\tests\\save_" + index + ".xml" );
                if ( xmlFile.exists() ) {
                    Entry e = loadXStream( xmlFile );
                    Object newResult = e.function.f( e.init, e.value );
                    boolean equals = newResult.equals( e.result );
//                System.out.println( "equals = " + equals );
                    final PrintStream stream = equals ? System.out : System.err;
                    stream.println( "index = " + index + " function = " + e.function + ", argument = " + e.value );
                }
                else {
                    break;
                }
            }
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public static int count = 0;

    //XMLEncoder failed with Caused by: java.lang.RuntimeException: failed to evaluate: <unbound>=Class.new();
    public void writeObject( Object e ) {
        writeXStream( e );
        count++;
    }

    private void writeXStream( final Object e ) {// XML encode directly to the file.
        File file = new File( "C:\\Users\\Sam\\Desktop\\tests\\save_" + count + ".xml" );
        XStream xstream = new XStream();
        String xml = xstream.toXML( e );
        try {
            FileUtils.writeString( file, xml );
        }
        catch ( IOException e1 ) {
            e1.printStackTrace();
        }
    }

    public static Entry loadXStream( File xmlFile ) throws FileNotFoundException {
        XStream xstream = new XStream();
        return (Entry) xstream.fromXML( xmlFile );
    }
}