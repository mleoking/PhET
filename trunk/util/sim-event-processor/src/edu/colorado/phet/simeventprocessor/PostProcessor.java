// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import java.util.ArrayList;

import org.jfree.data.xy.XYSeries;

import static java.util.Collections.reverse;
import static java.util.Collections.sort;

/**
 * @author Sam Reid
 */
public class PostProcessor extends Predef {
    public void process( final EventLog eventLog ) {

        println( "#########################" );
        println( "######### Processing tabs" );
        for ( Entry entry : eventLog ) {
            if ( entry.matches( "tab", "pressed" ) ) {
                println( "Switched tab to: " + entry.get( "text" ) + " after " + format( entry.time / 1000 ) + " sec" );
            }
        }

        println();
        println( "#########################" );
        println( "######### Processing deltas" );

        final ArrayList<EntryPair> pairs = new PairwiseProcessor().process( eventLog.removeSystemEvents() );
        sort( pairs );
        reverse( pairs );
        for ( EntryPair pair : pairs ) {
            println( "elapsed time: " + format( pair.elapsedTime ) + ", " + pair._1.brief() + " -> " + pair._2.brief() );
        }

        println();
        println( "#########################" );
        println( "######### Processing coverage" );

        plot( "Events vs time", "Time (sec)", "Events", new XYSeries( "Alice" ) {{
            for ( long time = 0; time < eventLog.getLastTime(); time += 1000 ) {
                int events = eventLog.getNumberOfEvents( time );
                System.out.println( time + "\t" + events );
                add( time / 1000.0, events );
            }
        }} );
    }
}