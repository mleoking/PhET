// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.simsharing.recording;

import java.io.*;
import java.util.ArrayList;

/**
 * @author Sam Reid
 */
public class SimHistory {
    private ArrayList<Object> states = new ArrayList<Object>();
    private ObjectOutputStream objectOutputStream;

    public SimHistory() {
        try {
            File dir = mkDirs( "simhistory/gravity-and-orbits" );
            objectOutputStream = new ObjectOutputStream( new FileOutputStream( new File( dir, "history-" + System.currentTimeMillis() + ".ser" ) ) );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    private File mkDirs( String filename ) {
        return new File( filename ) {{
            mkdirs();
            System.out.println( "Created directory: " + getAbsolutePath() );
        }};
    }

    public void store( Object obj ) {
        states.add( obj );
        try {
            objectOutputStream.writeObject( obj );
            objectOutputStream.flush();

            //intermittent snapshots in case main stream is truncated or fails
            //TODO: make these happen once every N time steps
//            ObjectOutputStream stream = new ObjectOutputStream( new FileOutputStream( new File( dir, "snapshot-" + System.currentTimeMillis() + ".ser" ) ) );
//            stream.writeObject( states );
//            stream.flush();
//            stream.close();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
            objectOutputStream.close();
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
    }

    public static ArrayList<Object> loadHistory( File file ) throws IOException, ClassNotFoundException {
        ArrayList<Object> list = new ArrayList<Object>();
        ObjectInputStream objectInputStream = new ObjectInputStream( new FileInputStream( file ) );
        while ( true ) {
            try {
                Object o = objectInputStream.readObject();
                if ( o != null ) {
                    System.out.println( "Read states: " + list.size() );
                    list.add( o );
                }
                else {
                    break;
                }
            }
            catch ( EOFException e ) {
                System.out.println( "EOF: " + e );
                break;
            }
        }
        return list;
    }
}
