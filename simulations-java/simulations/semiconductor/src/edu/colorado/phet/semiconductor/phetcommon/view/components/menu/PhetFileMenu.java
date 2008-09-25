/**
 * Class: PhetFileMenu
 * Package: edu.colorado.phet.common.view.components.menu
 * Author: Another Guy
 * Date: Jun 17, 2003
 */
package edu.colorado.phet.semiconductor.phetcommon.view.components.menu;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.semiconductor.SemiconductorResources;

public class PhetFileMenu extends JMenu {


    public PhetFileMenu() {
        this( new JComponent[]{} );
    }

    public PhetFileMenu( JComponent[] menuStuff ) {
        super( SemiconductorResources.getString( "PhetFileMenu.FileMenu" ) );
        setMnemonic( SemiconductorResources.getString( "PhetFileMenu.FileMnemonic" ).charAt( 0 ) );
        for ( int i = 0; i < menuStuff.length; i++ ) {
            Component component = menuStuff[i];
            this.add( component );
        }
        JMenuItem exitMI = new JMenuItem( SemiconductorResources.getString( "PhetFileMenu.ExitMenuItem" ) );
        exitMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.exit( 0 );
            }
        } );
        exitMI.setMnemonic( SemiconductorResources.getString( "PhetFileMenu.ExitMnemonic" ).charAt( 0 ) );
        this.add( exitMI );
    }
}
