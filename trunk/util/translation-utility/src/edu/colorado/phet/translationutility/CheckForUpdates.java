/* Copyright 2008, University of Colorado */

package edu.colorado.phet.translationutility;

import java.awt.Cursor;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.Properties;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.event.HyperlinkEvent.EventType;
import javax.swing.text.html.HTMLEditorKit;

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
public class CheckForUpdates {

    private static final int CONNECTION_TIMEOUT = 4000; // milliseconds
    
    private static final String HOST_NAME = "phet.colorado.edu";
    private static final String URL_HOME = "http://phet.colorado.edu/new/contribute/translate.php";
    private static final String URL_LATEST_VERSION_INFO = "http://phet.colorado.edu/new/contribute/translation-utility.properties";

    private static final String CHECKING_DIALOG_MESSAGE = "Checking for updates...";
    
    private static final String UPDATE_DIALOG_TITLE = "Update";
    private static final String UPDATE_DIALOG_MESSAGE = "<html><head><style type=\"text/css\">body { font-size: @FONT_SIZE@; font-family: @FONT_FAMILY@ }</style></head>" + "A newer version of PhET Translation Utility is available." + "<br><br>" + "Please use the latest version to create your translations." + "<br><br>" + "To download the lastest version, click here:" + "<br>" + "<a href=" + URL_HOME + ">" + URL_HOME + "</a>" + "<br><br>" + "</html>";

    /* not intended for instantiation */
    private CheckForUpdates() {}

    /**
     * Reads a properties file on the PhET production server.
     * Compares the SVN revision number property with the SVN revision number of the running program.
     * If they are different, alert the user by opening a dialog. 
     */
    public static void check() {
        
        // Are we connected to the Internet?
        boolean connectedToInternet = false;
        try {
            InetAddress addr = InetAddress.getByName( HOST_NAME );
            SocketAddress sockaddr = new InetSocketAddress( addr, 80 /* port */ );

            // Create an unbound socket
            Socket sock = new Socket();

            // This method will block until timeout occurs.
            // If timeout occurs, SocketTimeoutException is thrown.
            sock.connect( sockaddr, CONNECTION_TIMEOUT );
            connectedToInternet = true;
        }
        catch ( UnknownHostException e ) {
            System.out.println( "CheckForUpdates: unknown host: " + HOST_NAME );
        }
        catch ( SocketTimeoutException e ) {
            System.out.println( "CheckForUpdates: connection timed out" );
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
                URL url = new URL( URL_LATEST_VERSION_INFO );
                InputStream inputStream = url.openStream();
                if ( inputStream != null ) {
                    properties = new Properties();
                    properties.load( inputStream );
                }
            }
            catch ( IOException e ) {
                e.printStackTrace();
            }
            
            checkingDialog.dispose();

            // compare the SVN revision numbers
            if ( properties != null ) {
                String key = "version.revision";
                String thisRevision = TUResources.getProjectProperty( key );
                String remoteRevision = properties.getProperty( key );
                if ( remoteRevision != null && !thisRevision.equals( remoteRevision ) ) {
                    UpdateDialog updateDialog = new UpdateDialog( null );
                    SwingUtils.centerWindowOnScreen( updateDialog );
                    updateDialog.setVisible( true );
                }
            }
        }
    }
    
    private static class CheckingDialog extends JDialog {
        
        public CheckingDialog( Frame owner ) {
            super( owner, false /* modal */ );
            setResizable( false );
            JLabel messageLabel = new JLabel( CHECKING_DIALOG_MESSAGE );
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
            setTitle( UPDATE_DIALOG_TITLE );
            setResizable( false );

            // message with a hyperlink
            JEditorPane messageLabel = new JEditorPane();
            messageLabel.setEditorKit( new HTMLEditorKit() );
            String html = UPDATE_DIALOG_MESSAGE;
            html = html.replaceAll( "@FONT_SIZE@", new PhetFont().getSize() + "pt" );
            html = html.replaceAll( "@FONT_FAMILY@", new PhetFont().getFamily() );
            messageLabel.setText( html );
            messageLabel.setEditable( false );
            messageLabel.setBackground( new JLabel().getBackground() );
            messageLabel.setFont( new PhetFont( Font.BOLD, 24 ) );
            messageLabel.addHyperlinkListener( new HyperlinkListener() {

                public void hyperlinkUpdate( HyperlinkEvent e ) {
                    final EventType type = e.getEventType();
                    if ( type == HyperlinkEvent.EventType.ENTERED ) {
                        setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
                    }
                    else if ( type == HyperlinkEvent.EventType.EXITED ) {
                        setCursor( Cursor.getDefaultCursor() );
                    }
                    else if ( type == HyperlinkEvent.EventType.ACTIVATED ) {
                        PhetServiceManager.showWebPage( e.getURL() );
                        // If the user clicked the link, assume they want to use the new version, so exit this version.
                        System.exit( 0 );
                    }
                }
            } );

            JButton continueButton = new JButton( "Continue" );
            continueButton.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    dispose();
                }
            } );

            JButton quitButton = new JButton( "Quit" );
            quitButton.addActionListener( new ActionListener() {

                public void actionPerformed( ActionEvent e ) {
                    System.exit( 0 );
                }
            } );

            JPanel buttonPanel = new JPanel();
            EasyGridBagLayout buttonPanelLayout = new EasyGridBagLayout( buttonPanel );
            buttonPanel.setLayout( buttonPanelLayout );
            buttonPanelLayout.addComponent( continueButton, 0, 0 );
            buttonPanelLayout.addComponent( quitButton, 0, 1 );

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
