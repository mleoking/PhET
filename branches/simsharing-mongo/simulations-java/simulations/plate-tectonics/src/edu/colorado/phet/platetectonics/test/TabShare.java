// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.platetectonics.test;


import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

public class TabShare extends JFrame {

    public TabShare() {
        super( "TabShare Test" );

        JTabbedPane tabPane = new JTabbedPane();

        // Make the "always visible" panel the first child so
        // that it always remains "on top" of the z-order
        final JPanel panel = new JPanel() {
            {
                add( new JButton( "Hello" ) );
                setPreferredSize( new Dimension( 200, 100 ) );
            }

            @Override public void setVisible( boolean aFlag ) {
                // do nothing, so it stays visible
            }
        };
        tabPane.addComponentListener( new ComponentAdapter() {
            @Override public void componentResized( ComponentEvent e ) {
                JTabbedPane tabPane = (JTabbedPane) e.getSource();
                Component selected = tabPane.getComponentAt( tabPane.getSelectedIndex() );
                panel.setSize( selected.getSize() );
                panel.validate();
            }
        } );
        tabPane.add( "One", panel );

        tabPane.add( "Two", new JLabel( "dummy" ) );
        tabPane.add( "Three", new JLabel( "dummy" ) );
        tabPane.add( "Four", new JLabel( "dummy" ) );

        getContentPane().add( tabPane, BorderLayout.CENTER );
        pack();
        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    public static void main( String[] args ) {
        TabShare test = new TabShare();
        test.setVisible( true );
    }
}