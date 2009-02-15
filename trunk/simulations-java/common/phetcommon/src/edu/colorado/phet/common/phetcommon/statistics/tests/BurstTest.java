package edu.colorado.phet.common.phetcommon.statistics.tests;

import java.io.IOException;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.application.SessionCounter;
import edu.colorado.phet.common.phetcommon.statistics.SessionMessage;
import edu.colorado.phet.common.phetcommon.statistics.XMLStatisticsService;

/**
 * Sends many messages to the server to test its capabilitiy for handling many messages at once.
 */
public class BurstTest {
    public static void main( String[] args ) {
        SessionCounter.initInstance( "moving-man","moving-man" );
        SessionMessage.initInstance( new PhetApplicationConfig( args, "moving-man" ) );
//        int numThreads = 100;
//        int numMessagesPerThread = 100;

        int numThreads = 2;
        int numMessagesPerThread = 2;

        for ( int i = 0; i < numThreads; i++ ) {
            new BurstTestThread( i, numMessagesPerThread ).start();
        }
    }

    private static class BurstTestThread extends Thread {
        private int index;
        private int numMessagesPerThread;

        private BurstTestThread( int index, int numMessagesPerThread ) {
            this.index = index;
            this.numMessagesPerThread = numMessagesPerThread;
        }

        public void run() {
            super.run();
            System.out.println( "Started thread: " + index );
            for ( int i = 0; i < numMessagesPerThread; i++ ) {
                System.out.println( "Thread[" + index + "] sending message " + i + "..." );
                sendMessage();
            }
        }

        private void sendMessage() {
            SessionMessage sessionMessage = SessionMessage.getInstance();
            try {
                new XMLStatisticsService().postMessage( sessionMessage );
            }
            catch( IOException e ) {
                e.printStackTrace();
            }
        }
    }
}
