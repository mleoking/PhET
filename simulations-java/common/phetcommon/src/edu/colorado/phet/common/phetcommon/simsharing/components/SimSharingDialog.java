// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import edu.colorado.phet.common.phetcommon.simsharing.SimSharingEvents;
import edu.colorado.phet.common.phetcommon.util.function.VoidFunction1;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Dialog that shows user information and allows the user to send an ID message back to the server.
 *
 * @author Sam Reid
 */
public class SimSharingDialog extends JDialog {
    public SimSharingDialog( JFrame parent ) {
        super( parent, "Sim sharing event log" );
        setContentPane( new JPanel( new BorderLayout() ) {{
            add( new JScrollPane( new JTextArea( 20, 40 ) {{
                setEditable( false );
                SimSharingEvents.log.addObserver( new VoidFunction1<String>() {
                    public void apply( String s ) {
                        setText( s );
                        scrollRectToVisible( new Rectangle( 0, getHeight() - 1, 1, 1 ) );
                    }
                } );
            }} ), BorderLayout.CENTER );
            add( new JPanel() {{
                add( new JButton( "Copy to clipboard" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                            final StringSelection contents = new StringSelection( SimSharingEvents.log.get() );
                            clipboard.setContents( contents, contents );
                        }
                    } );
                }} );
            }}, BorderLayout.SOUTH );
        }} );
        pack();
        SwingUtils.centerInParent( this );
    }
}