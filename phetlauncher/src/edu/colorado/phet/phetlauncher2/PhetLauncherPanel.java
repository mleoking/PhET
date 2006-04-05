/* Copyright 2004, Sam Reid */
package edu.colorado.phet.phetlauncher2;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.common.view.util.ImageLoader;
import net.n3.nanoxml.XMLException;
import netx.jnlp.ParseException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * User: Sam Reid
 * Date: Apr 2, 2006
 * Time: 3:00:20 PM
 * Copyright (c) Apr 2, 2006 by Sam Reid
 */

public class PhetLauncherPanel extends JPanel {
    private PhetLauncher phetLauncher;
    private final JLabel simulationListLabel;
    private final JLabel onlineIndicator;
    private final VerticalLayoutPanel simulationEntries;
    private JButton connectToWeb;

    public PhetLauncherPanel( final PhetLauncher phetLauncher ) throws IOException {
        setLayout( new BorderLayout() );
        this.phetLauncher = phetLauncher;
        VerticalLayoutPanel appPanel = new VerticalLayoutPanel();
        appPanel.setFillNone();
        connectToWeb = new JButton( "Check Again" );
        connectToWeb.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                phetLauncher.tryToConnect();
                updateOnlineIndicator();
            }
        } );

        JButton checkListStatus = new JButton( "Check Again" );
        checkListStatus.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    updateLabel();
                    updateOnlineIndicator();
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );

        JButton refreshAll = new JButton( "Refresh Simulation List" );
        refreshAll.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    phetLauncher.refreshSimulationList();
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );

        simulationListLabel = new JLabel();

        phetLauncher.addListener( new PhetLauncher.Listener() {
            public void simulationFileRefreshed() {
                try {
                    updateLabel();
                    updateOnlineIndicator();
                }
                catch( IOException e ) {
                    e.printStackTrace();
                }
            }
        } );
        onlineIndicator = new JLabel();

        JButton synchronizeAll = new JButton( "Synchronize Everything" );
        synchronizeAll.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    phetLauncher.synchronizeAll();
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
                catch( IllegalAccessException e1 ) {
                    e1.printStackTrace();
                }
                catch( XMLException e1 ) {
                    e1.printStackTrace();
                }
                catch( ParseException e1 ) {
                    e1.printStackTrace();
                }
                catch( InstantiationException e1 ) {
                    e1.printStackTrace();
                }
                catch( ClassNotFoundException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );

        updateOnlineIndicator();
        updateLabel();

        HorizontalLayoutPanel onlinePair = new HorizontalLayoutPanel();
        onlinePair.add( onlineIndicator );
        onlinePair.add( connectToWeb );
        appPanel.add( onlinePair );

        HorizontalLayoutPanel listPair = new HorizontalLayoutPanel();
        listPair.add( simulationListLabel );
        listPair.add( checkListStatus );
        appPanel.add( listPair );

        appPanel.add( refreshAll );

        JButton downloadAll = new JButton( "Update All Descriptions" );
        downloadAll.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                try {
                    phetLauncher.downloadDescriptions();
                }
                catch( IOException e1 ) {
                    e1.printStackTrace();
                }
                catch( ParseException e1 ) {
                    e1.printStackTrace();
                }
                catch( IllegalAccessException e1 ) {
                    e1.printStackTrace();
                }
                catch( XMLException e1 ) {
                    e1.printStackTrace();
                }
                catch( ClassNotFoundException e1 ) {
                    e1.printStackTrace();
                }
                catch( InstantiationException e1 ) {
                    e1.printStackTrace();
                }
            }
        } );
        JButton checkStatusAll = new JButton( "Check Status For All" );
        checkStatusAll.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                phetLauncher.checkStatusAll();
            }

        } );
        appPanel.add( downloadAll );
        appPanel.add( checkStatusAll );
        appPanel.add( synchronizeAll );
        HorizontalLayoutPanel northPanel = new HorizontalLayoutPanel();
        northPanel.add( new JLabel( new ImageIcon( ImageLoader.loadBufferedImage( "images/phetlauncher-icon.jpg" ) ) ) );
        northPanel.add( appPanel );
        add( northPanel, BorderLayout.NORTH );
        simulationEntries = new VerticalLayoutPanel();
        JScrollPane comp = new JScrollPane( simulationEntries );
        comp.setPreferredSize( new Dimension( 600, 500 ) );
        add( comp, BorderLayout.CENTER );
    }

    public void updateAll() throws IOException {
        updateOnlineIndicator();
        updateLabel();
    }

    private void updateOnlineIndicator() {
        boolean online = phetLauncher.isOnline();
        String onlineStr = online ? "You are online" : "You are currently offline";
        Color color = online ? Color.blue : Color.red;
        onlineIndicator.setText( onlineStr );
        onlineIndicator.setForeground( color );
//        connectToWeb.setEnabled( !phetLauncher.isOnline() );
    }

    private void updateLabel() throws IOException {
        boolean current = phetLauncher.isSimulationListCurrent();
        String text = current ? "Simulation List is current" : "Simulation List is not current";
        simulationListLabel.setForeground( current ? Color.blue : Color.red );
        simulationListLabel.setText( text );
    }

//    public void addSimulationEntry( SimulationControl simulationControl ) {

    public void addSimulationEntry( JComponent simulationControl ) {
        simulationEntries.add( simulationControl );
        simulationEntries.revalidate();
    }

    public void clearSimulationEntries() {
        simulationEntries.removeAll();
    }
}
