package edu.colorado.phet.unfuddle.test;

/**
 * Created by: Sam
 * May 14, 2008 at 7:23:23 PM
 */
public class RunLongTime {
    public static void main( String[] args ) throws InterruptedException {
        long time = Long.parseLong( args[0] );
        System.out.println( "Started waiting: " + time + " ms" );
        Thread.sleep( time );
        System.out.println( "Finished waiting" );
    }
}
