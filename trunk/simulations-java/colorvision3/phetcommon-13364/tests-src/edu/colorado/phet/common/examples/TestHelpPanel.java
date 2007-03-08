/** Sam Reid*/
package edu.colorado.phet.common.examples;

import edu.colorado.phet.common.view.help.HelpPanel;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: May 26, 2004
 * Time: 12:18:54 PM
 * Copyright (c) May 26, 2004 by Sam Reid
 */
public class TestHelpPanel {
    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        frame.getContentPane().add( new HelpPanel( null ) );
        frame.setSize( 300, 300 );
        frame.setVisible( true );
    }
}
