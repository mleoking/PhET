/**
 * Class: HelpMenu
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: May 28, 2003
 */
package edu.colorado.phet.semiconductor.phetcommon.view.components.menu;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.common.phetcommon.application.PhetAboutDialog;
import edu.colorado.phet.semiconductor.phetcommon.application.PhetApplication;
import edu.colorado.phet.semiconductor.phetcommon.util.VersionUtils;
import edu.colorado.phet.semiconductor.phetcommon.view.PhetFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class HelpMenu extends JMenu {

    static {
        SimStrings.setStrings( "localization/SemiConductorPCStrings" );
    }

    public HelpMenu( final PhetFrame phetFrame ) {
        super( SimStrings.get( "HelpMenu.HelpMenu" ) );
        this.setMnemonic( SimStrings.get( "HelpMenu.HelpMnemonic" ).charAt( 0 ) );

        final JMenuItem about = new JMenuItem( SimStrings.get( "HelpMenu.AboutMenuItem" ) );
        about.setMnemonic( SimStrings.get( "HelpMenu.AboutMnemonic" ).charAt( 0 ) );
        about.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                new PhetAboutDialog( phetFrame, "semiconductor").show( );
            }
        } );
        add( about );
    }
}
