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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingEvents;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Dialog that displays the event log, and allows it to be saved.
 *
 * @author Sam Reid
 */
public class SimSharingLogDialog extends JDialog {

    public SimSharingLogDialog( final JFrame parent ) {
        super( parent, "Data Collection Log" );
        setContentPane( new JPanel( new BorderLayout() ) {{
            add( new JScrollPane( new JTextArea( 20, 40 ) {{
                setEditable( false );
                SimSharingEvents.log.addObserver( new VoidFunction1<String>() {
                    public void apply( String s ) {
                        setText( s );
                        scrollRectToVisible( new Rectangle( 0, getHeight() - 1, 1, 1 ) );
                    }
                } );
            }} ) {{
                setHorizontalScrollBarPolicy( JScrollPane.HORIZONTAL_SCROLLBAR_NEVER );
                setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
            }}, BorderLayout.CENTER );
            add( new JPanel() {{
                add( new JButton( "Save to file..." ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {

                            // Choose the file
                            JFileChooser fileChooser = new JFileChooser();
                            fileChooser.setDialogTitle( "Save" );
                            int rval = fileChooser.showSaveDialog( parent );
                            File selectedFile = fileChooser.getSelectedFile();
                            if ( rval == JFileChooser.CANCEL_OPTION || selectedFile == null ) {
                                return;
                            }

                            // If the file exists, confirm overwrite.
                            if ( selectedFile.exists() ) {
                                int reply = PhetOptionPane.showYesNoDialog( parent, "File exists. OK to replace?", "Confirm" );
                                if ( reply != JOptionPane.YES_OPTION ) {
                                    return;
                                }
                            }

                            // Write log to file.
                            try {
                                BufferedWriter writer = new BufferedWriter( new FileWriter( selectedFile ) );
                                writer.write( SimSharingEvents.log.get() );
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