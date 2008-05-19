/**
 * Class: HelpMenu
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: May 28, 2003
 */
package edu.colorado.phet.common_microwaves.view.components.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetAboutDialog;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

public class HelpMenu extends JMenu {
    public HelpMenu( final JFrame frame ) {
        super( SimStrings.get( "HelpMenu.HelpMenu" ) );
        final JMenuItem about = new JMenuItem( SimStrings.get( "HelpMenu.AboutMenuItem" ) );
        about.setMnemonic( SimStrings.get( "HelpMenu.AboutMnemonic" ).charAt( 0 ) );
        this.setMnemonic( SimStrings.get( "HelpMenu.HelpMnemonic" ).charAt( 0 ) );
        about.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                new PhetAboutDialog( frame, "microwaves" ).show();
            }
        } );
        add( about );
    }
}
