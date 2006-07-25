/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.simlauncher.model;

import edu.colorado.phet.common.util.EventChannel;
import edu.colorado.phet.simlauncher.Configuration;
import edu.colorado.phet.simlauncher.DebugFlags;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.EventListener;
import java.util.EventObject;

/**
 * PhetSiteConnection
 * <p/>
 * An abstraction of the connection to the Phet Web Site.
 * <p/>
 * Has a Thread that occaisionally checks to see if an actual connection to the
 * PhET site is available, and fires change events to its change listeners when the
 * state of that connection changes.
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PhetSiteConnection {

    private static PhetSiteConnection instance;

    public static PhetSiteConnection instance() {
        if( instance == null ) {
            instance = new PhetSiteConnection();
        }
        return instance;
    }

    private boolean isAvailable;
    private URL url;

    private PhetSiteConnection() {
        url = Configuration.instance().getPhetUrl();
        updateAvailability();
        ConnectionMonitor connectionMonitor = new ConnectionMonitor();
        connectionMonitor.start();
    }

    /**
     * Tries to connect to the PhET site with a URLConnection. When there is a connection,
     * the check completes very quickly. If there is no connection, the first check takes
     * a few seconds, and each subsequent check is very quick.
     *
     * @return true if there is a connection to the PhET web site
     */
    public boolean isConnected() {
        return this.isAvailable;
    }

    /**
     * Reports if there is a connection to the Phet web site.
     * @return true if there is a connection, false if not
     */
    private boolean checkConnection() {
        // Debugging
        if( DebugFlags.NO_PHET_SITE_CONNECTION_AVAILABLE ) {
            return false;
        }

        boolean connected;
        try {
            // Try to open a connection.
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();

            // Try to read a byte from the url. It seems that simply tying to connect a URLConnetcion doesn't
            // always throw an exception if the URL points to a non-existing directory or file
            DataInputStream dis;
            dis = new DataInputStream( url.openStream() );
            dis.read();
            dis.close();

            // If the two things above succeeded, we're connected
            connected = true;
        }
        catch( IOException e ) {
            connected = false;
        }
        return connected;
    }

    /**
     * Tries up to nTries times to get a connection
     */
    private synchronized void updateAvailability() {
        int nTries = 5;
        boolean connected = false;
        for( int i = 0; !connected && i < nTries; i++ ) {
            connected = checkConnection();
        }
        if( connected != isAvailable ) {
            isAvailable = connected;
            changeListenerProxy.connectionChanged( new ChangeEvent( this ) );
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Thread for periodically checking for an actual connection
    //--------------------------------------------------------------------------------------------------

    private class ConnectionMonitor extends Thread {
        private boolean stop = false;

        public void run() {

            while( !stop ) {
                updateAvailability();
                try {
                    Thread.sleep( 5000 );
                }
                catch( InterruptedException e ) {
                    setStop();
                }

            }
        }

        synchronized void setStop() {
            stop = true;
        }
    }

    //--------------------------------------------------------------------------------------------------
    // Events and listeners
    //--------------------------------------------------------------------------------------------------

    EventChannel changeEventChannel = new EventChannel( ChangeListener.class );
    ChangeListener changeListenerProxy = (ChangeListener)changeEventChannel.getListenerProxy();

    public void addChangeListener( ChangeListener listener ) {
        changeEventChannel.addListener( listener );
    }

    public void removeChangeListener( ChangeListener listener ) {
        changeEventChannel.removeListener( listener );
    }

    public class ChangeEvent extends EventObject {
        public ChangeEvent( Object source ) {
            super( source );
        }

        public PhetSiteConnection getPhetSiteConnection() {
            return (PhetSiteConnection)getSource();
        }
    }

    public interface ChangeListener extends EventListener {
        void connectionChanged( ChangeEvent event );
    }

}
