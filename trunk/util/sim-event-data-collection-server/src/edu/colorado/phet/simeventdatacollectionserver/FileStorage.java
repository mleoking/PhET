// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventdatacollectionserver;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Sam Reid
 */
public class FileStorage {
    private final Map<String, BufferedWriter> map = Collections.synchronizedMap( new HashMap<String, BufferedWriter>() );

    public void store( String m, String machineID, String sessionID ) throws IOException {
        BufferedWriter bufferedWriter = getBufferedWriter( machineID, sessionID );
        bufferedWriter.write( stripMachineAndUserID( m, machineID, sessionID ) );
        bufferedWriter.newLine();
        bufferedWriter.flush();
    }

    private String stripMachineAndUserID( String m, String machineID, String sessionID ) {
        String prefix = machineID + "\t" + sessionID + "\t";
        if ( m.startsWith( prefix ) ) {
            return m.substring( prefix.length() );
        }
        else {
            System.out.println( "Bogus prefix for message, returning full string: " + m );
            return m;
        }
    }

    private BufferedWriter getBufferedWriter( String machineID, String sessionID ) throws IOException {
        String key = getKey( machineID, sessionID );

        if ( map.containsKey( key ) ) {
            return map.get( key );
        }
        else {
            File file = new File( "/home/phet/eventserver/data/" + key + ".txt" );
            file.getParentFile().mkdirs();
            BufferedWriter bufferedWriter = new BufferedWriter( new FileWriter( file, true ) );
            bufferedWriter.write( "machineID = " + machineID + "\n" + "sessionID = " + sessionID + "\n" + "serverTime = " + System.currentTimeMillis() );
            bufferedWriter.newLine();
            bufferedWriter.flush();
            map.put( key, bufferedWriter );
            return bufferedWriter;
        }
    }

    private String getKey( String machineID, String sessionID ) {
        return machineID + "_" + sessionID;
    }

    public void close( String machineID, String sessionID ) throws IOException {

        getBufferedWriter( machineID, sessionID ).close();

        String key = getKey( machineID, sessionID );
        map.remove( key );

    }
}
