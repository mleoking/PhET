/* Copyright 2004, Sam Reid */
package edu.colorado.phet.launcher;

import netx.jnlp.runtime.JNLPRuntime;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Mar 29, 2006
 * Time: 9:17:08 PM
 * Copyright (c) Mar 29, 2006 by Sam Reid
 */

public class PhETLauncher {
    private JFrame frame;
    private ArrayList launchers = new ArrayList();
    private JLabel webAvailableLabel;
    private JPanel contentPane;
    private static final String version = "0.02";

    public PhETLauncher() throws IOException {
//        SecurityManager securityManager = new JNLPSecurityManager();
//        System.setSecurityManager( securityManager );
        JNLPRuntime.initialize();
        JNLPRuntime.setExitClass( PhETLauncher.class );
        frame = new JFrame( "PhET Launcher (" + version + ")" );
        frame.setSize( 600, 600 );

        contentPane = new JPanel();
        contentPane.setLayout( new BoxLayout( contentPane, BoxLayout.Y_AXIS ) );
        frame.setContentPane( contentPane );
        webAvailableLabel = new JLabel();
        contentPane.add( webAvailableLabel );
        JButton refresh = new JButton( "Refresh" );
        refresh.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    refresh();
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        contentPane.add( refresh );

        refresh();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    private void refresh() throws IOException {
        for( int i = 0; i < launchers.size(); i++ ) {
            ApplicationComponent applicationComponent = (ApplicationComponent)launchers.get( i );
            applicationComponent.refresh();
        }
        webAvailableLabel.setText( isWebAvailable() ? "Web is Available" : "You are off-line" );
    }

    private boolean isWebAvailable() {
        try {
            URL google = new URL( "http://www.google.com" );
            URLConnection conn = google.openConnection();
            BufferedReader bufferedReader = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
            bufferedReader.readLine();
            return true;
        }
        catch( MalformedURLException e ) {
        }
        catch( IOException e ) {
        }
        return false;
    }

    private void start() {
        frame.setVisible( true );
    }

    public static void main( String[] args ) throws IOException {
        PhETLauncher phETLauncher = new PhETLauncher();
        phETLauncher.addApplication( "Wave Interference 0.03", "http://www.colorado.edu/physics/phet/dev/waveinterference/0.03/waveinterference.jnlp" );
        phETLauncher.addApplication( "Schrodinger 0.30", "http://www.colorado.edu/physics/phet/dev/schrodinger/0.30/schrodinger.jnlp" );
        phETLauncher.addApplication( "Schrodinger 0.31", "http://www.colorado.edu/physics/phet/dev/schrodinger/0.31/schrodinger.jnlp" );
        phETLauncher.addApplication( "Schrodinger 0.32", "http://www.colorado.edu/physics/phet/dev/schrodinger/0.32/schrodinger.jnlp" );
        phETLauncher.addApplication( "Schrodinger 0.33", "http://www.colorado.edu/physics/phet/dev/schrodinger/0.33/schrodinger.jnlp" );
        phETLauncher.start();
    }

    private void addApplication( String name, String url ) throws IOException {
        URL theURL = new URL( url );
        ApplicationComponent applicationComponent = new ApplicationComponent( name, theURL );
        launchers.add( applicationComponent );
        contentPane.add( applicationComponent );
        refresh();
    }
}
