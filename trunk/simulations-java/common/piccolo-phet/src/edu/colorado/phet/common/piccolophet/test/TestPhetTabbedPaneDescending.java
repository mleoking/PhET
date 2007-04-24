/*  */
package edu.colorado.phet.common.piccolophet.test;

import edu.colorado.phet.common.piccolophet.PhetTabbedPane;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Mar 8, 2006
 * Time: 7:48:57 AM
 *
 */

public class TestPhetTabbedPaneDescending {
    public static void main( String[] args ) {
        JFrame frame = new JFrame( "Tab Test" );
        final PhetTabbedPane phetTabbedPane = new PhetTabbedPane();
        phetTabbedPane.addTab( "Hello! you guppies", new JLabel( "Hello" ) );
        phetTabbedPane.addTab( "yuppies and peppering", new JLabel( "Hello" ) );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        frame.setContentPane( phetTabbedPane );
        frame.setSize( 1000, 400 );

        frame.setVisible( true );
    }
}
