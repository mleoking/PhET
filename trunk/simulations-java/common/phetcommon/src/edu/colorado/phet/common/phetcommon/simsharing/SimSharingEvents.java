// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JComponent;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.PhetPersistenceDir;
import edu.colorado.phet.common.phetcommon.util.ObservableList;
import edu.colorado.phet.common.phetcommon.util.Option;
import edu.colorado.phet.common.phetcommon.util.function.Function0;

import static edu.colorado.phet.common.phetcommon.simsharing.Parameter.param;

/**
 * Central access point for action-oriented message delivery for user usage studies.
 *
 * @author Sam Reid
 */
public class SimSharingEvents {

    //Generate a strong unique id, see http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string-in-java
    private static final String SESSION_ID = generateIDString();

    //Identify the machine cookie (or create it if it did not already exist)
    //Only do this if permissions are enabled
    private static String MACHINE_COOKIE;

    //Flag to indicate whether the columns have been printed to the data source
    private static boolean printedColumns;

    //Actor for sending messages to the server
    private static ThreadedActor client;

    //Flag indicating whether messages should be sent to the server
    private static boolean connect = false;
    private static Option<Long> simStartedTime = new Option.None<Long>();
    private static Collection<String> queue = Collections.synchronizedCollection( new ArrayList<String>() );

    //Key for storage in the .phet/sim-sharing.properties file
    //Call it "cookie" instead of "id" so it doesn't sound so scary to end users
    private static final String MACHINE_COOKIE_KEY = "machine.cookie";

    public static final String OBJECT_SYSTEM = "system";
    public static final String ACTION_EXITED = "exited";

    //Determine whether the sim should try to send event messages to the server
    public static boolean shouldConnect() {
        return connect;
    }

    //A direct response to something the user did.
    public static void systemResponse( String action, Parameter... parameters ) {
        actionPerformed( OBJECT_SYSTEM, action, parameters );
    }

    //Signify that an action performed by the user has occurred by writing it to the appropriate sources, but only if the sim is running in "study mode" and is hence supposed to connect to the server
    public static void actionPerformed( String object, String action, Parameter... parameters ) {

        if ( connect ) {

            //Print the columns in the same format as the server to simplify processing
            if ( !printedColumns ) {
                //System.out.println( getColumnHeaders() );

                System.out.println( "machineID = " + MACHINE_COOKIE );
                System.out.println( "sessionID = " + SESSION_ID );
                System.out.println( "serverTime = " + System.currentTimeMillis() );

                printedColumns = true;
            }

            //Send the message to the destination
            String timestamp = simStartedTime.isSome() ? ( System.currentTimeMillis() - simStartedTime.get() ) + "" : "@" + System.currentTimeMillis();

            //Deliver the machine id + session id + message, but only print the message since that is all the server will log
            String parameterText = new ObservableList<Parameter>( parameters ).mkString( ", " );
            String message = timestamp + "\t" + object + "\t" + action + "\t" + parameterText;
            System.out.println( message );
            deliverMessage( MACHINE_COOKIE + "\t" + SESSION_ID + "\t" + timestamp + "\t" + object + "\t" + action + "\t" + parameterText );
        }
    }

    //TODO get rid of this? Excel treats the first row of tab-delimited data as column headers, so this would be nice if processing data in Excel.
    public static String getColumnHeaders() {
        return "Machine ID" + "\t" + "Session ID" + "\t" + "Event time (ms)" + "\t" + "Object" + "\t" + "Action" + "\t" + "Parameter List";
    }

    //Write the message to the console and to the server
    private static void deliverMessage( String s ) {

        boolean shouldDeliver = connect;
        boolean canDeliver = client != null;

        if ( shouldDeliver && canDeliver ) {
            tellWithErrorHandling( s );
        }

        else if ( shouldDeliver && !canDeliver ) {
            queue.add( s );
        }
    }

    private static void tellWithErrorHandling( String s ) {
        try {
            client.tell( s );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }
        catch ( Throwable t ) {
            t.printStackTrace();
        }
    }

