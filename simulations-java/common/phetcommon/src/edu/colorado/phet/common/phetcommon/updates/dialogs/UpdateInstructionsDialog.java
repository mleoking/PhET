package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.updates.OpenWebPageToNewVersion;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils.InteractiveHTMLPane;

/**
 * Dialog that provides instructions on how to update.
 * Uses in both the manual and automatic update user-interaction sequences.
 * Pressing OK opens a web browser to the sim's web page on the PhET site.
 * Pressing Cancel cancels the update.
 */
public abstract class UpdateInstructionsDialog extends AbstractUpdateDialog {
    
    private static final String TITLE = PhetCommonResources.getString( "Common.updates.howToUpdate" );
    private static final String OK_BUTTON = PhetCommonResources.getString( "Common.choice.ok" );
    private static final String CANCEL_BUTTON = PhetCommonResources.getString( "Common.choice.cancel" );
    
    /**
     * Update instructions that accompany an automatic update.
     */
    public static class AutomaticUpdateInstructionsDialog extends UpdateInstructionsDialog {
        public AutomaticUpdateInstructionsDialog( Frame owner, String project, String sim, String simName, String currentVersion, String newVersion ) {
            super( owner, project, sim, getAutomaticUpdateInstructionsHTML( simName, currentVersion, newVersion ) );
        }
    }
  
    /**
     * Update instructions that accompany a manual update.
     */
    public static class ManualUpdateInstructionsDialog extends UpdateInstructionsDialog {
        public ManualUpdateInstructionsDialog( Frame owner, String project, String sim, String simName, String currentVersion, String newVersion ) {
            super( owner, project, sim, getManualUpdateInstructionsHTML( project, sim, simName, currentVersion, newVersion ) );
        }
    }
    
    protected UpdateInstructionsDialog( Frame owner, final String project, final String sim, String htmlInstructions ) {
        super( owner, TITLE );
        setResizable( false );
        setModal( true  );
        
        // instructions on how to update
        JComponent htmlPane = new InteractiveHTMLPane( htmlInstructions );
        JPanel messagePanel = new JPanel();
        messagePanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        messagePanel.add( htmlPane );
        
        // opens a web browser to the sim's webpage
        JButton okButton = new JButton( OK_BUTTON );
        okButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
                OpenWebPageToNewVersion.openWebPageToNewVersion( project, sim );
            }
        } );
        
        // cancels the update
        JButton cancelButton = new JButton( CANCEL_BUTTON );
        cancelButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        } );
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add( okButton );
        buttonPanel.add( cancelButton );
        
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
        center();
    }
}
