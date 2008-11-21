/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.Frame;
import java.awt.Insets;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.util.Downloader.DownloaderListener;
import edu.colorado.phet.common.phetcommon.view.MaxCharsLabel;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetFont;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Dialog that shows update progress. 
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class UpdateProgressDialog extends JDialog implements DownloaderListener {
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    private static final int STATUS_MAX_CHARS = 50;
    private static final int STATUS_END_CHARS = 35;  // ellipsis will be this many chars from end of status message
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final JProgressBar progressBar;
    private final MaxCharsLabel statusLabel;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    public UpdateProgressDialog( Frame owner, String message ) {
        super( owner, PhetCommonResources.getString( "Common.updates.progressDialogTitle" ) );
        setModal( true );
        setResizable( false );
        
        // message
        JLabel messageLabel = new JLabel( message );
        
        // progress bar
        progressBar = new JProgressBar( 0, 100 );
        
        // status message
        statusLabel = new MaxCharsLabel( STATUS_MAX_CHARS, STATUS_END_CHARS );
        statusLabel.setFont( new PhetFont( 10 ) );
        
        //TODO: needs a Cancel button

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
        
        getContentPane().add( panel );
        pack();
        SwingUtils.centerDialog( this, owner );
    }
    
    //----------------------------------------------------------------------------
    // DownloaderListener implementation
    //----------------------------------------------------------------------------
    
    public void progress( String sourceURL, File destinationFile, double percentOfSource, double percentOfTotal ) {
        statusLabel.setText( sourceURL );
        progressBar.setValue( (int)( percentOfTotal * ( progressBar.getMaximum() - progressBar.getMinimum() ) ) );
    }
    
    public void completed( String sourceURL, File destinationFile ) {}

    public void error( String sourceURL, File destinationFile, String message, Exception e ) {
        dispose();
        //TODO: should the user be notified about this error here or elsewhere?
    }
    
    //----------------------------------------------------------------------------
    // Test
    //----------------------------------------------------------------------------

    public static void main( String[] args ) {
        //TODO: this test is incomplete, needs to include instance of Downloader and proper threading
        UpdateProgressDialog dialog = new UpdateProgressDialog( null, "updating \"Glaciers\" 4.00" );
        dialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
            public void windowClosed( WindowEvent e ) {
                System.exit( 0 );
            }
        });
        dialog.setVisible( true );
    }
}
