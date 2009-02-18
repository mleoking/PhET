/* Copyright 2009, University of Colorado */

package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Notifies the user about a recommended update to the PhET Installer.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class AutomaticInstallerUpdateDialog extends PaintImmediateDialog {
    
    private static final String TITLE = PhetCommonResources.getString( "Common.updates.updateAvailable" );
    private static final String ASK_ME_LATER_BUTTON = PhetCommonResources.getString( "Common.updates.askMeLater" );
    private static final String MORE_BUTTON = PhetCommonResources.getString( "Common.updates.installer.more" );
    private static final String MESSAGE_PATTERN = PhetCommonResources.getString( "Common.updates.installer.message" );
    private static final String MORE_MESSAGE = PhetCommonResources.getString( "Common.updates.installer.moreMessage" );
    
    public AutomaticInstallerUpdateDialog( Frame owner ) {
        super( owner, TITLE );
        setModal( true );
        setResizable( false );
        
        // subpanels
        JPanel messagePanel = createMessagePanel();
        JPanel buttonPanel = createButtonPanel();
        
        // main panel
        JPanel panel = new JPanel();
        panel.setBorder( new EmptyBorder( 5, 5, 5, 5 ) );
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
    
    private JPanel createMessagePanel() {
        
        Object[] args = { "Oct 3, 2008", "Feb 14, 2008" }; //TODO: get actual dates
        String s = MessageFormat.format( MESSAGE_PATTERN, args );
        JLabel messageLabel = new JLabel( s );
        
        JPanel panel = new JPanel();
        panel.add( messageLabel );
        return panel;
    }
    
    private JPanel createButtonPanel() {
        
        // Yes! button
        JButton updateButton = new InstallerUpdateButton();
        updateButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                // InstallerUpdateButton handles opening the web browser
                dispose();
            }
        });
        
        // Ask Me Later button
        JButton askMeLater = new JButton( ASK_ME_LATER_BUTTON );
        askMeLater.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleAskMeLater();
                dispose();
            }
        } );
        
        // More button
        JButton moreButton = new JButton( MORE_BUTTON );
        moreButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                handleMoreButton();
            }
        } );
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add( updateButton );
        buttonPanel.add( askMeLater );
        buttonPanel.add( moreButton );
        
        return buttonPanel;
    }
    
    private void handleAskMeLater() {
        //XXX write current time to preferences file: installer.updates.ask-me-later-pressed.milliseconds
    }
    
    private void handleMoreButton() {
        JOptionPane.showMessageDialog( this, MORE_MESSAGE , TITLE, JOptionPane.PLAIN_MESSAGE );
    }
    
    /*
     * Test, this edits the real preferences file!
     */
     public static void main( String[] args ) {
         AutomaticInstallerUpdateDialog dialog = new AutomaticInstallerUpdateDialog( null );
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
