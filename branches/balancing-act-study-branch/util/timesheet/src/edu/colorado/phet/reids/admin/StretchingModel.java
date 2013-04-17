// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.reids.admin;

/**
 * @author Sam Reid
 */
public class StretchingModel {
    public long getTimeSinceBeginningOfLastSession( TimesheetModel timesheetModel ) {
        long elapsed = 0;
        for ( int i = timesheetModel.getEntryCount() - 1; i >= 0; i-- ) {
            Entry entry = timesheetModel.getEntry( i );
            elapsed += entry.getElapsedSeconds();
            if ( entryMatches( entry ) ) {
                break;
            }
        }
        return elapsed;
    }

    public boolean entryMatches( Entry entry ) {
        return entry.getNotes().equals( "stretching & exercise" );
    }
}
