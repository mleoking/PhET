// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;

import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingManager;
import edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponents;
import edu.colorado.phet.common.phetcommon.util.FileUtils;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.PhetFrame;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

import static edu.colorado.phet.common.phetcommon.simsharing.messages.ComponentChain.chain;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserActions.pressed;
import static edu.colorado.phet.common.phetcommon.simsharing.messages.UserComponents.*;

/**
 * Menu item and dialog for accessing the sim-sharing event log.
 *
 * @author Sam Reid
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class SimSharingLogMenuItem extends SimSharingJMenuItem {

    private static final String TITLE = "Data Collection Log"; // give it a less scary name
    private static final String ACTION = "Save";
    private static final String FILE_SUFFIX = ".txt";

    private SimSharingLogDialog dialog;

    public SimSharingLogMenuItem( final PhetFrame parent ) {
        super( UserComponents.dataCollectionLogMenuItem, TITLE + "..." );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                if ( dialog == null ) {
                    dialog = new SimSharingLogDialog( parent );
                }
                dialog.setVisible( true );
            }
        } );
    }

    // Dialog that displays the event log, and allows it to be saved to a file.
    public static class SimSharingLogDialog extends JDialog {

        private File currentDirectory = null;

        public SimSharingLogDialog( final JFrame parent ) {
            super( parent, TITLE );
            setContentPane( new JPanel( new BorderLayout() ) {{
                // scrolling pane that shows the log
                add( new JScrollPane( new JTextArea( 20, 40 ) {{
                    setEditable( false );
                    //TODO the scrollpane has scrolling issues when it's created, and as it dynamically updates. Horizontal scrollbar should start full left, and stay where it's put.
                    SimSharingManager.getInstance().log.addObserver( new VoidFunction1<String>() {
                        public void apply( String s ) {
                            setText( s );
                            scrollRectToVisible( new Rectangle( 0, getHeight() - 1, 1, 1 ) );
                        }
                    } );
                }} ), BorderLayout.CENTER );
                // Save button that saves the log to a file
                add( new JPanel() {{
                    add( new SimSharingJButton( chain( simSharingLogFileDialog, saveButton ), ACTION + "..." ) {{
                        addActionListener( new ActionListener() {
                            public void actionPerformed( ActionEvent e ) {

                                // Choose the file
                                JFileChooser fileChooser = new JFileChooser( currentDirectory );
                                fileChooser.setDialogTitle( TITLE + " : " + ACTION );
                                int rval = fileChooser.showSaveDialog( parent ); // blocks
                                File selectedFile = fileChooser.getSelectedFile();
                                if ( rval == JFileChooser.CANCEL_OPTION || selectedFile == null ) {
                                    SimSharingManager.sendUserEvent( chain( simSharingLogFileDialog, fileChooserCancelButton ), pressed );
                                    return;
                                }
                                currentDirectory = selectedFile.getParentFile();
                                SimSharingManager.sendUserEvent( chain( simSharingLogFileDialog, fileChooserSaveButton ), pressed );

                                // Ensure that the file has the proper suffix.
                                if ( !FileUtils.hasSuffix( selectedFile, FILE_SUFFIX ) ) {
                                    selectedFile = new File( selectedFile.getParent(), selectedFile.getName() + FILE_SUFFIX );
                                }

                                // If the file exists, confirm overwrite.
                                if ( selectedFile.exists() ) {
                                    String message = MessageFormat.format( "File {0} exists. OK to replace?", selectedFile.getName() );
                                    int reply = PhetOptionPane.showYesNoDialog( parent, message, "Confirm" );
                                    if ( reply != JOptionPane.YES_OPTION ) {
                                        SimSharingManager.sendUserEvent( chain( simSharingLogFileDialog, replaceFileNoButton ), pressed );
                                        return;
                                    }
                                    SimSharingManager.sendUserEvent( chain( simSharingLogFileDialog, replaceFileYesButton ), pressed );
                                }

                                // Write log to file.
                                try {
                                    BufferedWriter writer = new BufferedWriter( new FileWriter( selectedFile ) );
                                    writer.write( SimSharingManager.getInstance().log.get() );
                                    writer.close();
                                }
                                catch ( IOException ioe ) {
                                    ioe.printStackTrace();
                                }
                            }
                        } );
                    }} );
                }}, BorderLayout.SOUTH );
            }} );
            pack();
            SwingUtils.centerInParent( this );
        }
    }
}