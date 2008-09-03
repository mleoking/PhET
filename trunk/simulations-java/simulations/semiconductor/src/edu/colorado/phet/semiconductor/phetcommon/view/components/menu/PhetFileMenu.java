/**
 * Class: PhetFileMenu
 * Package: edu.colorado.phet.common.view.components.menu
 * Author: Another Guy
 * Date: Jun 17, 2003
 */
package edu.colorado.phet.semiconductor.phetcommon.view.components.menu;

import edu.colorado.phet.common.phetcommon.view.util.SimStrings;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PhetFileMenu extends JMenu {

    static {
        SimStrings.setStrings( "localization/SemiConductorPCStrings" );
    }

    public PhetFileMenu() {
        this( new JComponent[]{} );
    }

    public PhetFileMenu( JComponent[] menuStuff ) {
        super( SimStrings.get( "PhetFileMenu.FileMenu" ) );
        setMnemonic( SimStrings.get( "PhetFileMenu.FileMnemonic" ).charAt( 0 ) );
        for( int i = 0; i < menuStuff.length; i++ ) {
            Component component = menuStuff[i];
            this.add( component );
        }
        JMenuItem exitMI = new JMenuItem( SimStrings.get( "PhetFileMenu.ExitMenuItem" ) );
        exitMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.exit( 0 );
            }
        } );
        exitMI.setMnemonic( SimStrings.get( "PhetFileMenu.ExitMnemonic" ).charAt( 0 ) );
        this.add( exitMI );
    }
}
