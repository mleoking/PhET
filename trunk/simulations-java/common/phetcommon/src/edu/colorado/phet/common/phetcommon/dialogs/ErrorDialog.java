/* Copyright 2008, University of Colorado */

package edu.colorado.phet.common.phetcommon.dialogs;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.text.MessageFormat;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils.InteractiveHTMLPane;

/**
 * ErrorDialog is a general-purpose error dialog.
 * If an Exception is specified, a Details button is visible that lets you see the stack trace.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class ErrorDialog extends PaintImmediateDialog {

    private static final String CLOSE_BUTTON = PhetCommonResources.getString( "Common.choice.close" );
    private static final String DETAILS_BUTTON = PhetCommonResources.getString( "Common.ErrorDialog.detailsButton" );
    private static final String DETAILS_TITLE = PhetCommonResources.getString( "Common.ErrorDialog.detailsTitle" );
    private static final String CONTACT_PHET = PhetCommonResources.getString( "Common.ErrorDialog.contactPhet" );
    
    public ErrorDialog( Frame owner, String title, String htmlMessage ) {
        this( owner, title, htmlMessage, null /* exception */ );
    }
    
    public ErrorDialog( Frame owner, String title, String htmlMessage, final Exception exception ) {
        super( owner, title );
        init( htmlMessage, exception );
    }
    
    public ErrorDialog( JDialog owner, String title, String htmlMessage ) {
        this( owner, title, htmlMessage, null /* exception */ );
    }
    
    public ErrorDialog( JDialog owner, String title, String htmlMessage, final Exception exception ) {
        super( owner, title );
        init( htmlMessage, exception );
    }
    
    private void init( String htmlMessage, final Exception exception ) {
        setResizable( false );
        setModal( true );

        JComponent htmlPane = new InteractiveHTMLPane( htmlMessage );
        JPanel messagePanel = new JPanel();
        messagePanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        messagePanel.add( htmlPane );

        JPanel buttonPanel = new JPanel();
        
        // closes the dialog
        JButton closeButton = new JButton( CLOSE_BUTTON );
        closeButton.addActionListener( new ActionListener() {

            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        } );
        buttonPanel.add( closeButton );

        // Details button
        if ( exception != null ) {
            final JButton detailsButton = new JButton( DETAILS_BUTTON );
            detailsButton.addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent event ) {
                    // show a stack trace
                    String htmlMessage = getContactPhetMessageHTML();
                    JDialog dialog = new StackTraceDialog( ErrorDialog.this, DETAILS_TITLE, htmlMessage, exception );
                    SwingUtils.centerDialogInParent( dialog );
                    dialog.setModal( ErrorDialog.this.isModal() ); // use same modality for details
                    dialog.setVisible( true );
                }
            } );
            buttonPanel.add( detailsButton );
        }

        // layout
        JPanel panel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( messagePanel, row++, column );
        layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        layout.addAnchoredComponent( buttonPanel, row++, column, GridBagConstraints.CENTER );

        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }
    
    private static String getContactPhetMessageHTML() {
        Object[] args = { HTMLUtils.getPhetMailtoHref() };
        String htmlFragment = MessageFormat.format( CONTACT_PHET, args );
        return HTMLUtils.createStyledHTMLFromFragment( htmlFragment );
    }
    
    // test
    public static void main( String[] args ) {
        // dialog must have an owner if you want cursor to change over hyperlinks
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
        String htmlMessage = HTMLUtils.createStyledHTMLFromFragment( "Something very bad just happened." );
        JDialog dialog = new ErrorDialog( frame, "Run For Your Life!", htmlMessage, new IOException() );
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
