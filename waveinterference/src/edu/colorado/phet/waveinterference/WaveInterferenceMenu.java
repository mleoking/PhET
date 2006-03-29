/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.tests.RunAllTests;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 10:26:03 PM
 * Copyright (c) Mar 28, 2006 by Sam Reid
 */

public class WaveInterferenceMenu extends JMenu {
    public WaveInterferenceMenu() {
        super( "Options" );
        JMenuItem jMenuItem = new JMenuItem( "Run All Tests" );
        jMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                RunAllTests.main( new String[0] );
            }
        } );
        add( jMenuItem );
    }
}
