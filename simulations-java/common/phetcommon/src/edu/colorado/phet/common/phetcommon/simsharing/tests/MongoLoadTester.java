// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.tests;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingMessage;
import edu.colorado.phet.common.phetcommon.simsharing.logs.MongoLog;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IParameterKey;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserAction;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponent;
import edu.colorado.phet.common.phetcommon.simsharing.messages.IUserComponentType;
import edu.colorado.phet.common.phetcommon.simsharing.messages.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.messages.ParameterSet;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentChain;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

/**
 * Runs lots of threads which all send some data to the mongo server--helps us
 * to see what kind of load MongoDB can support on our hardware/network
 * configuration.
 *
 * @author Sam Reid
 * @author John Blanco
 */
public class MongoLoadTester {

    public static final String LOAD_TESTING_DB_NAME = "loadtesting";
    public static final String DB_USER_NAME = "phetsimclient";
    public static final String DB_COLLECTION = "loadtesterCollection";
    public static final String [] SIM_SHARING_ARGS = new String[]{ "-study", "load-testing"};

    private static final int AVERAGE_TIME_BETWEEN_MESSAGES_MILLIS = 1000;
    private static final int MAX_MESSAGE_TIME_VARIATION = 500;
    private static final int NUM_CLIENTS = 10;

    //Part of the mongoDB password, see #3231
    public static final String MER = "meR".toLowerCase();

    private static final Random RAND = new Random();

    public static void main( String[] args ) {

        // Argument processing.
        double runTime = 10; // in seconds.
        if ( args.length > 1 ) {
            printUsage();
            return;
        }
        else if ( args.length == 1 ) {
            runTime = Double.parseDouble( args[0] );
        }

        System.out.println( "Load test starting up, run time = " + runTime );

        // Initialize the sim sharing manager.
        SimSharingManager.init( new PhetApplicationConfig( SIM_SHARING_ARGS, "test-java-project" ), LOAD_TESTING_DB_NAME );
        System.out.println( "SimSharingManager.getInstance().getSessionId() = " + SimSharingManager.getInstance().getSessionId() );

        // Establish a separate connection to the database.
        long initialMessageCount;
        Mongo mongo;
        DB db;
        DBCollection collection;
        try {
            mongo = new Mongo( MongoLog.HOST_IP_ADDRESS, MongoLog.PORT );
            db = mongo.getDB( LOAD_TESTING_DB_NAME );
            db.authenticate( DB_USER_NAME, ( MER + SimSharingManager.MONGO_PASSWORD + "" + ( 2 * 2 * 2 ) + "ss0O88723otbubaoue" ).toCharArray() );
            collection = db.getCollection( SimSharingManager.getInstance().getSessionId() );
            initialMessageCount = collection.getCount();
        }
        catch ( UnknownHostException e ) {
            System.out.println( "Exception thrown when trying to connect to database, " + e.getMessage() );
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            return;
        }

        // Create the thread pool where the threads will be run.
        ExecutorService threadPool = Executors.newFixedThreadPool( NUM_CLIENTS );

        // Create a bunch of threads, each of which send data to the DB.
        List<MessageSendingThread> messageSendingThreads = new ArrayList<MessageSendingThread>();
        for ( int i = 0; i < NUM_CLIENTS; i++ ) {
            messageSendingThreads.add( new MessageSendingThread( runTime, i ) );
        }

        // Launch the threads.
        for ( Runnable messageSendingThread : messageSendingThreads ) {
            threadPool.submit( messageSendingThread );
        }

        // Wait until all threads are complete, then analyze the data in the
        // DB and make sure that it looks correct.
        try {
            threadPool.shutdown();
            threadPool.awaitTermination( (long) ( runTime * 10 ), TimeUnit.SECONDS );
            Thread.sleep( 100 ); // If we don't wait a little bit, the last message if often not get counted.
            System.out.println( "All threads terminated." );
            long finalMessageCount = collection.getCount();
            // Total up the sent messages.
            int totalMessagesSent = 0;
            for ( MessageSendingThread thread : messageSendingThreads ) {
                totalMessagesSent += thread.messagesSent;
            }
            System.out.println( "initialMessageCount = " + initialMessageCount );
            System.out.println( "finalMessageCount = " + finalMessageCount );
            System.out.println( "totalMessagesSent = " + totalMessagesSent );

//            SimSharingManager.getInstance().
            // Compare the expected and actual message count to decide whether
            // the test was successful.
            System.out.println( "Test passed = " + ( initialMessageCount + totalMessagesSent == finalMessageCount ) );
        }
        catch ( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    private static void printUsage() {
        System.out.println( "Usage: MongoLoadTester <run time in seconds>" );
    }

    // Thread that sends messages to the database.
    private static class MessageSendingThread implements Runnable {

        private final double runTime;
        private final int clientIndex;

        public int messagesSent = 0;

        private MessageSendingThread( double runTime, int clientIndex ) {
            this.runTime = runTime;
            this.clientIndex = clientIndex;
        }

        public void run() {
            int messageIndex = 0;
            long startTime = System.currentTimeMillis();
            while ( System.currentTimeMillis() < startTime + runTime * 1000 ) {
                try {
                    long sleepTime = (long) ( AVERAGE_TIME_BETWEEN_MESSAGES_MILLIS + ( RAND.nextDouble() - 0.5 ) * 2 * MAX_MESSAGE_TIME_VARIATION );
                    Thread.sleep( sleepTime );
                    SimSharingManager.sendUserMessage( UserComponentChain.chain( UserComponents.loadTester, clientIndex),
                                                       UserComponentTypes.testApp,
                                                       UserActions.autoGeneratedAction,
                                                       new ParameterSet( new Parameter( ParameterKeys.messageNumber, messageIndex ) ) );
                    messageIndex++;
                    messagesSent++;
                }
                catch ( InterruptedException e ) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static enum UserComponents implements IUserComponent {
        loadTester
    }

    private static enum UserComponentTypes implements IUserComponentType {
        testApp
    }

    private static enum UserActions implements IUserAction {
        autoGeneratedAction
    }

    private static enum ParameterKeys implements IParameterKey {
        messageNumber
    }
}