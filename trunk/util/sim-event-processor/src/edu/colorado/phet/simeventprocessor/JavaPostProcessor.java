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
public class JavaPostProcessor extends Processor {
    public void process( final EventLog eventLog ) {

        //See how often users switched tabs
        println( "#########################" );
        println( "######### Processing tabs" );
        for ( Entry entry : eventLog ) {
            if ( entry.matches( "tab", "pressed" ) ) {
                println( "Switched tab to: " + entry.get( "text" ) + " after " + entry.time + " sec" );
            }
        }

        //See how long the user paused between doing things
        println();
        println( "#########################" );
        println( "######### Processing deltas" );

        ArrayList<EntryPair> list = pairs( eventLog );
        sort( list );
        reverse( list );
        for ( EntryPair pair : list.subList( 0, 10 ) ) {
            println( "elapsed time: " + pair.time + " sec, " + pair.brief );
        }

        //See if the user used lots of the important controls
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

    //Show plots of the numbers of controls used vs time.
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
        new JavaPostProcessor().process(
                new File( "C:\\Users\\Sam\\Desktop\\tablog-1.txt" )
        );
    }
}