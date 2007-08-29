/**
 * Class: HelpMenu
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: May 28, 2003
 */
package edu.colorado.phet.common.conductivity.view.components.menu;

import edu.colorado.phet.common.conductivity.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.PhetAboutDialog;
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HelpMenu extends JMenu {

    static {
        SimStrings.setStrings( "localization/ConductivityPCStrings" );
    }

    public HelpMenu( final PhetApplication app ) {
        super( SimStrings.get( "HelpMenu.HelpMenu" ) );
        final JMenuItem about = new JMenuItem( SimStrings.get( "HelpMenu.AboutMenuItem" ) );
        about.setMnemonic( SimStrings.get( "HelpMenu.AboutMnemonic" ).charAt( 0 ) );
        this.setMnemonic( SimStrings.get( "HelpMenu.HelpMnemonic" ).charAt( 0 ) );
        about.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
//                JOptionPane.showMessageDialog( about, msg, SimStrings.get( "HelpMenu.AboutLabel" ) + " " + name, JOptionPane.INFORMATION_MESSAGE );
                new PhetAboutDialog( (Frame)SwingUtilities.getWindowAncestor( HelpMenu.this ), "conductivity").show( );
            }
        } );
        add( about );
    }
}
