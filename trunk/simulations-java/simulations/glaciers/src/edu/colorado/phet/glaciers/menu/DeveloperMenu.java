/* Copyright 2007-2008, University of Colorado */

package edu.colorado.phet.glaciers.menu;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;
import javax.swing.JMenu;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.glaciers.GlaciersApplication;
import edu.colorado.phet.glaciers.dialog.DeveloperControlsDialog;

/**
 * DeveloperMenu is the "Developer" menu that appears in the menu bar.
 * This menu contains global developer-only features for tuning and debugging.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DeveloperMenu extends JMenu {

    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final GlaciersApplication _app;
    private final JCheckBoxMenuItem _developerControlsItem;
    private JDialog _developerControlsDialog;

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DeveloperMenu( GlaciersApplication app ) {
        super( "Developer" );

        _app = app;

        _developerControlsItem = new JCheckBoxMenuItem( "Developer Controls..." );
        add( _developerControlsItem );
        _developerControlsItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleDeveloperControls();
            }
        } );
    }
    
    //----------------------------------------------------------------------------
    // Event handlers
    //----------------------------------------------------------------------------

    private void handleDeveloperControls() {
        if ( _developerControlsItem.isSelected() ) {
            Frame owner = PhetApplication.instance().getPhetFrame();
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
                    _developerControlsItem.setSelected( false );
                    _developerControlsDialog = null;
                }
            } );
        }
        else {
            _developerControlsDialog.dispose();
        }
    }
}
