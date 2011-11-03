// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.common.phetcommon.simsharing.components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import edu.colorado.phet.common.phetcommon.simsharing.Parameter;
import edu.colorado.phet.common.phetcommon.simsharing.SimSharingEvents;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Dialog that shows user information and allows the user to send an ID message back to the server.
 *
 * @author Sam Reid
 */
public class SimSharingDialog extends JDialog {
    public SimSharingDialog( JFrame parent ) {
        super( parent, "About Sim Sharing" );
        setContentPane( new VerticalLayoutPanel() {{

            //Show the machine cookie and session id, call it a cookie so it is not intimidating
            add( new JTextArea( "cookie\n" + SimSharingEvents.MACHINE_COOKIE + "\n\n" +
                                "session id\n" + SimSharingEvents.SESSION_ID ) {{
                setEditable( false );
            }} );

            //Add a place where the user can submit their own custom ID
            add( new JPanel() {{
                add( new JLabel( "id" ) );
                final JTextField textField = new JTextField( 10 ) {{
                    final JTextField t = this;
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            submitText( t );
                        }
                    } );
                }};
                add( textField );
                add( new JButton( "Submit" ) {{
                    addActionListener( new ActionListener() {
                        public void actionPerformed( ActionEvent e ) {
                            submitText( textField );
                        }
                    } );
                }} );
            }} );
        }} );
        pack();
        SwingUtils.centerInParent( this );
    }

    //Send the ID to the server
    private void submitText( JTextField textField ) {
        SimSharingEvents.sendEvent( "id", "submitted", Parameter.param( "id", textField.getText() ) );
    }
}