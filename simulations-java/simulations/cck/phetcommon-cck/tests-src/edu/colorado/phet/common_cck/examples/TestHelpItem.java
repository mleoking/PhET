/** Sam Reid*/
package edu.colorado.phet.common_cck.examples;

import edu.colorado.phet.common_cck.view.ApparatusPanel;
import edu.colorado.phet.common_cck.view.help.HelpItem;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: May 26, 2004
 * Time: 12:13:48 PM
 * Copyright (c) May 26, 2004 by Sam Reid
 */
public class TestHelpItem {
    public static void main( String[] args ) {
        JFrame frame = new JFrame();
        ApparatusPanel ap = new ApparatusPanel();
        frame.setContentPane( ap );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        HelpItem hi = new HelpItem( "Hi there,\nSailor!", 100, 0 );
        ap.addGraphic( hi );
        frame.setSize( 500, 500 );
        frame.setVisible( true );
    }
}
