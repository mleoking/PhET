/* Copyright 2004, Sam Reid */
package edu.colorado.phet.phetlauncher2;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import netx.jnlp.ParseException;

import javax.swing.*;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * User: Sam Reid
 * Date: Apr 2, 2006
 * Time: 3:15:39 PM
 * Copyright (c) Apr 2, 2006 by Sam Reid
 */

public class SimulationControl extends HorizontalLayoutPanel {
    private JLabel localLabel;
    private JLabel remoteLabel;
    private PhetLauncher phetLauncher;
    private Simulation simulation;
    private JLabel statusLabel;
    private boolean onlineAllowed = false;

    public SimulationControl( PhetLauncher phetLauncher, final Simulation simulation ) throws IOException {
        this( phetLauncher, simulation, false );
    }

    public SimulationControl( PhetLauncher phetLauncher, final Simulation simulation, boolean onlineAllowed ) throws IOException {
        this.onlineAllowed = onlineAllowed;
        this.phetLauncher = phetLauncher;
        this.simulation = simulation;
        setBorder( BorderFactory.createTitledBorder( simulation.getTitle() ) );
        if( simulation.getImage() != null ) {
            add( new JLabel( new ImageIcon( simulation.getImage() ) ) );
        }
        JEditorPane comp = new JEditorPane();
        comp.setBackground( Color.black );
        comp.setPreferredSize( new Dimension( 300, 120 ) );
        comp.setEditable( false );
        comp.setEditorKit( new HTMLEditorKit2() );
        comp.setPage( simulation.getAbstractURL() );
        add( comp );

        localLabel = new JLabel();
        remoteLabel = new JLabel();
        statusLabel = new JLabel();
        VerticalLayoutPanel statusPanel = new VerticalLayoutPanel();
        statusPanel.setFillNone();
        statusPanel.add( localLabel );
        statusPanel.add( remoteLabel );
        statusPanel.add( statusLabel );
        add( statusPanel );
        JButton checkStatus = new JButton( "Check Status" );
        checkStatus.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                checkStatus();
            }

        } );
        statusPanel.add( checkStatus );
        JButton downloadResources = new JButton( "Download" );
        downloadResources.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    simulation.downloadResources();
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
                catch( ParseException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        statusPanel.add( downloadResources );
        JButton launchLocal = new JButton( "Launch" );
        launchLocal.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    simulation.launch();
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        simulation.addListener( new Simulation.Listener() {
            public void downloadCompleted() {
                SimulationControl.this.onlineAllowed = true;
                update();
            }

            public void checkStatus() {
            }
        } );
        statusPanel.add( launchLocal );
        update();
        simulation.addListener( new Simulation.Listener() {
            public void downloadCompleted() {
            }

            public void checkStatus() {
                SimulationControl.this.checkStatus();
            }
        } );
    }

    private void checkStatus() {
        if( !phetLauncher.isOnline() ) {
            JOptionPane.showMessageDialog( this, "Must be online to check status." );
        }
        else {
            this.onlineAllowed = true;
            update();
        }
    }

    public String format( long time ) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "EEE d MMM yyyy h:mm:ss a z" );
        return simpleDateFormat.format( new Date( time ) );
    }

    private void update() {
        boolean remoteAvailable = onlineAllowed() && simulation.isRemoteVersionAvailable();
        long remoteTimeStamp = remoteAvailable ? simulation.getRemoteTimeStamp() : 0;
        if( remoteAvailable ) {
            remoteLabel.setText( "Remote Version: " + format( remoteTimeStamp ) );
        }
        else {
            remoteLabel.setText( "Remote Version Unknown" );
        }
        if( simulation.localCopyExists() ) {
            localLabel.setText( "Local Version: " + format( simulation.getLocalTimeStamp() ) );
            localLabel.setForeground( Color.black );
        }
        else {
            localLabel.setText( "Not Downloaded" );
            localLabel.setForeground( Color.red );
        }
        if( simulation.localCopyExists() && remoteAvailable ) {
            if( remoteTimeStamp == simulation.getLocalTimeStamp() ) {
                statusLabel.setText( "Up To Date" );
                statusLabel.setForeground( Color.blue );
            }
            else {
                statusLabel.setText( "New Version Available" );
                statusLabel.setForeground( Color.red );
            }
        }
        else {
            statusLabel.setText( "" );
        }
    }

    private boolean onlineAllowed() {
        return onlineAllowed;
    }

    class HTMLEditorKit2 extends HTMLEditorKit {
        public Document createDefaultDocument() {
            HTMLDocument doc = (HTMLDocument)( super.createDefaultDocument() );
            doc.setAsynchronousLoadPriority( -1 ); // load synchronously
            return doc;
        }
    }
}
