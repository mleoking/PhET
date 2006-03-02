/* Copyright 2004, Sam Reid */
package edu.colorado.phet.qm.phetcommon;

import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.view.PhetLookAndFeel;
import edu.colorado.phet.qm.SchrodingerApplication;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * User: Sam Reid
 * Date: Mar 1, 2006
 * Time: 5:45:05 PM
 * Copyright (c) Mar 1, 2006 by Sam Reid
 */

public class TestPhetTabbedPane {
    public static void main( String[] args ) {
        PhetLookAndFeel.setLookAndFeel();
        new PhetLookAndFeel().apply();
        JFrame frame = new JFrame( "Tab Test" );
        PhetTabbedPane phetTabbedPane = new PhetTabbedPane();
        SchrodingerApplication schrodingerApplication = new SchrodingerApplication( args );
        schrodingerApplication.startApplication();
        schrodingerApplication.getPhetFrame().setVisible( false );

        for( int i = 0; i < schrodingerApplication.numModules(); i++ ) {
            Module m = schrodingerApplication.moduleAt( i );
            phetTabbedPane.addTab( m.getName(), m.getModulePanel() );
        }

//        phetTabbedPane.addTab( "Hello Tab", new JLabel( "Hello" ) );
//        phetTabbedPane.addTab( "Slider Tab", new JSlider() );
        phetTabbedPane.addTab( "Color Chooser", new JColorChooser() );
        JButton content = new JButton( "Button" );
        phetTabbedPane.addTab( "A Button", content );

        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.setVisible( true );
        frame.addKeyListener( new KeyListener() {
            public void keyPressed( KeyEvent e ) {
                if( e.getKeyCode() == KeyEvent.VK_SPACE || e.getKeyCode() == KeyEvent.VK_X ) {
                    System.exit( 0 );
                }
            }

            public void keyReleased( KeyEvent e ) {
            }

            public void keyTyped( KeyEvent e ) {
            }
        } );
        frame.setContentPane( phetTabbedPane );
        frame.setSize( 800, 600 );
    }
}
