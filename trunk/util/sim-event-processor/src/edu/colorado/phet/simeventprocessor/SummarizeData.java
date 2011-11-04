// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import fj.F;
import fj.Ord;
import fj.data.List;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.jfree.data.xy.XYSeries;

import edu.colorado.phet.common.phetcommon.util.Option;

import static fj.Function.curry;
import static fj.data.List.iterableList;

/**
 * @author Sam Reid
 */
public class SummarizeData extends Processor {
    public static void main( String[] args ) throws IOException {
        new SummarizeData().process( new File( "C:\\Users\\Sam\\Desktop\\tarred" ).listFiles() );
    }

    @Override public void process( ArrayList<EventLog> all ) {
        System.out.println( "Found " + all.size() + " event logs" );
        List<EventLog> list = iterableList( all );
        List<EventLog> recent = list.filter( new F<EventLog, Boolean>() {
            @Override public Boolean f( EventLog entry ) {
                return entry.getServerStartTime() >= 1320434917933L &&
                       !entry.getID().equals( new Option.Some<String>( "samreid" ) ) && entry.minutesUsed() >= 2;
//                &&
//                       entry.getSimName().equals( "Molecule Shapes" );
//                       entry.getSimName().equals( "Molecule Polarity" );

            }
        } );

        recent = recent.sort( Ord.ord( curry( new SimpleComparator<EventLog>( new F<EventLog, Comparable>() {
            @Override public Comparable f( EventLog e ) {
                return e.getServerStartTime();
//                return e.getID().get();
            }
        } ) ) ) );

        for ( EventLog entry : recent ) {
            System.out.println( entry.brief() );
        }

        final List<EventLog> finalRecent = recent;
        ArrayList<XYSeries> seriesList = new ArrayList<XYSeries>() {{
            int count = 0;
            for ( final EventLog eventLog : finalRecent ) {
                count++;
                final XYSeries xySeries = new XYSeries( "Student " + eventLog.getID().get() ) {{
                    for ( long time = 0; time < eventLog.getLastTime(); time += 500 ) {
                        int events = eventLog.getNumberOfEvents( time );
                        add( time / 1000.0 / 60.0, events );
                    }
                }};
                add( xySeries );
            }
        }};

        plot( "Events vs time", "Time (minutes)", "Events", seriesList.toArray( new XYSeries[seriesList.size()] ) );

    }

    @Override public void process( EventLog eventLog ) {
    }
}
