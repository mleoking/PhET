package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;

/**
 * The update button that appears in installer update dialogs.
 * Pressing this button requests an installer update.
 */
public class InstallerUpdateButton extends JButton {
    
    private static final String INSTALLER_URL = "http://phet.colorado.edu/get_phet/full_install.php";
    
    public InstallerUpdateButton() {
        super( PhetCommonResources.getString( "Common.updates.installer.yes" ) );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                PhetServiceManager.showWebPage( INSTALLER_URL );
            }
        } );
    }
}