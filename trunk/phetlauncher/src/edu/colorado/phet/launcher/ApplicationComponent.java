/* Copyright 2004, Sam Reid */
package edu.colorado.phet.launcher;

import netx.jnlp.*;
import netx.jnlp.cache.CacheEntry;
import netx.jnlp.cache.ResourceTracker;
import netx.jnlp.cache.UpdatePolicy;

import javax.swing.*;
import javax.swing.border.TitledBorder;
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

public class ApplicationComponent extends VerticalLayoutPanel {
    private URL location;
    private JLabel localLabel;
    private JButton updateButton;
    private JLabel remoteLabel;
    private JLabel status;

    public ApplicationComponent( String title, final URL webstartURL ) throws IOException {
        this.location = webstartURL;
        setFillNone();
        setBorder( new TitledBorder( title ) );
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

        localLabel = new JLabel();
        add( localLabel );
        remoteLabel = new JLabel();
        add( remoteLabel );
        status = new JLabel();
        add( status );
//        currentLabel = new JLabel( "Current" );
//        add( currentLabel );
        updateButton = new JButton( "Update" );
        add( updateButton );
        refresh();
        updateButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    JNLPFile jnlpFile = new JNLPFile( location );
                    ResourcesDesc res = jnlpFile.getResources();

//                    JARDesc jar = res.getMainJAR();
//                    System.out.println( "jar.getLocation() = " + jar.getLocation() );
                    for( int i = 0; i < res.getJARs().length; i++ ) {
                        boolean ok = downloadJAR( res.getJARs()[i] );
                        if( !ok ) {
                            System.err.println( "Downloaded the jar, but not some resources.  This could deceive the user into " +
                                                "Thinking they have downloaded a version when they haven't." );
                            break;
                        }
                    }

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

    private boolean downloadJAR( JARDesc jar ) {
        try {
            ResourceTracker resourceTracker = new ResourceTracker();
            resourceTracker.addResource( jar.getLocation(), null, UpdatePolicy.ALWAYS );
            resourceTracker.startResource( jar.getLocation() );
            return resourceTracker.waitForResource( jar.getLocation(), 1000 );
        }
        catch( InterruptedException e1 ) {
            e1.printStackTrace();
        }
        return false;
    }

    public void refresh() throws IOException {
        long remoteTimeStamp = getRemoteTimeStamp( null, location, null );
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy.MM.dd 'at' hh:mm:ss a z" );
        String remoteString = simpleDateFormat.format( new Date( remoteTimeStamp ) );
        if( remoteTimeStamp == -1 ) {
            remoteLabel.setText( "Offline: remote version unavailable." );
        }
        else {
            remoteLabel.setText( "Remote: " + remoteString );
        }

        long localTimeStamp = getLocalTimeStamp( null, location, null );
        String localString = simpleDateFormat.format( new Date( localTimeStamp ) );
        if( localTimeStamp == -1 ) {
            localLabel.setText( "No Cached Version" );
        }
        else {
            localLabel.setText( "Cached: " + localString );
        }
        if( remoteTimeStamp != -1 && remoteTimeStamp == localTimeStamp ) {
            status.setText( "You have the latest version" );
            status.setForeground( Color.blue );
        }
        else if( remoteTimeStamp != -1 && remoteTimeStamp != localTimeStamp ) {
            status.setText( "There is a newer version online." );
            status.setForeground( Color.red );
        }

        System.out.println( "localString = " + localString );
        System.out.println( "remoteString = " + remoteString );
    }

    public static long getLocalTimeStamp( URLConnection connection, URL source, Version version ) {
        CacheEntry entry = new CacheEntry( source, version ); // could pool this
        if( entry.isCached() ) {
            return entry.getLastModified();
        }
        else {
            return -1;
        }
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
