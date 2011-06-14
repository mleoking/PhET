package edu.colorado.phet.unfuddletool.util;

import java.util.Date;

public class UpdateThread extends Thread {
    public void run() {
        try {
            while ( true ) {
                // every minute
                Thread.sleep( 1000 * 60 );

                System.out.println( "Requesting latest activity from the server at " + ( new Date() ) );

                try {
                    new ActivityUpdaterThread().start();
                }
                catch( Exception e ) {
                    e.printStackTrace();
                    System.out.println( "Attempting to recover" );
                }
            }
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
    }

    private static class ActivityUpdaterThread extends Thread {
        @Override
        public void run() {
            Activity.requestRecentActivity( 4 );
        }
    }
}
