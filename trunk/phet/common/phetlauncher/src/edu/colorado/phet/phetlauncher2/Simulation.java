/* Copyright 2004, Sam Reid */
package edu.colorado.phet.phetlauncher2;

import edu.colorado.phet.common.view.util.ImageLoader;
import netx.jnlp.ParseException;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Properties;

/**
 * User: Sam Reid
 * Date: Apr 2, 2006
 * Time: 8:55:15 PM
 * Copyright (c) Apr 2, 2006 by Sam Reid
 */

public abstract class Simulation {
    private PhetLauncher phetLauncher;
    private String title;
    private BufferedImage image;
    private URL abstractURL;
    private SimulationXMLEntry entry;
    private ArrayList listeners = new ArrayList();
    protected final String TIMESTAMP_PROPERTY = "timestamp";

    public Simulation( PhetLauncher phetLauncher, SimulationXMLEntry entry ) throws IOException, ParseException {
        this.entry = entry;
        URL thumbURL = new URL( entry.getThumb() );
        if( phetLauncher.hasLocalCopy( thumbURL ) ) {
            File localCopy = phetLauncher.getLocalFile( thumbURL );
            try {
                this.image = ImageLoader.loadBufferedImage( localCopy.toURL() );
            }
            catch( Exception e ) {
                this.image = new BufferedImage( 10, 10, BufferedImage.TYPE_INT_RGB );
            }
            this.title = getTitle( phetLauncher, entry );

            this.phetLauncher = phetLauncher;
            this.abstractURL = phetLauncher.getLocalFile( entry.getAbstractURL() ).toURL();
        }
    }

    protected abstract String getTitle( PhetLauncher phetLauncher, SimulationXMLEntry entry ) throws IOException, ParseException;

    public String getTitle() {
        return title;
    }

    public BufferedImage getImage() {
        return image;
    }

    public URL getAbstractURL() {
        return abstractURL;
    }

    //returns the newest time of any resources for the simulation.
    public abstract long getRemoteTimeStamp();

    //returns the oldest time of any resources for the simulation.
    public long getLocalTimeStamp() {
        File timestampFile = getTimestampFile();
        if( timestampFile.exists() ) {
            Properties properties = new Properties();
            try {
                properties.load( new FileInputStream( timestampFile ) );
            }
            catch( IOException e ) {
                e.printStackTrace();
                return 0;
            }
            return Long.parseLong( properties.getProperty( TIMESTAMP_PROPERTY ) );
        }
        else {
            return 0;
        }
    }

    public boolean isRemoteVersionAvailable() {
        return phetLauncher.isOnline();
    }

    public boolean localCopyExists() {
        return getTimestampFile().exists();
    }

    public abstract void downloadResources() throws IOException, ParseException;

    public void notifyDownloadComplete() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.downloadCompleted();
        }
    }

    public void checkStatus() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.checkStatus();
        }
    }

    public static interface Listener {
        public void downloadCompleted();

        void checkStatus();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    protected File getTimestampFile() {
        File localFile = getLocalSimulationFile();
        String abs = localFile.getAbsolutePath();
        return new File( abs + "-timestamp" );
    }

    protected void storeDownloadTimestamp() throws IOException {
        File resultFile = getTimestampFile();
        resultFile.getParentFile().mkdirs();
        resultFile.createNewFile();

        Properties properties = new Properties();
        properties.setProperty( TIMESTAMP_PROPERTY, "" + getRemoteTimeStamp() );
        properties.store( new FileOutputStream( resultFile ), "Automatically generated, do not modify." );
    }

    public abstract void launch() throws IOException;

    protected File getLocalSimulationFile() {
        return phetLauncher.getLocalFile( entry.getSimulationURL() );
    }

    public PhetLauncher getPhetLauncher() {
        return phetLauncher;
    }

    public SimulationXMLEntry getEntry() {
        return entry;
    }
}
