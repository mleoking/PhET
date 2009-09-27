/* Copyright 2009, University of Colorado */

package edu.colorado.phet.naturalselection.developer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.naturalselection.NaturalSelectionApplication;

/**
 * Menu item that provides access to developer controls.
 * Not internationalized.
 */
public class DeveloperControlsMenuItem extends JCheckBoxMenuItem {

    private NaturalSelectionApplication _app;
    private JDialog _developerControlsDialog;

    public DeveloperControlsMenuItem( NaturalSelectionApplication app ) {
        super( "Developer controls..." );
        _app = app;
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleDeveloperControls();
            }
        } );
    }

    private void handleDeveloperControls() {
        System.out.println( "DeveloperControlsMenuItem.handleDeveloperControls" );//XXX
        if ( isSelected() ) {
            Frame owner = PhetApplication.getInstance().getPhetFrame();
            _developerControlsDialog = new DeveloperControlsDialog( owner, _app );
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
