package edu.colorado.phet.common.phetcommon.view.util;

import java.awt.BorderLayout;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

/**
 * A dialog similar to JOptionPane.showMessageDialog with type=JOptionPane.PLAIN_MESSAGE.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PlainMessageDialog extends PaintImmediateDialog {
    
    private static final String CLOSE_BUTTON = PhetCommonResources.getString( "Common.choice.close" );

    public PlainMessageDialog( Frame owner, String title, String message ) {
        this( owner, title, new JLabel( message ) );
    }
    
    public PlainMessageDialog( Dialog owner, String title, String message ) {
        this( owner, title, new JLabel( message ) );
    }
    
    public PlainMessageDialog( Frame owner, String title, JComponent message ) {
        super( owner, title );
        init( message );
    }
    
    public PlainMessageDialog( Dialog owner, String title, JComponent message ) {
        super( owner, title );
        init( message );
    }
    
    private void init( JComponent message ) {
        JPanel panel = new JPanel( new BorderLayout() );
        panel.add( createMessagePanel( message ), BorderLayout.CENTER );
        panel.add( createButtonPanel(), BorderLayout.SOUTH );
        setContentPane( panel );
        pack();
        SwingUtils.centerDialogInParent( this );
    }
    
    private JPanel createMessagePanel( JComponent message ) {
        JPanel messagePanel = new JPanel();
        messagePanel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
        messagePanel.add( message );
        return messagePanel;
    }
    
    private JPanel createButtonPanel() {
        
        // Close button
        JButton closeButton = new JButton( CLOSE_BUTTON );
        closeButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add( closeButton );
        
        return buttonPanel;
    }
    
    public static void main( String[] args ) {
        JDialog dialog = new PlainMessageDialog( (Frame)null, "my title", "my message" );
        dialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
            public void windowClosed( WindowEvent e ) {
                System.exit( 0 );
            }
        } );
        SwingUtils.centerWindowOnScreen( dialog );
        dialog.setVisible( true );
    }
}
