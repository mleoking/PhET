// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import fj.F;
import lombok.Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;

import edu.colorado.phet.common.phetcommon.util.FileUtils;

import com.thoughtworks.xstream.XStream;

/**
 * Function wrapper that also records the results of invocation for later testing.
 *
 * @author Sam Reid
 */
public class FRecorder<A, B> extends F<A, B> {
    private final F<A, B> function;
    private static final boolean debug = true;

    public FRecorder( F<A, B> function ) {
        this.function = function;
    }

    //Convenience method to get rid of type parameter boilerplate
    public static <X, Y> FRecorder record( F<X, Y> function ) {
        return new FRecorder<X, Y>( function );
    }

    @Override public B f( final A a ) {
        final B result = function.f( a );
        final B result2 = function.f( a );
        if ( !result.equals( result2 ) ) {
            System.out.println( "Nondeterministic method. warning!" );
            System.out.println( "a = " + result );
            System.out.println( "b = " + result2 );
            boolean equal = result.equals( result2 );
            System.out.println( "equal = " + equal );
            function.f( a );
            for ( int i = 0; i < 1; i++ ) {
                IntroState x = (IntroState) function.f( a );
                IntroState y = (IntroState) function.f( a );
                System.out.println( "containerSetEquals = " + x.containerSet.equals( y.containerSet ) );
                System.out.println( "pieSetEquals = " + x.pieSet.equals( y.pieSet ) );
                System.out.println( "pieSet.Slices.Equals = " + x.pieSet.slices.equals( y.pieSet.slices ) );
                System.out.println( "pieSet.SliceFactory.Equals = " + x.pieSet.sliceFactory.equals( y.pieSet.sliceFactory ) );
            }
        }

        Entry<A, B> e = new Entry<A, B>( a, function, result );
        System.out.println( "Recorded function application: " + count + ", function = " + function );

        //Could do in a thread if too expensive
        writeObject( e );

        return result;
    }

    public static @Data class Entry<A, B> implements Serializable {
        final A input;
        final F<A, B> function;
        final B output;
    }

    public static int count = 0;

    //XMLEncoder failed with Caused by: java.lang.RuntimeException: failed to evaluate: <unbound>=Class.new();
    public void writeObject( Entry e ) {
        writeXStream( e );
        count++;
    }

    private void writeXStream( final Entry e ) {// XML encode directly to the file.
        File file = new File( "C:\\Users\\Sam\\Desktop\\tests\\save_" + count + ".xml" );
        XStream xstream = new XStream();
        String xml = xstream.toXML( e );
        try {
            FileUtils.writeString( file, xml );
        }
        catch ( IOException e1 ) {
            e1.printStackTrace();
        }

        try {
            Entry loaded = loadXStream( file );
            boolean equals = loaded.output.equals( e.output );
            System.out.println( "compared xstream, equals = " + equals );
            if ( !equals ) {
                System.out.println( "a = " + e.output );
                System.out.println( "b = " + loaded.output );
            }
        }
        catch ( FileNotFoundException e1 ) {
            e1.printStackTrace();
        }
    }

    public static Entry loadXStream( File xmlFile ) throws FileNotFoundException {
        XStream xstream = new XStream();
        return (Entry) xstream.fromXML( xmlFile );
    }

    public static void main( String[] args ) {
        try {
            for ( int index = 0; index < 100; index++ ) {
                final File xmlFile = new File( "C:\\Users\\Sam\\Desktop\\tests\\save_" + index + ".xml" );
                if ( xmlFile.exists() ) {
                    Entry e = loadXStream( xmlFile );
                    Object output = e.function.f( e.input );
                    boolean equals = output.equals( e.output );
                    final PrintStream stream = equals ? System.out : System.err;
                    stream.println( ( equals ? "pass" : "fail" ) + ", index = " + index + " function = " + e.function );
                    if ( !equals ) {
                        if ( debug ) {
                            output.equals( e.output );
                        }
                        stream.println( "loadedValue = " + e.output );
                        stream.println( "computedVal = " + output );
                    }
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
}