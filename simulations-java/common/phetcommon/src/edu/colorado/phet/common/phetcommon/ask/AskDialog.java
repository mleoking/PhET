// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.ask;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.windowClosing;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponents.askDialog;

/**
 * Dialog that asks for a donation.
 *
 * @author Aaron Davis
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AskDialog extends JDialog {

    private static final int DISPLAY_TIME = 10; // seconds

    // Constructor is private, creation and display is handled by static methods.
    private AskDialog( PhetApplicationConfig config, Frame parent ) {
        super( parent );
        setResizable( false );
        setContentPane( new AskPanel( config, this ) );
        SwingUtils.setBackgroundDeep( this, Color.WHITE ); // color expected by sponsors
        pack();

        // click on the dialog to make it go away
        addMouseListener( new MouseAdapter() {
            @Override
            public void mousePressed( MouseEvent event ) {
                SimSharingManager.sendUserMessage( askDialog, UserComponentTypes.dialog, UserActions.pressed );
                dispose();
            }
        } );
    }

    // Show the dialog, centered in the parent frame.
    public static JDialog show( PhetApplicationConfig config, Frame parent ) {

        final JDialog dialog = new AskDialog( config, parent );
        dialog.addWindowListener( new WindowAdapter() {
            // called when the close button in the dialog's window dressing is clicked
            public void windowClosing( WindowEvent e ) {
                SimSharingManager.sendUserMessage( askDialog, UserComponentTypes.dialog, windowClosing );
                dialog.dispose();
            }
        } );
        SwingUtils.centerInParent( dialog );
        dialog.setVisible( true );
        return dialog;
    }

    // Should the dialog be displayed?
    public static boolean shouldShow( PhetApplicationConfig config ) {
        return config.isAskFeatureEnabled();
    }
}
