// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.balancingchemicalequations.developer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Developer menu item for previewing all of the molecules used in this sim.
 * No i18n since this is visible only in developer mode.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PreviewMoleculesMenuItem extends JCheckBoxMenuItem {

    private JDialog dialog;

    public PreviewMoleculesMenuItem( final Frame parentFrame ) {
        super( "Preview molecules..." );
        // the menu item was changed
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                handleDialog( parentFrame );
            }
        } );
    }

    /*
     * Open or close the dialog, depending on whether the menu item is selected.
     */
    private void handleDialog( Frame parentFrame ) {
        if ( isSelected() ) {
            dialog = new DeveloperMoleculesDialog( parentFrame );
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

    // The dialog...
    private static class DeveloperMoleculesDialog extends JDialog {
        public DeveloperMoleculesDialog( Frame parentFrame ) {
            super( parentFrame );
            setTitle( "Molecules" );
            setModal( false );
            setResizable( true );
            setContentPane( new PreviewMoleculesCanvas( parentFrame ) );
            pack();
            SwingUtils.centerInParent( this );
        }
    }
}
