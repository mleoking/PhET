// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.piccolophet;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

/**
 * Menu item that provides access to controls for properties related to a Piccolo-based tabbed pane.
 * This menu item is intended to be added to the Developer menu, and is therefore not internationalized.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class TabbedPanePropertiesMenuItem extends JCheckBoxMenuItem {

    private static final String ITEM_LABEL = "Tabbed Pane properties..."; // developer control, i18n not required

    private JDialog dialog;

    public TabbedPanePropertiesMenuItem( final Frame owner, final PhetTabbedPane tabbedPane ) {
        super( ITEM_LABEL );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleAction( owner, tabbedPane );
            }
        } );
    }

    private void handleAction( final Frame owner, final PhetTabbedPane tabbedPane ) {
        if ( isSelected() ) {
            dialog = new TabbedPanePropertiesDialog( owner, tabbedPane );
            dialog.setVisible( true );
            dialog.addWindowListener( new WindowAdapter() {

                public void windowClosed( WindowEvent e ) {
                    cleanup();
                }

                public void windowClosing( WindowEvent e ) {
                    cleanup();
                }

                private void cleanup() {
                    setSelected( false );
                    dialog = null;
                }
            } );
        }
        else {
            dialog.dispose();
        }
    }
}
