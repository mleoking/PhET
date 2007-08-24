/*  */
package edu.colorado.phet.forces1d.tests;

import edu.colorado.phet.common_force1d.view.ApparatusPanel;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Mar 6, 2005
 * Time: 4:43:03 PM
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
