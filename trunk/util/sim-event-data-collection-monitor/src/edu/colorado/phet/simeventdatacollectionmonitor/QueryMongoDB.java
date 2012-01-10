// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventdatacollectionmonitor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

import javax.swing.SwingUtilities;
import javax.swing.Timer;

import edu.colorado.phet.simsharinganalysis.Entry;
import edu.colorado.phet.simsharinganalysis.EntryJavaUtil;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

/**
 * Get data from an existing mongodb session.
 *
 * @author Sam Reid
 */
public class QueryMongoDB {
    Mongo m;

    public QueryMongoDB() throws UnknownHostException {
        m = new Mongo();
    }

    public static void main( String[] args ) throws UnknownHostException {
        SwingUtilities.invokeLater( new Runnable() {
            public void run() {
                try {
                    new QueryMongoDB().start();
                }
                catch ( UnknownHostException e ) {
                    e.printStackTrace();
                }
            }
        } );
    }

    private void start() throws UnknownHostException {
        //Run timer in swing thread for ease of integration with swing components.
        new Timer( 1000, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                process();
            }
        } ).start();
    }

    private void process() {
        List<String> names = m.getDatabaseNames();
        System.out.println( "names = " + names );
        for ( String machineID : names ) {
            if ( !machineID.equals( "admin" ) ) {
                DB database = m.getDB( machineID );
                Set<String> sessionIDs = database.getCollectionNames();
                for ( String session : sessionIDs ) {
                    if ( !session.equals( "system.indexes" ) ) {
                        System.out.println( session );
                        DBCollection collection = database.getCollection( session );
                        System.out.println( "Number messages in session: " + collection.getCount() );
                        DBCursor cur = collection.find();
                        while ( cur.hasNext() ) {
                            DBObject obj = cur.next();
//                            System.out.println( "obj = " + obj );
                            parse( obj );
                        }
                    }
                }
            }
        }
    }

    private void parse( DBObject obj ) {
        String machineID = obj.get( "machineID" ).toString();
        String sessionID = obj.get( "sessionID" ).toString();
        String time = obj.get( "time" ).toString();
        String messageType = obj.get( "messageType" ).toString();
        String object = obj.get( "object" ).toString();
        String action = obj.get( "action" ).toString();
        DBObject parameters = (DBObject) obj.get( "parameters" );
//        System.out.println( "parameters = " + parameters );
        Entry e = EntryJavaUtil.EntryJavaUtil( Long.parseLong( time ), messageType, object, action, null );
        System.out.println( e );
    }
}