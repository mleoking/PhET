// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;

/**
 * @author Sam Reid
 */
public class SimSharingMongoClient {

    //Flag for debugging, if this is set to false, then it won't send messages to the server, but will still print them to the console
    private static final boolean ALLOW_SERVER_CONNECTION = true;

    private Mongo mongo;

    //Strings for MongoDB
    public static final String TIME = "time";
    public static final String MESSAGE_TYPE = "messageType";
    public static final String COMPONENT = "component";
    public static final String ACTION = "action";
    public static final String PARAMETERS = "parameters";

    //Default to phet-server, but you can use this command line arg for local testing:
    //-Dsim-event-data-collection-server-host-ip-address=localhost
    public static String HOST_IP_ADDRESS = System.getProperty( "sim-event-data-collection-server-host-ip-address", "128.138.145.107" );

    //On phet-server, port must be in a specific range of allowed ports, see Unfuddle ticket
    public static int PORT = Integer.parseInt( System.getProperty( "sim-event-data-collection-server-host-port", "44101" ) );

    //http://www.cs.umd.edu/class/spring2006/cmsc433/lectures/util-concurrent.pdf
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    public SimSharingMongoClient() {
        try {
            mongo = new Mongo( HOST_IP_ADDRESS, PORT );
        }
        catch ( UnknownHostException e ) {
            e.printStackTrace();
        }
    }

    // Sends a message to the server, and prefixes the message with a couple of additional fields.
    public void sendToServer( final AugmentedMessage message, final String machineCookie, final String sessionId ) {

        executor.execute( new Runnable() {
            public void run() {

                //one database per machine
                DB database = mongo.getDB( machineCookie );

                //TODO: Authentication
                //                database.authenticate();

                //One collection per session, lets us easily iterate and add messages per session.
                DBCollection coll = database.getCollection( sessionId );

                BasicDBObject doc = new BasicDBObject() {{
                    put( TIME, message.time + "" );
                    put( MESSAGE_TYPE, message.message.messageType.toString() );
                    put( COMPONENT, message.message.component.toString() );
                    put( ACTION, message.message.action.toString() );
                    put( PARAMETERS, new BasicDBObject() {{
                        for ( Parameter parameter : message.message.parameters ) {
                            //                            if ( parameter.name == null ) {
                            //                                System.out.println( "parameter = " + parameter );
                            //                            }
                            //                            else if ( parameter.value == null ) {
                            //                                System.out.println( "parameter.value = " + parameter.value );
                            //                            }
                            put( parameter.name.toString(), parameter.value == null ? "null" : parameter.value.toString() );
                        }
                    }} );
                }};

                try {
                    WriteResult result = coll.insert( doc );
                }

                //like new MongoException.Network( "can't say something" , ioe )
                catch ( RuntimeException e ) {
                    //                    System.out.println( "My error: " + e.getMessage() );
                    //
                    //                    Enumeration<String> s = LogManager.getLogManager().getLoggerNames();
                    //                    while ( s.hasMoreElements() ) {
                    //                        String s1 = s.nextElement();
                    //                        System.out.println( "s1 = " + s1 );
                    //                    }
                }
            }
        } );
    }
}