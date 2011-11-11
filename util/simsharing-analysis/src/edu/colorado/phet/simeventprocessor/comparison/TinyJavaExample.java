// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor.comparison;

import java.io.IOException;

import edu.colorado.phet.simeventprocessor.JavaEventLog;
import edu.colorado.phet.simeventprocessor.Processor;

/**
 * @author Sam Reid
 */
public class TinyJavaExample {
    public static void main( String[] args ) throws IOException {
        //To answer the question of how many lines of code in Java to write the equivalent scala expression of:
        //51 characters in one line
        //res0.filter(_.day=="11-10-2011").map(_.user).sorted

        JavaEventLog[] res0 = Processor.load( "test" );

        //193 characters in 7 lines
//        ArrayList<String> filtered = new ArrayList<String>();
//        for ( JavaEventLog log : res0 ) {
//            if ( log.day.equals( "11-10-2011" ) ) {
//                filtered.add( log.user );
//            }
//        }
//        Collections.sort( filtered );

        //character compression ratio: 26% of the size of Java
        //line compression ratio: 14% the size of Java
    }
}
