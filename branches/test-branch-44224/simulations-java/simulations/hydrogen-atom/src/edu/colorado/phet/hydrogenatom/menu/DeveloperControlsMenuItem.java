/* Copyright 2006, University of Colorado */

package edu.colorado.phet.hydrogenatom.menu;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.hydrogenatom.dialog.DeveloperControlsDialog;
import edu.colorado.phet.hydrogenatom.module.HAModule;

/*
 * * "Developer controls" menu item.
 * 
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperControlsMenuItem extends JCheckBoxMenuItem implements ActionListener {

    private HAModule _module;
    private JDialog _developerControlsDialog;

    public DeveloperControlsMenuItem( final HAModule module ) {
        super( "Developer controls..." );
        _module = module;
        addActionListener( this );
    }

    public void actionPerformed( ActionEvent event ) {
        if ( isSelected() ) {
            Frame owner = PhetApplication.getInstance().getPhetFrame();
            _developerControlsDialog = new DeveloperControlsDialog( owner, _module );
            _developerControlsDialog.setVisible( true );
            _developerControlsDialog.addWindowListener( new WindowAdapter() {

                public void windowClosed( WindowEvent e ) {
                    cleanup();
                }

                public void windowClosing( WindowEvent e ) {
                    cleanup();
                }

                private void cleanup() {
                    setSelected( false );
                    _developerControlsDialog = null;
                }
            } );
        }
        else {
            _developerControlsDialog.dispose();
        }
    }
}
