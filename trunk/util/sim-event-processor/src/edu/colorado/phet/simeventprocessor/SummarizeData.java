// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventprocessor;

import fj.F;
import fj.F2;
import fj.Ord;
import fj.Ordering;
import fj.data.List;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
                return entry.getServerStartTime() >= 1320434917933L && !entry.getID().equals( new Option.Some<String>( "samreid" ) ) && entry.minutesUsed() >= 2;
            }
        } );

        recent = recent.sort( Ord.ord( curry( new F2<EventLog, EventLog, Ordering>() {
            @Override public Ordering f( EventLog a, EventLog b ) {
                return Ord.<Comparable>comparableOrd().compare( a.getServerStartTime(), b.getServerStartTime() );
            }
        } ) ) );


        for ( EventLog entry : recent ) {
            System.out.println( entry.brief() );
        }
    }

    @Override public void process( EventLog eventLog ) {
    }
}
