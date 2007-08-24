/*  */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.tests.RunAllTests;
import edu.colorado.phet.waveinterference.util.WIStrings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 10:26:03 PM
 */

public class WaveInterferenceMenu extends JMenu {
    public WaveInterferenceMenu() {
        super( WIStrings.getString( "controls.options" ) );
        JMenuItem jMenuItem = new JMenuItem( WIStrings.getString( "testing.run-tests" ) );
        jMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                RunAllTests.main( new String[0] );
            }
        } );
        add( jMenuItem );
    }
}
