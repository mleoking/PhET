// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.data.xy.XYSeries;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.param;
import static java.util.Collections.reverse;
import static java.util.Collections.sort;

/**
 * @author Sam Reid
 */
public class PostProcessor extends Processor {
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
            println( "elapsed time: " + format( pair.elapsedTime / 1000.0 ) + " sec, " + pair._1.brief() + " -> " + pair._2.brief() );
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

        //Events to search for in Molecule Polarity Tab 1
        final EntryList eventsOfInterest = new EntryList() {{
            add( "check box", "pressed", param( "text", "Bond Dipole" ) );
            add( "check box", "pressed", param( "text", "Partial Charges" ) );
            add( "check box", "pressed", param( "text", "Bond Character" ) );

            add( "radio button", "pressed", param( "description", "Surface type" ), param( "value", "ELECTROSTATIC_POTENTIAL" ) );
            add( "radio button", "pressed", param( "description", "Surface type" ), param( "value", "ELECTROSTATIC_POTENTIAL" ) );
            add( "radio button", "pressed", param( "description", "Surface type" ), param( "value", "NONE" ) );

            add( "radio button", "pressed", param( "description", "Electric field on" ), param( "value", "true" ) );
            add( "radio button", "pressed", param( "description", "Electric field on" ), param( "value", "false" ) );

            add( "mouse", "dragged", param( "atom", "A" ) );
            add( "mouse", "dragged", param( "atom", "B" ) );
        }};

        plot( "Events of interest", "Time (sec)", "Events", new XYSeries( "Alice" ) {{
            for ( long time = 0; time < eventLog.getLastTime(); time += 1000 ) {
                int events = eventLog.getNumberOfEvents( time, eventsOfInterest );
                System.out.println( time + "\t" + events );
                add( time / 1000.0, events );
            }
        }} );

        EntryList events = eventLog.getEvents( eventsOfInterest );
        System.out.println( "At the end of the sim, the user had played with " + events.size() + " / " + eventsOfInterest.size() + " interesting events: " + events );

        ArrayList<Entry> copy = new ArrayList<Entry>( eventsOfInterest );
        copy.removeAll( events );
        System.out.println( "Things the user didn't do: " + copy );
    }

    public static void main( String[] args ) throws IOException {
        new PostProcessor().process( new File( "C:\\Users\\Sam\\Desktop\\biglog3.txt" ) );
    }
}