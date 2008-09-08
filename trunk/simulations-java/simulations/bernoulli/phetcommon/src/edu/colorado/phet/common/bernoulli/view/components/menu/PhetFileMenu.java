/**
 * Class: PhetFileMenu
 * Package: edu.colorado.phet.common.view.components.menu
 * Author: Another Guy
 * Date: Jun 17, 2003
 */
package edu.colorado.phet.common.bernoulli.view.components.menu;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.*;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

public class PhetFileMenu extends JMenu {

    public PhetFileMenu() {
        this( new JComponent[]{} );
    }

    public PhetFileMenu( JComponent[] menuStuff ) {
        super( PhetCommonResources.getString( "Common.FileMenu.Title" ));
        for( int i = 0; i < menuStuff.length; i++ ) {
            Component component = menuStuff[i];
            this.add( component );
        }
        JMenuItem exitMI = new JMenuItem( PhetCommonResources.getString( "Common.FileMenu.Exit" ) );
        exitMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                System.exit( 0 );
            }
        } );
        this.add( exitMI );
    }
}
