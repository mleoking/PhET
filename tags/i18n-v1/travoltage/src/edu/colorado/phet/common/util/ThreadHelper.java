package edu.colorado.phet.common.util;

public class ThreadHelper {
    public static void quietNap( int restTime ) {
        try {
            Thread.sleep( restTime );
        }
        catch( InterruptedException e ) {
            System.out.println( "Napping disturbed" );
        }
    }

    public static void nap( int restTime ) {
        System.out.println( "Wait... " + restTime + "." );
        try {
            Thread.sleep( restTime );
        }
        catch( InterruptedException e ) {
            System.out.println( "Napping disturbed" );
        }
    }
}
