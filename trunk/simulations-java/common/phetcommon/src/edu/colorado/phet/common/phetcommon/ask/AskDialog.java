// Copyright 2015, University of Colorado Boulder

package edu.colorado.phet.common.phetcommon.ask;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponentTypes;
import edu.colorado.phet.common.phetcommon.view.util.GridPanel;
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

    private static final String DONATE_URL = "https://donatenow.networkforgood.org/1437859";

    // Constructor is private, creation and display is handled by (static) show method.
    private AskDialog( Frame parent ) {
        super( parent );
        setResizable( false );
        setContentPane( new AskPanel( this ) );
        SwingUtils.setBackgroundDeep( this, Color.WHITE );
        pack();
    }

    // Shows the dialog, centered in the parent frame.
    public static JDialog show( Frame parent ) {

        final JDialog dialog = new AskDialog( parent );
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

    // Content for the Ask dialog
    private class AskPanel extends GridPanel {

        public AskPanel( final Dialog dialog ) {

            // layout components, some of which are optional
            int xMargin = 40;
            int yMargin = 20;
            setBorder( new CompoundBorder( new LineBorder( Color.BLACK, 1 ), new EmptyBorder( yMargin, xMargin, yMargin, xMargin ) ) );
            setGridX( 0 ); // vertical
            setAnchor( Anchor.CENTER );

            add( Box.createVerticalStrut( 15 ) );

            // image
            add( new JLabel( new ImageIcon( PhetCommonResources.getImage( "ask/ask.png" ) ) ) );

            add( Box.createVerticalStrut( 15 ) );

            // Opens a web browser and closes the dialog
            JButton donateButton = new JButton( "Make a Donation!" );
            donateButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    PhetServiceManager.showWebPage( DONATE_URL );
                    dialog.dispose();
                }
            } );
            add( donateButton );

            // Closes the dialog
            JButton maybeLaterButton = new JButton( "Maybe later" );
            maybeLaterButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    dialog.dispose();
                }
            } );
            add( maybeLaterButton );

            // Closes the dialog
            JButton playButton = new JButton( "Play" );
            playButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    dialog.dispose();
                }
            } );
            add( playButton );

            add( Box.createVerticalStrut( 15 ) );
        }
    }
}
