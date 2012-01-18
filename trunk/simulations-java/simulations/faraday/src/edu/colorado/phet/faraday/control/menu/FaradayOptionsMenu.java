// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.faraday.control.menu;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JMenuItem;

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.simsharing.components.SimSharingJMenuItem;
import edu.colorado.phet.common.phetcommon.view.menu.OptionsMenu;
import edu.colorado.phet.faraday.FaradaySimSharing.UserComponents;
import edu.colorado.phet.faraday.FaradayStrings;
import edu.colorado.phet.faraday.control.dialog.BackgroundColorHandler;
import edu.colorado.phet.faraday.control.dialog.GridControlsDialog;


/**
 * OptionsMenu implements the Options menu that appears in the Faraday menubar.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class FaradayOptionsMenu extends OptionsMenu {

    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------

    /**
     * Sole constructor.
     *
     * @param application
     */
    public FaradayOptionsMenu( final PhetApplication application ) {
        super();

        // Background Color menu item, disabled when dialog is open
        final JMenuItem backgroundColorMenuItem = new SimSharingJMenuItem( UserComponents.backgroundColorMenuItem, FaradayStrings.MENU_ITEM_BACKGROUND_COLOR );
        backgroundColorMenuItem.setMnemonic( FaradayStrings.MNEMONIC_BACKGROUND_COLOR );
        backgroundColorMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                BackgroundColorHandler handler = new BackgroundColorHandler( application );
                JDialog backgroundColorDialog = handler.getDialog();
                backgroundColorDialog.addWindowListener( new WindowAdapter() {
                    // called when dispose is called
                    public void windowClosed( WindowEvent e ) {
                        backgroundColorMenuItem.setEnabled( true );
                    }

                    // called when the close button in the dialog's window dressing is clicked
                    public void windowClosing( WindowEvent e ) {
                        backgroundColorMenuItem.setEnabled( true );
                    }
                } );
                backgroundColorDialog.setVisible( true );
                backgroundColorMenuItem.setEnabled( false );
            }
        } );
        add( backgroundColorMenuItem );

        // Grid Controls dialog, disabled when dialog is open
        final JMenuItem gridControlsMenuItem = new SimSharingJMenuItem( UserComponents.backgroundColorMenuItem, FaradayStrings.MENU_ITEM_GRID_CONTROLS );
        gridControlsMenuItem.setMnemonic( FaradayStrings.MNEMONIC_GRID_CONTROLS );
        gridControlsMenuItem.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                final GridControlsDialog gridControlsDialog = new GridControlsDialog( application );
                gridControlsDialog.addWindowListener( new WindowAdapter() {
                    // called when dispose is called
                    public void windowClosed( WindowEvent e ) {
                        gridControlsMenuItem.setEnabled( true );
                    }

                    // called when the close button in the dialog's window dressing is clicked
                    public void windowClosing( WindowEvent e ) {
                        gridControlsDialog.revert();
                        gridControlsMenuItem.setEnabled( true );
                    }
                } );
                gridControlsDialog.setVisible( true );
                gridControlsMenuItem.setEnabled( false );
            }
        } );
        add( gridControlsMenuItem );
    }
}
