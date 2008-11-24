/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.DownloadThread;
import edu.colorado.phet.common.phetcommon.util.DownloadThread.DebugDownloadThreadListener;
import edu.colorado.phet.common.phetcommon.util.DownloadThread.DownloadThreadListener;
import edu.colorado.phet.common.phetcommon.view.MaxCharsLabel;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Dialog that shows progress of a batch download. 
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class DownloadProgressDialog extends JDialog {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final String DOWNLOADING = PhetCommonResources.getString( "Common.DownloadProgressDialog.downloading" );
    
    private static final int STATUS_MAX_CHARS = 50;
    private static final int STATUS_END_CHARS = 35;  // ellipsis will be this many chars from end of status message
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final DownloadThread downloader;
    private final DownloadThreadListener downloaderListener;
    private final JProgressBar progressBar;
    private final MaxCharsLabel statusLabel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public DownloadProgressDialog( Frame owner, String title, String message, DownloadThread downloader ) {
        super( owner, title );
        setModal( true );
//        setResizable( false );
        
        this.downloader = downloader;
        this.downloaderListener = new ThisDownloadListener();
        this.downloader.addListener( downloaderListener );
        
        // message
        JLabel messageLabel = new JLabel( message );
        
        // progress bar
        progressBar = new JProgressBar( 0, 100 );
        
        // status message
        statusLabel = new MaxCharsLabel( STATUS_MAX_CHARS, STATUS_END_CHARS );
        statusLabel.setFont( new PhetFont( 10 ) );
        
        // Cancel button
        JButton cancelButton = new JButton( PhetCommonResources.getString( "Common.choice.cancel" ) );
        cancelButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                doCancel();
            }
        });
        
        // Close button in window dress acts like Cancel button
        addWindowListener( new WindowAdapter() { 
            public void windowClosing(WindowEvent e) {
                doCancel();
            }
        } );

        // size the progress bar to be about the same size as the longer of the message or the longest status message
        String s = "";
        for ( int i = 0; i < STATUS_MAX_CHARS; i++ ) {
            s += 'M'; // font is probably proportional, so use one of the widest chars 
        }
        statusLabel.setText( s );
        int progressBarWidth = (int)Math.max( messageLabel.getPreferredSize().getWidth(), statusLabel.getPreferredSize().getWidth() ) + 20 /* fudge factor */;
        SwingUtils.setPreferredWidth( progressBar, progressBarWidth );
        statusLabel.setText( "" );
        
        // layout
        JPanel panel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        layout.setInsets( new Insets( 10, 10, 10, 10 ) );
        int row = 0;
        int column = 0;
        layout.addComponent( messageLabel, row++, column );
        layout.addComponent( progressBar, row++, column );
        layout.addComponent( statusLabel, row++, column );
        layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        layout.addAnchoredComponent( cancelButton, row, column, GridBagConstraints.CENTER );
        
        getContentPane().add( panel );
        pack();
        SwingUtils.centerDialog( this, owner );
    }
    
    private void doCancel() {
        downloader.cancel();
    }
    
    //----------------------------------------------------------------------------
    // Superclass overrides
    //----------------------------------------------------------------------------
    
    public void dispose() {
        downloader.removeListeners( downloaderListener );
        super.dispose();
    }
    
    //----------------------------------------------------------------------------
    // DownloaderListener implementation
    //----------------------------------------------------------------------------
    
    private class ThisDownloadListener implements DownloadThreadListener {
        
        public void succeeded() {
            dispose();
        }
        
        public void failed() {
            // do nothing, specific error reported by error()
        }
        
        public void canceled() {
            dispose();
        }
        
        public void requestAdded( String sourceURL, File destinationFile ) {
            // don't care about request additions
        }
        
        public void progress( String sourceURL, File destinationFile, double percentOfSource, double percentOfTotal ) {
            statusLabel.setText( DOWNLOADING + " " + sourceURL );
            progressBar.setValue( (int) ( percentOfTotal * ( progressBar.getMaximum() - progressBar.getMinimum() ) ) );
        }

        public void completed( String sourceURL, File destinationFile ) {
            // do nothing
        }

        public void error( String sourceURL, File destinationFile, String message, Exception e ) {
            JOptionPane.showMessageDialog( DownloadProgressDialog.this, message, "Update Error", JOptionPane.ERROR_MESSAGE ); //TODO: notify the user about this error
            dispose();
        }
    }
    
    //----------------------------------------------------------------------------
    // Test
    //----------------------------------------------------------------------------

    public static void main( String[] args ) throws IOException {
        
        // create download thread
        DownloadThread downloadThread = new DownloadThread();
        
        // add a listener
        downloadThread.addListener( new DebugDownloadThreadListener() );
        
        // add download requests
        String tmpDirName = System.getProperty( "java.io.tmpdir" ) + System.getProperty( "file.separator" );
        downloadThread.addRequest( HTMLUtils.getSimJarURL( "glaciers", "glaciers", "&", "en" ), tmpDirName + "glaciers.jar" );
        downloadThread.addRequest( HTMLUtils.getSimJarURL( "ph-scale", "ph-scale", "&", "en" ), tmpDirName + "ph-scale.jar" );

        // progress dialog
        DownloadProgressDialog dialog = new DownloadProgressDialog( null, "Download Progress", "updating \"Glaciers\" 4.00", downloadThread );
        dialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
            public void windowClosed( WindowEvent e ) {
                System.exit( 0 );
            }
        });
        
        // start the download
        downloadThread.start();
        dialog.setVisible( true );
    }
}
