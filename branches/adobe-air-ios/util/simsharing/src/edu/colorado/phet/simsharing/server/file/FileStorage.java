// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simsharing.server.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.StringTokenizer;

import edu.colorado.phet.common.phetcommon.simsharing.state.SimState;
import edu.colorado.phet.simsharing.messages.AddSamples;
import edu.colorado.phet.simsharing.messages.SampleBatch;
import edu.colorado.phet.simsharing.messages.SessionID;
import edu.colorado.phet.simsharing.messages.SessionRecord;
import edu.colorado.phet.simsharing.messages.StudentSummary;
import edu.colorado.phet.simsharing.server.Storage;
import edu.colorado.phet.simsharing.teacher.SessionList;
import edu.colorado.phet.simsharing.teacher.StudentList;

/**
 * @author Sam Reid
 */
public class FileStorage implements Storage {
    private File root;

    public FileStorage( File root ) {
        this.root = root;
        root.mkdirs();
    }

    public SimState getSample( SessionID sessionID, int index ) {
        File sessionDir = getSessionDir( sessionID );
        return (SimState) read( new File( sessionDir, "sample_" + index + ".ser" ) );
    }

    private File getSessionDir( SessionID sessionID ) {
        return new File( root, toFilename( sessionID ) );
    }

    private String toFilename( SessionID sessionID ) {
        return "session-" + sessionID.getIndex();
    }

    public int getNumberSamples( SessionID sessionID ) {
        return getSessionDir( sessionID ).listFiles( new FilenameFilter() {
            public boolean accept( File dir, String name ) {
                return name.startsWith( "sample_" );
            }
        } ).length;
    }

    public int getNumberSessions() {
        return root.listFiles( new FilenameFilter() {
            public boolean accept( File dir, String name ) {
                return name.startsWith( "session" );
            }
        } ).length;
    }

    public SessionList listAllSessions() {
        return new SessionList( new ArrayList<SessionRecord>() {{
            for ( int i = 0; i < getNumberSessions(); i++ ) {
                add( new SessionRecord( getSessionID( i ), getSessionStartTime( i ) ) );
            }
            Collections.sort( this, new Comparator<SessionRecord>() {
                public int compare( SessionRecord o1, SessionRecord o2 ) {
                    return Double.compare( o1.getTime(), o2.getTime() );
                }
            } );
        }} );
    }

    private long getSessionStartTime( int i ) {
        //TODO: Fix
        return System.currentTimeMillis();
    }

    private SessionID getSessionID( int i ) {
        return (SessionID) read( new File( root, "session-" + i + "/sessionid.ser" ) );
    }

    public StudentList getActiveStudentList() {
        return new StudentList( new ArrayList<StudentSummary>() {{
            SessionList allSessions = listAllSessions();
            for ( Object o : allSessions.toArray() ) {
                SessionRecord sessionRecord = (SessionRecord) o;
                final int index = getNumberSamples( sessionRecord.getSessionID() ) - 1;
                if ( index > 0 ) {
                    SimState sample = getSample( sessionRecord.getSessionID(), index );
                    add( new StudentSummary( sessionRecord.getSessionID(), sample.getImage(), 1000, 1000, 1000 ) );
                }
            }
        }} );
    }

    public void startSession( SessionID sessionID ) {
        File dir = getSessionDir( sessionID );
        dir.mkdirs();
        write( new File( dir, "sessionid.ser" ), sessionID );
    }

    public void endSession( SessionID sessionID ) {
    }

    public SampleBatch getSamplesAfter( SessionID id, final int index ) {
        final File[] list = getSessionDir( id ).listFiles( new FilenameFilter() {
            public boolean accept( File dir, String name ) {

                final boolean b = name.startsWith( "sample_" + index + ".ser" );
                if ( b ) {
                    final StringTokenizer stringTokenizer = new StringTokenizer( name, "_." );
                    stringTokenizer.nextToken();
                    int i = Integer.parseInt( stringTokenizer.nextToken() );
                    return i > index;
                }
                return false;
            }
        } );
        return new SampleBatch<SimState>( new ArrayList<SimState>() {{
            for ( File file : list ) {
                add( (SimState) read( file ) );
            }
        }}, getNumberSamples( id ) );
    }

    public void storeAll( SessionID sessionID, AddSamples data ) {
        File dir = getSessionDir( sessionID );
        for ( Object o : data.data ) {
            SimState s = (SimState) o;
            write( new File( dir, "sample_" + s.getIndex() + ".ser" ), s );
        }
    }

    public void clear() {
    }

    public static void write( File file, Object obj ) {
        try {
            FileOutputStream bos = new FileOutputStream( file );
            ObjectOutputStream oos = new ObjectOutputStream( bos );
            oos.writeObject( obj );
            oos.flush();
            oos.close();
            bos.close();
        }
        catch ( IOException ex ) {
            //TODO: Handle the exception
        }
    }

    public static Object read( File file ) {
        Object obj = null;
        try {
            FileInputStream bis = new FileInputStream( file );
            ObjectInputStream ois = new ObjectInputStream( bis );
            obj = ois.readObject();
        }
        catch ( IOException ex ) {
            //TODO: Handle the exception
        }
        catch ( ClassNotFoundException ex ) {
            //TODO: Handle the exception
        }
        return obj;
    }
}
