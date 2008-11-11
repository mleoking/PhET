package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.MessageFormat;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils.InteractiveHTMLPane;

/**
 * Error dialog, with a summary and "Details" button to get more info.
 * The "Details" button opens a Details dialog.
 */
public class UpdateErrorDialog extends JDialog {
    
    private static final String TITLE = PhetCommonResources.getString( "Common.updates.error.title" );
    private static final String OK_BUTTON = PhetCommonResources.getString( "Common.choice.ok" );
    private static final String DETAILS_BUTTON = PhetCommonResources.getString( "Common.updates.error.detailsButton" );
    private static final String DETAILS_TITLE = PhetCommonResources.getString( "Common.updates.error.detailsTitle" );

    public UpdateErrorDialog( Frame owner, final Exception exception ) {
        super( owner, TITLE );
        setModal( true );
        setResizable( false );
        
        // notification that an error occurred
        String html = getErrorMessageHTML();
        JComponent htmlPane = new InteractiveHTMLPane( html );
        JPanel messagePanel = new JPanel();
        messagePanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        messagePanel.add( htmlPane );
        
        // closes the dialog
        JButton okButton = new JButton( OK_BUTTON );
        okButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        } );
        
        // shows the stack trace
        //TODO: make it easy to copy-&-paste this, for emailing to phethelp
        final JButton detailsButton = new JButton( DETAILS_BUTTON );
        detailsButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent event ) {
                final StringWriter result = new StringWriter();
                final PrintWriter printWriter = new PrintWriter( result );
                exception.printStackTrace( printWriter );
                JOptionPane.showMessageDialog( detailsButton, result.getBuffer(), DETAILS_TITLE, JOptionPane.INFORMATION_MESSAGE );
            }
        } );
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add( okButton );
        buttonPanel.add( detailsButton );
        
        // main panel layout
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
    
    protected static String getErrorMessageHTML() {
        Object[] args = { HTMLUtils.getPhetHomeHref(), HTMLUtils.getPhetMailtoHref() };
        String htmlFragment = MessageFormat.format( PhetCommonResources.getString( "Common.updates.errorMessage" ), args );
        return HTMLUtils.createStyledHTMLFromFragment( htmlFragment );
    }
    
    public static void main( String[] args ) {
        // dialog must have an owner if you want cursor to change over hyperlinks
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
        SwingUtils.centerWindowOnScreen( frame );
        frame.setVisible( true );
        JDialog dialog = new UpdateErrorDialog( frame, new IOException() );
        dialog.addWindowListener( new WindowAdapter() {
            public void windowClosed( WindowEvent e ) {
                System.exit( 0 );
            }
        } );
        dialog.setVisible( true );
    }
}
