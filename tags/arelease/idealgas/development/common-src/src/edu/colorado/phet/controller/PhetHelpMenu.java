/**
 * Class: PhetHelpMenu
 * Package: edu.colorado.phet.controller
 * User: Ron LeMaster
 * Date: Dec 9, 2002
 * Time: 3:57:20 PM
 */
package edu.colorado.phet.controller;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class provides the default Help menu for Phet applications.
 * It has one item, an About.. selection that displays an application-specific
 * modal dialog.
 * @see PhetAboutDialog
 */
public class PhetHelpMenu extends JMenu {

    JFrame parent;
    PhetAboutDialog aboutDialog;

    public PhetHelpMenu( JFrame parent, PhetAboutDialog aboutDialog ) {
        super( "Help" );
        this.parent = parent;
        this.aboutDialog = aboutDialog;
        JMenuItem aboutMI = new JMenuItem( "About..." );
        aboutMI.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PhetHelpMenu.this.aboutDialog.show();
            }
        } );
        this.add( aboutMI );
    }
}
