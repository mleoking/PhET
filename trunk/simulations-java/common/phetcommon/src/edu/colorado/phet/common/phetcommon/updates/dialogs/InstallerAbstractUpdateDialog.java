/* Copyright 2009, University of Colorado */

package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.MessageFormat;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import edu.colorado.phet.common.phetcommon.PhetCommonConstants;
import edu.colorado.phet.common.phetcommon.application.PaintImmediateDialog;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.servicemanager.PhetServiceManager;
import edu.colorado.phet.common.phetcommon.updates.IAskMeLaterStrategy;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.PhetOptionPane;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

/**
 * Base class for installer update dialogs.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public abstract class InstallerAbstractUpdateDialog extends PaintImmediateDialog {
    
    private static final String TITLE = PhetCommonResources.getString( "Common.updates.updateAvailable" );
    private static final String UPDATE_BUTTON = PhetCommonResources.getString( "Common.updates.installer.yes" );
    private static final String ASK_ME_LATER_BUTTON = PhetCommonResources.getString( "Common.updates.askMeLater" );
    private static final String MORE_BUTTON = PhetCommonResources.getString( "Common.updates.installer.more" );
    private static final String NO_BUTTON = PhetCommonResources.getString( "Common.choice.no" );
    private static final String MESSAGE_PATTERN = PhetCommonResources.getString( "Common.updates.installer.message" );
    private static final String MORE_MESSAGE = PhetCommonResources.getString( "Common.updates.installer.moreMessage" );
    
    public InstallerAbstractUpdateDialog( Frame owner ) {
        super( owner, TITLE );
        setModal( true );
        setResizable( false );
    }
    
    /*
     * Subclass must call this at the end of their constructor,
     * so that the GUI is initialized *after* member data is initialized. 
     */
    protected void initGUI() {
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
        
        Object[] args = { "Oct 3, 2008", "Feb 14, 2009" }; //TODO: get actual dates
        String s = MessageFormat.format( MESSAGE_PATTERN, args );
        JLabel messageLabel = new JLabel( s );
        
        JPanel panel = new JPanel();
        panel.add( messageLabel );
        return panel;
    }
    
    protected abstract JPanel createButtonPanel();
    
    protected static class UpdateButton extends JButton {
        public UpdateButton( final JDialog dialog ) {
            super( UPDATE_BUTTON );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    PhetServiceManager.showWebPage( PhetCommonConstants.PHET_INSTALLER_URL );
                    dialog.dispose();
                }
            } );
        }
    }
    
    protected static class AskMeLaterButton extends JButton {
        public AskMeLaterButton( final JDialog dialog, final IAskMeLaterStrategy askMeLaterStrategy ) {
            super( ASK_ME_LATER_BUTTON );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    askMeLaterStrategy.setStartTime( System.currentTimeMillis() );
                    dialog.dispose();
                }
            } );
        }
    }
    
    protected static class NoButton extends JButton {
        public NoButton( final JDialog dialog ) {
            super( NO_BUTTON );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    dialog.dispose();
                }
            } );
        }
    }
    
    protected static class MoreButton extends JButton {
        public MoreButton( final JDialog parent ) {
            super( MORE_BUTTON );
            addActionListener( new ActionListener() {
                public void actionPerformed( ActionEvent e ) {
                    PhetOptionPane.showMessageDialog( parent, TITLE, MORE_MESSAGE );
                }
            } );
        }
    }
}
