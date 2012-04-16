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
    private static final boolean debug = true;

    public F2Recorder( F2<A, B, C> function ) {
        this.function = function;
    }

    //Convenience method to get rid of type parameter boilerplate
    public static <X, Y, Z> F2Recorder record( F2<X, Y, Z> function ) {
        return new F2Recorder<X, Y, Z>( function );
    }

    @Override public C f( final A a, final B b ) {
        final C result = function.f( a, b );
        final C result2 = function.f( a, b );
        if ( !result.equals( result2 ) ) {
            System.out.println( "Nondeterministic method. warning!" );
            System.out.println( "a = " + result );
            System.out.println( "b = " + result2 );
            boolean equal = result.equals( result2 );
            System.out.println( "equal = " + equal );
            function.f( a, b );
            for ( int i = 0; i < 1; i++ ) {
                IntroState x = (IntroState) function.f( a, b );
                IntroState y = (IntroState) function.f( a, b );
                System.out.println( "containerSetEquals = " + x.containerSet.equals( y.containerSet ) );
                System.out.println( "pieSetEquals = " + x.pieSet.equals( y.pieSet ) );
                System.out.println( "pieSet.Slices.Equals = " + x.pieSet.slices.equals( y.pieSet.slices ) );
                System.out.println( "pieSet.SliceFactory.Equals = " + x.pieSet.sliceFactory.equals( y.pieSet.sliceFactory ) );
            }
        }

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
            boolean equals = loaded.result.equals( e.result );
            System.out.println( "compared xstream, equals = " + equals );
            if ( !equals ) {
                System.out.println( "a = " + e.result );
                System.out.println( "b = " + loaded.result );
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
                    Object newResult = e.function.f( e.init, e.value );
                    boolean equals = newResult.equals( e.result );
                    final PrintStream stream = equals ? System.out : System.err;
                    stream.println( ( equals ? "pass" : "fail" ) + ", index = " + index + " function = " + e.function + ", argument = " + e.value );
                    if ( !equals ) {
                        if ( debug ) {
                            newResult.equals( e.result );
                        }
                        stream.println( "loadedValue = " + e.result );
                        stream.println( "computedVal = " + newResult );
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