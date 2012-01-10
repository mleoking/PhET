// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.simeventdatacollectionmonitor;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

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
    final Object[] columnNames = { "Machine ID", "Session ID", "User ID", "last event received", "event count" };
    private final JTable table = new JTable() {{
        setAutoCreateRowSorter( true );
        setModel( new DefaultTableModel() {{
            setColumnIdentifiers( columnNames );
        }} );
    }};
    private final JFrame frame = new JFrame( "MongoDB Monitor" ) {{
        setDefaultCloseOperation( EXIT_ON_CLOSE );
    }};

    public QueryMongoDB() throws UnknownHostException {
        m = new Mongo();

        frame.setContentPane( new JPanel( new BorderLayout() ) {{
            add( new JScrollPane( table ), BorderLayout.CENTER );
        }} );
        frame.pack();
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
        frame.setVisible( true );

        //Run timer in swing thread for ease of integration with swing components.
        new Timer( 1000, new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                process();
            }
        } ).start();
    }

    //Columns in table:
    //Machine ID | Session ID | user ID | last event received | numberEvents
    private void process() {
        ArrayList<Object[]> rows = new ArrayList<Object[]>();
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
                        Object[] row = new Object[] { machineID, session, "?", "?", collection.getCount() };
                        rows.add( row );
                    }
                }
            }
        }
        Object[][] data = new Object[rows.size()][columnNames.length];
        for ( int i = 0; i < data.length; i++ ) {
            data[i] = rows.get( i );
        }
        table.setModel( new DefaultTableModel( data, columnNames ) );
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
//        Entry e = EntryJavaUtil.EntryJavaUtil( Long.parseLong( time ), messageType, object, action, null );
//        System.out.println( e );
    }
}