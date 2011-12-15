// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.common.phetcommon.sponsorship;

import java.awt.Color;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.Timer;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Actions;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingStrings.Parameters;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Dialog that displays a simulation's sponsor.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SponsorDialog extends JDialog {

    public static final String SIMSHARING_OBJECT = "sponsorDialog";

    private static final int DISPLAY_TIME = 5; // seconds

    // Constructor is private, creation and display is handled by static methods.
    private SponsorDialog( PhetApplicationConfig config, Frame parent ) {
        super( parent );
        setResizable( false );
        setContentPane( new SponsorPanel( config ) );
        SwingUtils.setBackgroundDeep( this, Color.WHITE ); // color expected by sponsors
        pack();

        // click on the dialog to make it go away
        addMouseListener( new MouseAdapter() {
            @Override
            public void mousePressed( MouseEvent event ) {
                SimSharingManager.sendEvent( SIMSHARING_OBJECT, Actions.PRESSED );
                dispose();
            }
        } );
    }

    // Show the dialog, centered in the parent frame.
    public static JDialog show( PhetApplicationConfig config, Frame parent, boolean startDisposeTimer ) {

        final JDialog dialog = new SponsorDialog( config, parent );
        dialog.addWindowListener( new WindowAdapter() {
            // called when the close button in the dialog's window dressing is clicked
            public void windowClosing( WindowEvent e ) {
                SimSharingManager.sendEvent( SponsorDialog.SIMSHARING_OBJECT, Actions.WINDOW_SYSTEM_CLOSE_BUTTON_PRESSED );
                dialog.dispose();
            }
        } );
        SwingUtils.centerInParent( dialog );
        dialog.setVisible( true );

        if ( startDisposeTimer ) {
            // Dispose of the dialog after DISPLAY_TIME seconds. Take care to call dispose in the Swing thread.
            final Timer timer = new Timer( DISPLAY_TIME * 1000, new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    if ( dialog.isDisplayable() ) {
                        SimSharingManager.sendSystemEvent( Actions.CLOSED, Parameter.param( Parameters.WINDOW, SIMSHARING_OBJECT ) );
                        dialog.dispose();
                    }
                }
            } );
            timer.setRepeats( false );
            dialog.addWindowListener( new WindowAdapter() {
                @Override public void windowClosed( WindowEvent e ) {
                    timer.stop();
                }
            } );
            timer.start();
        }
        return dialog;
    }

    // Should the dialog be displayed?
    public static boolean shouldShow( PhetApplicationConfig config ) {
        boolean isFeatureEnabled = config.isSponsorFeatureEnabled();
        boolean isWellFormed = new SponsorProperties( config ).isWellFormed();
        boolean hasProgramArg = config.hasCommandLineArg( "-sponsorPrototype" ); //TODO delete this after prototyping
        return isFeatureEnabled && isWellFormed && hasProgramArg;
    }
}
