// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.moleculepolarity.developer;

import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JDialog;

import org.jmol.api.JmolViewer;

import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Menu item that opens a Jmol console in a dialog.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class JmolConsoleMenuItem extends JCheckBoxMenuItem {

    private final Frame owner;
    private final JmolViewer viewer;
    private JDialog dialog;

    public JmolConsoleMenuItem( final Frame owner, final JmolViewer viewer ) {
        super( "Jmol Console..." );
        this.owner = owner;
        this.viewer = viewer;
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( isSelected() ) {
                    openDialog();
                }
                else {
                    closeDialog();
                }
            }
        } );
    }

    private void openDialog() {
        if ( dialog != null ) {
            closeDialog();
        }
        dialog = new JDialog( owner ) {{
            setTitle( "Jmol Console" );
            setContentPane( new JmolConsole( viewer ) );
            pack();
            addWindowListener( new WindowAdapter() {

                // called when the close button in the dialog's window dressing is clicked
                @Override
                public void windowClosing( WindowEvent e ) {
                    closeDialog();
                }

                // called by JDialog.dispose
                @Override
                public void windowClosed( WindowEvent e ) {
                    closeDialog();
                    if ( isSelected() ) {
                        setSelected( false );
                    }
                }
            } );
        }};
        SwingUtils.centerInParent( dialog );
        dialog.setVisible( true );
    }

    private void closeDialog() {
        if ( dialog != null ) {
            dialog.dispose();
            dialog = null;
        }
    }
}
