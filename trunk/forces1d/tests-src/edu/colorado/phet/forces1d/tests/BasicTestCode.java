/* Copyright 2004, Sam Reid */
package edu.colorado.phet.forces1d.tests;

import edu.colorado.phet.common.view.ApparatusPanel;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Mar 6, 2005
 * Time: 4:43:03 PM
 * Copyright (c) Mar 6, 2005 by Sam Reid
 */

public class BasicTestCode {
    ApparatusPanel apparatusPanel = new ApparatusPanel();
    private JFrame frame;

    public BasicTestCode() {
        frame = new JFrame( "Test" );
        frame.setContentPane( apparatusPanel );
        frame.setSize( 600, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
    }

    public void start() {
        frame.setVisible( true );
    }

}
