// Copyright 2002-2012, University of Colorado
package edu.colorado.phet.fractionsintro.intro.model;

import fj.F;
import lombok.Data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.colorado.phet.common.phetcommon.util.FileUtils;

/**
 * Function wrapper that also records the results of invocation for later testing.
 * <p/>
 * Regression tests for the model:
 * When the user interacts with the sim, the simulation state is taken from an initial state s0 to a final state s1 like this:
 * <p/>
 * s1 = f(s0)
 * <p/>
 * In order to make this testable, these 3 components (s0, f, s1) are recorded to disk, so that the code may be checked at a later time.  These regression tests are generated at runtime as the user/tests plays with the sim, and hence guarantees that the tested inputs and outputs are natural states of the sim (and not, say, unusual corner cases).
 * <p/>
 * This form of testing could also be used to record stepInTime events, but perhaps they should be filtered since there are so many of them.
 * <p/>
 * Tests passing does not guarantee that all features are working perfectly, just that the tested functions produce the same outputs given the same inputs.  Likewise, tests failing do not guarantee that there is a problem, perhaps some allowed change has crept in.  The regression tests are to be used to identify potential problems, but since a significant amount of the sim is outside of the functional model (e.g. the user interface code), user testing will have to be done anyways.
 * <p/>
 * Here are some different storage techniques I investigated, along with their pros and cons.
 * java serializable: no dependencies, brittle to changes
 * java xmlencoder: no dependencies, maybe slow, requires default constructor
 * xstream: lots of jar dependencies, no need to put "implements serializable" or add default constructors, and it works
 * jackson: looks like it requires default constructor?
 * gson: requires no-args constructor and cannot handle circles
 * <p/>
 * Generated tests are large. One run generated 25MB of plaintext xml.
 * Zipped this is 1.3MB
 * 7Zipped it was 157KB
 * <p/>
 * 7Zip has a Java implementation or binding if we decide to use that.
 *
 * @author Sam Reid
 */
public class RegressionTestRecorder<A, B> extends F<A, B> {
    private final F<A, B> function;
    private static final boolean debug = true;
    private static final boolean checkDeterminism = false;
    private static final ExecutorService executorService = Executors.newFixedThreadPool( 1 );
    private static final boolean checkSavedFile = false;

    private RegressionTestRecorder( F<A, B> function ) {
        this.function = function;
    }

    //Convenience construction method to get rid of type parameter boilerplate
    public static <X, Y> RegressionTestRecorder record( F<X, Y> function ) {
        return new RegressionTestRecorder<X, Y>( function );
    }

    @Override public B f( final A a ) {
        final B result = function.f( a );

        if ( checkDeterminism ) {
            checkDeterminism( a, result );
        }

        final Entry<A, B> e = new Entry<A, B>( a, function, result );
        System.out.println( "Recorded function application: " + count + ", function = " + function );

        //Could do in a thread if too expensive
        //Look ma, no synchronization issues since using immutable instance
        executorService.execute( new Runnable() {
            public void run() {
                writeObject( e );
            }
        } );

        return result;
    }

    private void checkDeterminism( A a, B result ) {
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

    //    public static final XStream xstream = new XStream();
    public static final IXStream xstream = new IXStream();

    private void writeXStream( final Entry e ) {// XML encode directly to the file.
        File file = new File( "C:\\Users\\Sam\\Desktop\\tests\\save_" + count + ".xml" );
        String xml = xstream.toXML( e );
        try {
            FileUtils.writeString( file, xml );
        }
        catch ( IOException e1 ) {
            e1.printStackTrace();
        }

        if ( checkSavedFile ) {
            checkSavedFile( e, file );
        }
    }

    private void checkSavedFile( final Entry e, final File file ) {
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
        return (Entry) xstream.fromXML( xmlFile );
    }

    public static void main( String[] args ) {
        try {
            File testDir = new File( "C:\\Users\\Sam\\Desktop\\tests" );
            File[] children = testDir.listFiles( new FilenameFilter() {
                public boolean accept( final File dir, final String name ) {
                    return name.endsWith( ".xml" );
                }
            } );
            Arrays.sort( children, new Comparator<File>() {
                public int compare( final File o1, final File o2 ) {
                    return Double.compare( o1.lastModified(), o2.lastModified() );
                }
            } );
            for ( File file : children ) {
                Entry e = loadXStream( file );
                Object output = e.function.f( e.input );
                boolean equals = output.equals( e.output );
                final PrintStream stream = equals ? System.out : System.err;
                stream.println( ( equals ? "pass" : "fail" ) + ", file = " + file.getName() + " function = " + e.function );
                if ( !equals ) {
                    stream.println( "loadedValue = " + e.output );
                    stream.println( "computedVal = " + output );
                }
            }
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    //Dummy wrapper so we can compile without xstream
    private static class IXStream {
        public String toXML( final Entry e ) {
            return null;
        }

        public Object fromXML( final File xmlFile ) {
            return null;
        }
    }
}