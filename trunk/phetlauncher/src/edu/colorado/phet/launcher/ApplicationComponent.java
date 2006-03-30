/* Copyright 2004, Sam Reid */
package edu.colorado.phet.launcher;

import netx.jnlp.*;
import netx.jnlp.cache.CacheEntry;
import netx.jnlp.cache.ResourceTracker;
import netx.jnlp.cache.UpdatePolicy;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: Sam Reid
 * Date: Mar 29, 2006
 * Time: 9:46:46 PM
 * Copyright (c) Mar 29, 2006 by Sam Reid
 */

public class ApplicationComponent extends JPanel {
    private URL location;
    private JLabel label;
    private JLabel currentLabel;
    private JButton updateButton;

    public ApplicationComponent( final URL webstartURL ) throws IOException {
        this.location = webstartURL;
        JButton button = new JButton( "Launch" );

        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final Launcher launcher = new Launcher();
                Thread t = new Thread( new Runnable() {
                    public void run() {
                        try {
                            launcher.setUpdatePolicy( UpdatePolicy.NEVER );
                            launcher.launch( webstartURL );
                        }
                        catch( LaunchException e1 ) {
                            e1.printStackTrace();
                        }
                    }
                } );
                t.start();
            }
        } );
        add( button );

        label = new JLabel();
        add( label );
        currentLabel = new JLabel( "Current" );
        add( currentLabel );
        updateButton = new JButton( "Update" );
        add( updateButton );
        refresh();
        updateButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    JNLPFile jnlpFile = new JNLPFile( location );
                    ResourcesDesc res = jnlpFile.getResources();
                    JARDesc jar = res.getMainJAR();
                    System.out.println( "jar.getLocation() = " + jar.getLocation() );
                    try {
                        ResourceTracker resourceTracker = new ResourceTracker();
                        resourceTracker.addResource( jar.getLocation(), null, UpdatePolicy.ALWAYS );
                        resourceTracker.startResource( jar.getLocation() );
                        resourceTracker.waitForResource( jar.getLocation(), 1000 );
                    }
                    catch( InterruptedException e1 ) {
                        e1.printStackTrace();
                    }
                    CacheEntry cacheEntry = new CacheEntry( jar.getLocation(), null );
                    System.out.println( "cacheEntry.isCached() = " + cacheEntry.isCached() );
                    System.out.println( "cacheEntry.isCurrent( null) = " + cacheEntry.isCurrent( null ) );

                    refresh();
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
                catch( ParseException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
    }

    public void refresh() throws IOException {
        long remoteTimeStamp = getRemoteTimeStamp( null, location, null );
        boolean cached = new CacheEntry( location, null ).isCached();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy.MM.dd 'at' hh:mm:ss a z" );
        String remoteString = simpleDateFormat.format( new Date( remoteTimeStamp ) );
        if( cached ) {
            long localTimeStamp = getLocalTimeStamp( null, location, null );
            String localString = simpleDateFormat.format( new Date( localTimeStamp ) );

            label.setText( "<html>local=<br>" + localString + "<br>remote=<br>" + remoteString + "</html>" );
            if( remoteTimeStamp == -1 ) {
                label.setText( "Cannot connect to resource." );
            }
            if( !localString.equals( remoteString ) ) {
                label.setForeground( Color.red );
                currentLabel.setText( "Not current" );
                updateButton.setVisible( true );
            }
            else {
                currentLabel.setText( "Current" );
                label.setForeground( Color.blue );
                updateButton.setVisible( false );
            }
        }
        else {
            currentLabel.setText( "No Cached Version" );
            label.setText( "Remote=" + remoteString );
            updateButton.setVisible( true );
        }
    }

    public static long getLocalTimeStamp( URLConnection connection, URL source, Version version ) {
        try {
            if( connection == null ) {
                connection = source.openConnection();
            }
            connection.connect();
        }
        catch( IOException e ) {
            return -1;
        }
        CacheEntry entry = new CacheEntry( source, version ); // could pool this
        return entry.getLastModified();
    }

    public static long getRemoteTimeStamp( URLConnection connection, URL source, Version version ) {
        try {
            if( connection == null ) {
                connection = source.openConnection();
            }
            connection.connect();
        }
        catch( IOException e ) {
            return -1;
        }
        return connection.getLastModified();
    }
}
