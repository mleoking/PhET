/* Copyright 2008, University of Colorado */

package edu.colorado.phet.statesofmatter.developer;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.statesofmatter.AbstractStatesOfMatterApp;
import edu.colorado.phet.statesofmatter.StatesOfMatterApplication;

/**
 * Menu item that provides access to developer controls.
 * Not internationalized.
 */
public class DeveloperControlsMenuItem extends JCheckBoxMenuItem {

    private AbstractStatesOfMatterApp _app;

    private JDialog _developerControlsDialog;

    public DeveloperControlsMenuItem( AbstractStatesOfMatterApp app ) {
        super( "Developer controls..." );

        _app = app;

        addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent event ) {
                handleDeveloperControls();
            }
        } );
    }

    private void handleDeveloperControls() {
        if ( isSelected() ) {
            Frame owner = PhetApplication.getInstance().getPhetFrame();
            if ( _app instanceof StatesOfMatterApplication ) {
                _developerControlsDialog = new StatesOfMaterDeveloperControlsDialog( owner, _app );
            }
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