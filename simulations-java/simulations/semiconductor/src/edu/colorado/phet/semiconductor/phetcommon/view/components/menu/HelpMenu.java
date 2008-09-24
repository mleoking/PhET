/**
 * Class: HelpMenu
 * Package: edu.colorado.phet.common.view
 * Author: Another Guy
 * Date: May 28, 2003
 */
package edu.colorado.phet.semiconductor.phetcommon.view.components.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetAboutDialog;
import edu.colorado.phet.common.phetcommon.view.util.SimStrings;
import edu.colorado.phet.semiconductor.SemiconductorResources;
import edu.colorado.phet.semiconductor.phetcommon.view.PhetFrame;

public class HelpMenu extends JMenu {

    static {
        SimStrings.setStrings( "localization/SemiConductorPCStrings" );
    }

    public HelpMenu( final PhetFrame phetFrame ) {
        super( SemiconductorResources.getString( "HelpMenu.HelpMenu" ) );
        this.setMnemonic( SemiconductorResources.getString( "HelpMenu.HelpMnemonic" ).charAt( 0 ) );

        final JMenuItem about = new JMenuItem( SemiconductorResources.getString( "HelpMenu.AboutMenuItem" ) );
        about.setMnemonic( SemiconductorResources.getString( "HelpMenu.AboutMnemonic" ).charAt( 0 ) );
        about.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                new PhetAboutDialog( phetFrame, "semiconductor" ).show();
            }
        } );
        add( about );
    }
}
