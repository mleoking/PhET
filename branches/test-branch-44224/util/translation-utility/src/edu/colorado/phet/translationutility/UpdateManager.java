/* Copyright 2008-2010, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Properties;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Checks to see if a newer version of Translation Utility is available.
 * If there is a newer version available, tell the user.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class UpdateManager {

    private static final int CONNECTION_TIMEOUT = 4000; // milliseconds
    
    private static final String SVN_REVISION_KEY = "version.revision";

    /* not intended for instantiation */
    private UpdateManager() {}

    /**
     * Reads a properties file on the PhET production server.
     * Compares the SVN revision number property with the SVN revision number of the running program.
     * If they are different, alert the user by opening a dialog. 
     */
    public static void checkForUpdate() {
        
        // Are we connected to the Internet?
        boolean connectedToInternet = false;
        try {
            InetAddress addr = InetAddress.getByName( TUConstants.PHET_HOSTNAME );
            SocketAddress sockaddr = new InetSocketAddress( addr, 80 /* port */ );

            // Create an unbound socket
            Socket sock = new Socket();

            // This method will block until timeout occurs.
            // If timeout occurs, SocketTimeoutException is thrown.
            sock.connect( sockaddr, CONNECTION_TIMEOUT );
            connectedToInternet = true;
        }
        catch ( UnknownHostException e ) {
            System.out.println( "UpdateManager.checkForUpdates: unknown host: " + TUConstants.PHET_HOSTNAME );
        }
        catch ( SocketTimeoutException e ) {
            System.out.println( "UpdateManager.checkForUpdates: connection timed out" );
        }
        catch ( IOException e ) {
            e.printStackTrace();
        }

        if ( connectedToInternet ) {
            
            // display checking dialog
            JDialog checkingDialog = new CheckingDialog( null );
            SwingUtils.centerWindowOnScreen( checkingDialog );
            checkingDialog.setVisible( true );

            // read the remote file into a Properties object
            Properties properties = null;
            try {
                URL url = new URL( TUConstants.TU_LATEST_VERSION_URL );
                InputStream inputStream = url.openStream();
                if ( inputStream != null ) {
                    properties = new Properties();
                    properties.load( inputStream );
                }
            }
            catch ( FileNotFoundException e ) {
                System.out.println( "UpdateManager: " + TUConstants.TU_LATEST_VERSION_URL + " not found" );
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
            
            checkingDialog.dispose();

            // compare the SVN revision numbers
            if ( properties != null ) {
                String thisRevisionString = TUResources.getProjectProperty( SVN_REVISION_KEY );
                String remoteRevisionString = properties.getProperty( SVN_REVISION_KEY );
                if ( remoteRevisionString != null && !thisRevisionString.equals( remoteRevisionString ) ) {
                    try {
                        int thisRevision = Integer.valueOf( thisRevisionString ).intValue();
                        int remoteRevision = Integer.valueOf( remoteRevisionString ).intValue();
                        if ( thisRevision < remoteRevision ) {
                            UpdateDialog updateDialog = new UpdateDialog( null );
                            SwingUtils.centerWindowOnScreen( updateDialog );
                            updateDialog.setVisible( true );
                        }
                    }
                    catch ( NumberFormatException nfe ) {
                        System.out.println( "UpdateManager: SVN revision number is not an integer" );
                    }
                    
                }
            }
        }
    }
    
    /*
     * Dialog that is displayed while we're checking the remote file.
     */
    private static class CheckingDialog extends JDialog {
        
        public CheckingDialog( Frame owner ) {
            super( owner, false /* modal */ );
            setResizable( false );
            JLabel messageLabel = new JLabel( TUStrings.CHECKING_FOR_UPDATE_MESSAGE );
            JPanel panel = new JPanel();
            final int margin = 15;
            panel.setBorder( BorderFactory.createEmptyBorder( margin, margin, margin, margin ) );
            panel.add( messageLabel );
            getContentPane().add( panel );
            pack();
        }
    }

    /*
     * Dialog that directs the user to the latest version.
     */
    private static class UpdateDialog extends JDialog {

        public UpdateDialog( Frame owner ) {
            super( owner, true /* modal */);
            setTitle( TUStrings.UPDATE_DIALOG_TITLE );
            setResizable( false );

            // message
            JLabel messageLabel = new JLabel( TUStrings.UPDATE_AVAILABLE_MESSAGE );
            messageLabel.setFont( new PhetFont( 14 ) );

            JButton getNewVersionButton = new JButton( TUStrings.GET_NEW_VERSION_BUTTON );
            getNewVersionButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    PhetServiceManager.showWebPage( TUConstants.TU_HOME_URL );
                    System.exit( 0 );
                }
            } );
            
            JButton continueButton = new JButton( TUStrings.CONTINUE_WITH_OLD_VERSION_BUTTON );
            continueButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    dispose();
                }
            } );

            JPanel buttonPanel = new JPanel();
            EasyGridBagLayout buttonPanelLayout = new EasyGridBagLayout( buttonPanel );
            buttonPanel.setLayout( buttonPanelLayout );
            buttonPanelLayout.addComponent( getNewVersionButton, 0, 0 );
            buttonPanelLayout.addComponent( continueButton, 0, 1 );

            JPanel mainPanel = new JPanel();
            final int margin = 15;
            mainPanel.setBorder( BorderFactory.createEmptyBorder( margin, margin, margin, margin ) );
            EasyGridBagLayout mainLayout = new EasyGridBagLayout( mainPanel );
            mainLayout.setAnchor( GridBagConstraints.CENTER );
            mainPanel.setLayout( mainLayout );
            mainLayout.addComponent( messageLabel, 0, 0 );
            mainLayout.addFilledComponent( new JSeparator(), 1, 0, GridBagConstraints.HORIZONTAL );
            mainLayout.addComponent( buttonPanel, 2, 0 );

            getContentPane().add( mainPanel );
            pack();
        }
    }
}
