// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import fj.F;
import fj.Ord;
import fj.data.List;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import org.jfree.data.xy.XYSeries;

import edu.colorado.phet.common.phetcommon.util.FileUtils;
import edu.colorado.phet.common.phetcommon.util.function.Function1;

import static fj.Function.curry;
import static fj.data.List.iterableList;

/**
 * @author Sam Reid
 */
public class SummarizeData extends Processor {

    private final long EPOCH_START = 0L;

    public static void main( String[] args ) throws IOException {
        new SummarizeData().processDir( new File( "C:\\Users\\Sam\\Desktop\\file-vi" ) );
    }

    public static class EMMondayFilter extends F<JavaEventLog, Boolean> {
        long startTime = 1320692751844L;
        long endTime = 1320696963181L;
        String simName = "Molecule Shapes";

        @Override public Boolean f( JavaEventLog entry ) {
            return
                    entry.getServerStartTime() >= startTime && entry.getServerStartTime() <= endTime
                    && entry.getSimName().equals( simName )
                    && entry.getStudy().equals( "utah" )
                    && !entry.getMachineID().startsWith( "samreid" )
                    && !entry.getID().startsWith( "samreid" )
                    ;
        }
    }

    public static class KLMondayFilter extends F<JavaEventLog, Boolean> {
        long startTime = 1320692751844L;
        private String simName;

        //        long endTime = 1320696963181L;
        public KLMondayFilter( String simName ) {
            this.simName = simName;
        }

        @Override public Boolean f( JavaEventLog entry ) {
            return
                    entry.getServerStartTime() >= startTime
//                    && entry.getServerStartTime() <= endTime
                    && entry.getSimName().equals( simName )
                    && entry.getStudy().equals( "colorado" )
                    && !entry.getMachineID().startsWith( "samreid" )
                    && !entry.getID().startsWith( "samreid" )
                    && !entry.getID().equals( "" )
                    && !entry.getID().equals( "null" )
                    && !entry.getID().equals( "None" )
                    ;
        }
    }

    @Override public void process( ArrayList<JavaEventLog> all ) {
        System.out.println( "Found " + all.size() + " event logs" );
        List<JavaEventLog> list = iterableList( all );

//        final String simName = "Molecule Shapes";
        final String simName = "Molecule Polarity";
        List<JavaEventLog> recent = list.filter( new KLMondayFilter( simName ) );

//        List<EventLog> recent = list.filter( new F<EventLog, Boolean>() {
//            @Override public Boolean f( EventLog entry ) {
//                return entry.getServerStartTime() >= EPOCH_START
//                       && entry.getStudy().equals( "utah" )
////                       && !entry.getID().equals( new Option.Some<String>( "samreid" ) )
////                       && entry.minutesUsed() >= 0
////                       && entry.getSimName().equals( "Molecule Shapes" )
////                       && entry.getSimName().equals( "Molecule Polarity" )
//                        ;
//            }
//        } );

        recent = recent.sort( Ord.ord( curry( new SimpleComparator<JavaEventLog>( new F<JavaEventLog, Comparable>() {
            @Override public Comparable f( JavaEventLog e ) {
                return e.getServerStartTime();
//                return e.getID().get();
            }
        } ) ) ) );

        File destDir = new File( "C:/Users/Sam/Desktop/destDir2-" + simName );
        destDir.mkdirs();
        for ( JavaEventLog entry : recent ) {
            try {
                FileUtils.copyToDir( entry.sourceFile, destDir );
                System.out.println( "Copied " + entry.sourceFile + ", to " + destDir );
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
        }

        for ( JavaEventLog entry : recent ) {
            System.out.println( entry.brief() );
        }

        int count = 0;
        for ( JavaEventLog entry : recent ) {
            count = count + entry.size();
        }
        System.out.println( "count = " + count );

        final List<JavaEventLog> finalRecent = recent;
        ArrayList<XYSeries> seriesList = new ArrayList<XYSeries>() {{
            int count = 0;
            for ( final JavaEventLog eventLog : finalRecent ) {
                count++;
                final XYSeries xySeries = new XYSeries( "Student " + eventLog.getID() ) {{
                    for ( long time = 0; time < eventLog.getLastTime(); time += 500 ) {
                        int events = eventLog.getNumberOfEvents( time );
                        add( time / 1000.0 / 60.0, events );
                    }
                }};
                add( xySeries );
            }
        }};

        plot( "Events vs time for " + simName, "Time (minutes)", "Events", seriesList.toArray( new XYSeries[seriesList.size()] ) );

        //count unique machine ID's
        HashSet<String> machineIDs = new HashSet<String>();
        for ( JavaEventLog entry : recent ) {
            machineIDs.add( entry.getMachineID() );
        }
        System.out.println( "Since " + new Date( EPOCH_START ) + ", received " + machineIDs.size() + " different machine ID's" );

        HashSet<String> ids = new HashSet<String>();
        for ( JavaEventLog entry : recent ) {
            ids.add( entry.getID() );
        }
        System.out.println( "Received " + ids.size() + " ids: " + ids );

        ArrayList<String> idList = new ArrayList<String>( ids );
        Collections.sort( idList );
        for ( String s : idList ) {
            System.out.println( s );
        }

        for ( String machineID : machineIDs ) {
            System.out.println( "Machine ID: " + machineID );
            for ( JavaEventLog entry : recent ) {
                if ( entry.getMachineID().equals( machineID ) ) {
                    println( "\t" + entry.getID() );
                }
            }
        }

        HashSet<String> errSystem = new HashSet<String>();
        for ( JavaEventLog entry : recent ) {
            JavaEventLog out = entry.keepItems( new Function1<JavaEntry, Boolean>() {
                public Boolean apply( JavaEntry entry ) {
                    return entry.matches( "system", "erred" );
                }
            } );
            if ( out.size() > 0 ) {
                System.out.println( "erred on machine: " + entry.getMachineID() + ": " + out );
                errSystem.add( entry.getMachineID() );
            }
        }
        System.out.println( "erred on machines: " + errSystem );
        ArrayList<String> s = new ArrayList<String>( errSystem );
        for ( int i = 0; i < errSystem.size(); i++ ) {
            System.out.println( ( i + 1 ) + ": " + s.get( i ) );
        }
    }

    @Override public void process( JavaEventLog eventLog ) {
    }
}
