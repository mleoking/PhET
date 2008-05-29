package edu.colorado.phet.flashlauncher;

import edu.colorado.phet.flashlauncher.util.BareBonesBrowserLaunch;

/**
 * Created by: Sam
 * May 29, 2008 at 7:53:28 AM
 */
public class FlashLauncher {
    private String[] args;

    public FlashLauncher( String[] args ) {
        this.args = args;
    }

    public static void main( String[] args ) {
        new FlashLauncher( args ).start();
    }

    private void start() {
        System.out.println( "FlashLauncher.start" );
        BareBonesBrowserLaunch.openURL( "http://www.google.com" );
    }
}
