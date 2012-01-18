// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.logs;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.LogManager;

import edu.colorado.phet.common.phetcommon.simsharing.Log;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingMessage;
import edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;
import com.mongodb.WriteResult;

/**
 * The destination for Mongo logging messages for sim sharing, connects directly to MongoDB server, see #3213.
 * Note that currently there is one database per machine, and one top-level collection for each session.
 * This may not be optimal for searches that span multiple machines (or operations that must operate on all stored logs), so may be changed in the future.
 *
 * @author Sam Reid
 */
public class MongoLog implements Log {

    //Flag for debugging, if this is set to false, then it won't send messages to the server, but will still print them to the console
    private static final boolean ALLOW_SERVER_CONNECTION = true;

    private Mongo mongo;

    //Strings for MongoDB
    public static final String TIME = "time";
    public static final String MESSAGE_TYPE = "messageType";
    public static final String COMPONENT = "component";
    public static final String COMPONENT_TYPE = "componentType";
    public static final String ACTION = "action";
    public static final String PARAMETERS = "parameters";

    //Default to phet-server, but you can use this command line arg for local testing:
    //-Dsim-event-data-collection-server-host-ip-address=localhost
    public static String HOST_IP_ADDRESS = System.getProperty( "sim-event-data-collection-server-host-ip-address", "128.138.145.107" );

    //On phet-server, port must be in a specific range of allowed ports, see Unfuddle ticket
    public static int PORT = Integer.parseInt( System.getProperty( "sim-event-data-collection-server-host-port", "44100" ) );

    //http://www.cs.umd.edu/class/spring2006/cmsc433/lectures/util-concurrent.pdf
    private ExecutorService executor = Executors.newSingleThreadExecutor();
    private final String machineID;
    private final String sessionID;
    private final DBCollection collection;

    public MongoLog( String machineID, String sessionID ) {
        this.machineID = machineID;
        this.sessionID = sessionID;
        try {
            mongo = new Mongo( HOST_IP_ADDRESS, PORT );
        }
        catch ( UnknownHostException e ) {
            e.printStackTrace();
        }

        //Disables mongo logging (and maybe all other logging as well!)
        //TODO: how to disable just the mongo log from DBPort info messages?  They are sent repeatedly when server offline.
        try {
            LogManager.getLogManager().reset();
        }
        catch ( Exception e ) {
            System.out.println( "error on log reset: " + e.getMessage() );
        }

        //one database per machine
        DB database = mongo.getDB( machineID );

        //TODO: Authentication
        //                database.authenticate();

        //One collection per session, lets us easily iterate and add messages per session.
        collection = database.getCollection( sessionID );
    }

    // Sends a message to the server, and prefixes the message with a couple of additional fields.
    public void addMessage( final SimSharingMessage message ) throws IOException {

        executor.execute( new Runnable() {
            public void run() {

                BasicDBObject doc = new BasicDBObject() {{
                    put( TIME, message.time + "" );
                    put( MESSAGE_TYPE, message.messageType.toString() );
                    put( COMPONENT, message.component.toString() );
                    put( COMPONENT_TYPE, message.componentType.toString() );
                    put( ACTION, message.action.toString() );
                    put( PARAMETERS, new BasicDBObject() {{
                        for ( Parameter parameter : message.parameters ) {
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
                    WriteResult result = collection.insert( doc );
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

    public String getName() {
        return "MongoDB Server @ " + mongo.getAddress();
    }
}