    //Called from the first line of main(), connects to the server and sends a start message
    public static void simStarted( final PhetApplicationConfig config ) {
        simStartedTime = new Option.Some<Long>( System.currentTimeMillis() );
        connect = config.hasCommandLineArg( "-study" );
        if ( connect ) {

            //Create or load the machine id
            try {
                MACHINE_COOKIE = getMachineID();
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }

            new Thread( new Runnable() {
                public void run() {
                    //Create the actor, but fail gracefully if cannot connect
                    try {
                        client = new ThreadedActor( new DefaultActor() );
                    }
                    catch ( ClassNotFoundException e ) {
                        e.printStackTrace();
                    }
                    catch ( IOException e ) {
                        e.printStackTrace();
                    }
                    catch ( Throwable t ) {
                        t.printStackTrace();
                    }

                    systemResponse( "started",
                                    param( "time", simStartedTime.get() ),
                                    param( "name", config.getName() ),
                                    param( "version", config.getVersion().formatForAboutDialog() ),
                                    param( "project", config.getProjectName() ),
                                    param( "flavor", config.getFlavor() ),
                                    param( "locale", config.getLocale().toString() ),
                                    param( "distribution tag", config.getDistributionTag() ),

                                    //Can't have commas in args because of the parser, but can look up the study argument
                                    param( "study", getArgAfter( config.getCommandLineArgs(), "-study" ) ) );
                    systemResponse( "connected to server" );

                    //Report on any messages that were collected while we were trying to connect to the server
                    if ( client != null ) {
                        deliverQueue();
                    }
                    else {
                        System.out.println( "Weren't able to connect to the server even though we really wanted to." );
                    }
                }
            } ).start();
        }
    }

    //Find the argument after the specified key, for purposes of finding a command line argument like "-study colorado"
    private static String getArgAfter( String[] commandLineArgs, String key ) {
        int s = Arrays.asList( commandLineArgs ).indexOf( key );
        if ( s >= 0 && s <= commandLineArgs.length - 2 ) {
            return commandLineArgs[s + 1];
        }
        else {
            return "?";
        }
    }

    //Identify the machine ID (or create it if it did not already exist)
    private static String getMachineID() throws IOException {
        PhetPersistenceDir phetPersistenceDir = new PhetPersistenceDir();
        final File simsharingFile = new File( phetPersistenceDir, "sim-sharing.properties" );
        if ( simsharingFile.exists() ) {
            Properties p = new Properties() {{
                load( new FileInputStream( simsharingFile ) );
            }};
            return p.getProperty( MACHINE_COOKIE_KEY );
        }
        else {

            //Attempt to make the properties come out in a reliable order to make diffing usable if/when more properties are added (just in case)
            //see http://stackoverflow.com/questions/54295/how-to-write-java-util-properties-to-xml-with-sorted-keys
            Properties p = new Properties() {
                @Override
                public Set<Object> keySet() {
                    return Collections.unmodifiableSet( new TreeSet<Object>( super.keySet() ) );
                }

                @Override
                public synchronized Enumeration<Object> keys() {
                    return Collections.enumeration( new TreeSet<Object>( super.keySet() ) );
                }
            };
            final String ID = generateIDString();
            p.put( MACHINE_COOKIE_KEY, ID );
            p.store( new FileOutputStream( simsharingFile ), "Automatically generated by " + SimSharingEvents.class.getName() );
            return ID;
        }
    }

    private static synchronized void deliverQueue() {
        for ( String s : queue ) {
            tellWithErrorHandling( s );
        }
        queue.clear();
    }

    //Attach to components that users can drag, so we see when the drag starts and ends.
    public static void addDragSequenceListener( JComponent component, final Function0<Parameter[]> message ) {
        component.addMouseListener( new MouseAdapter() {
            @Override public void mousePressed( MouseEvent e ) {
                actionPerformed( "mouse", "startDrag", message.apply() );
            }

            @Override public void mouseReleased( MouseEvent e ) {
                actionPerformed( "mouse", "endDrag", message.apply() );
            }
        } );
    }

    //Generate a strong unique id, see http://stackoverflow.com/questions/41107/how-to-generate-a-random-alpha-numeric-string-in-java
    private static String generateIDString() {
        return new BigInteger( 130, new SecureRandom() ).toString( 32 );
    }
}