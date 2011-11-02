// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.data.xy.XYSeries;

import static edu.colorado.phet.simeventprocessor.MoleculePolarityEventsOfInterest.getMoleculePolarityEventsOfInterest;
import static java.util.Collections.reverse;
import static java.util.Collections.sort;

/**
 * Example code for processing the results of simulation runs.
 *
 * @author Sam Reid
 */
public class PostProcessor extends Processor {
    public void process( final EventLog eventLog ) {

        println( "#########################" );
        println( "######### Processing tabs" );
        for ( Entry entry : eventLog ) {
            if ( entry.matches( "tab", "pressed" ) ) {
                println( "Switched tab to: " + entry.get( "text" ) + " after " + entry.time + " sec" );
            }
        }

        println();
        println( "#########################" );
        println( "######### Processing deltas" );

        final ArrayList<EntryPair> pairs = new PairwiseProcessor().process( eventLog.getWithoutSystemEvents() );
        sort( pairs );
        reverse( pairs );
        for ( EntryPair pair : pairs.subList( 0, 10 ) ) {
            println( "elapsed time: " + format( pair.elapsedTimeMillis / 1000.0 ) + " sec, " + pair._1.brief() + " -> " + pair._2.brief() );
        }

        println();
        println( "#########################" );
        println( "######### Processing coverage" );

        final EntryList eventsOfInterest = getMoleculePolarityEventsOfInterest();
        EntryList userEvents = eventLog.find( eventsOfInterest );
        System.out.println( "At the end of the sim, the user had played with " + userEvents.size() + " / " + eventsOfInterest.size() + " interesting events." );

        ArrayList<Entry> userMissed = new ArrayList<Entry>( eventsOfInterest );
        userMissed.removeAll( userEvents );
        System.out.println( "Things the user didn't do: " + userMissed );
    }

    @Override public void process( final ArrayList<EventLog> all ) {
        ArrayList<XYSeries> seriesList = new ArrayList<XYSeries>() {{
            for ( final EventLog eventLog : all ) {
                final XYSeries xySeries = new XYSeries( "Student " + all.indexOf( eventLog ) ) {{
                    for ( long time = 0; time < eventLog.getLastTime(); time += 1000 ) {
                        int events = eventLog.getNumberOfEvents( time );
                        add( time / 1000.0, events );
                    }
                }};
                add( xySeries );
            }
        }};

        plot( "Events vs time", "Time (sec)", "Events", seriesList.toArray( new XYSeries[seriesList.size()] ) );

        ArrayList<XYSeries> xySeriesList = new ArrayList<XYSeries>() {{
            for ( final EventLog eventLog : all ) {

                final XYSeries series = new XYSeries( "Student " + all.indexOf( eventLog ) ) {{
                    for ( long time = 0; time < eventLog.getLastTime(); time += 1000 ) {
                        int events = eventLog.getNumberOfEvents( time, getMoleculePolarityEventsOfInterest() );
                        add( time / 1000.0, events );
                    }
                }};
                add( series );
            }
        }};

        plot( "Events of interest", "Time (sec)", "Events", xySeriesList.toArray( new XYSeries[xySeriesList.size()] ) );
    }

    public static void main( String[] args ) throws IOException {
        new PostProcessor().process(
                new File( "C:\\Users\\Sam\\Desktop\\biglog4.txt" ),
                new File( "C:\\Users\\Sam\\Desktop\\biglog5.txt" ),
                new File( "C:\\Users\\Sam\\Desktop\\biglog6.txt" ) );
    }
}