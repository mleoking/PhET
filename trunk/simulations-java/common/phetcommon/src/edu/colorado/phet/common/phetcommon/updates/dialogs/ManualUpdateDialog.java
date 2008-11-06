package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.tracking.TrackingManager;
import edu.colorado.phet.common.phetcommon.tracking.TrackingMessage;
import edu.colorado.phet.common.phetcommon.updates.OpenWebPageToNewVersion;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;


public class ManualUpdateDialog extends AbstractUpdateDialog {
    
    private static final String TITLE = PhetCommonResources.getString( "Common.updates.updateAvailable" );
    private static final String CANCEL_BUTTON = PhetCommonResources.getString( "Common.choice.cancel" );
    private static final String TRY_IT_LINK = PhetCommonResources.getString( "Common.updates.tryIt" );
    
    public ManualUpdateDialog( Frame owner, final String project, final String sim, final String simName, final PhetVersion currentVersion, final PhetVersion newVersion ) {
        super( owner, TITLE );
        setResizable( false );
        setModal( true );
        
        // information about the update that was found
        JLabel versionComparisonLabel = new JLabel( getVersionComparisonHTML( simName, currentVersion.formatForTitleBar(), newVersion.formatForTitleBar() ) );
        
        // link to sim's webpage
        String tryItHtml = "<html><u>" + TRY_IT_LINK + "</u></html>";
        JLabel tryItLink = new JLabel( tryItHtml );
        tryItLink.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        tryItLink.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                TrackingManager.postActionPerformedMessage( TrackingMessage.UPDATES_TRY_IT_PRESSED );
                OpenWebPageToNewVersion.openWebPageToNewVersion( project, sim );
            }
        } );
        tryItLink.setForeground( Color.blue );
        
        // update button
        JButton updateButton = new UpdateButton( project, sim );
        
        // cancel button
        JButton cancelButton = new JButton( CANCEL_BUTTON );
        cancelButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                TrackingManager.postActionPerformedMessage( TrackingMessage.UPDATES_CANCEL_PRESSED );
                dispose();
            }
        } );
        
        JPanel messagePanel = new VerticalLayoutPanel();
        messagePanel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 5, 10 ) );
        messagePanel.add( versionComparisonLabel );
        messagePanel.add( Box.createVerticalStrut( 5 ) );
        messagePanel.add( tryItLink );
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add( updateButton );
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
