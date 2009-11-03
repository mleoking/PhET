/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.dialogs;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.StringUtil;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils.InteractiveHTMLPane;

/**
 * StackTraceDialog is a dialog that displays a stack trace.
 * A JTextField is used so that it's easy for the user to copy-&-paste.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class StackTraceDialog extends PaintImmediateDialog {
    
    private static final String CLOSE_BUTTON = PhetCommonResources.getString( "Common.choice.close" );
    
    public StackTraceDialog( JDialog owner, String title, String htmlMessage, Exception e ) {
        super( owner, title );
        init( htmlMessage, e );
    }
    
    public StackTraceDialog( Frame owner, String title, String htmlMessage, Exception e ) {
        super( owner, title );
        init( htmlMessage, e );
    }
    
    private void init( String htmlMessage, Exception e ) {
        setModal( false );
        setResizable( true );
        
        JPanel panel = new JPanel( new BorderLayout() );
        
        JComponent htmlPane = new InteractiveHTMLPane( htmlMessage );
        htmlPane.setBackground( panel.getBackground() );
        htmlPane.setBorder(  BorderFactory.createEmptyBorder( 0, 0, 10, 0 ) );
        
        String stackTrace = StringUtil.stackTraceToString( e );
        JTextArea textArea = new JTextArea( stackTrace );
        textArea.setEditable( false );
        JScrollPane scrollPane = new JScrollPane( textArea );
        
        JButton closeButton = new JButton( CLOSE_BUTTON );
        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        } );
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add( closeButton );
        
        panel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 0, 10 ) );
        panel.add( htmlPane, BorderLayout.NORTH );
        panel.add( scrollPane, BorderLayout.CENTER );
        panel.add( buttonPanel, BorderLayout.SOUTH );
        
        setContentPane( panel );
        setSize( 600, 250 ); // default size
    }
    
    // test
    public static void main( String[] args ) {
        String htmlMessage = HTMLUtils.createStyledHTMLFromFragment( "Something very bad just happened." );
        JDialog dialog = new StackTraceDialog( (Frame)null, "Stack Trace", htmlMessage, new IOException() );
        dialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
            public void windowClosed( WindowEvent e ) {
                System.exit( 0 );
            }
        } );
        dialog.setVisible( true );
    }
}