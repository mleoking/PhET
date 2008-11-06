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

import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.preferences.*;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.tracking.TrackingManager;
import edu.colorado.phet.common.phetcommon.tracking.TrackingMessage;
import edu.colorado.phet.common.phetcommon.updates.*;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils;

/**
 * The dialog that used to automatically notify the user that an update is available.
 */
public class AutomaticUpdateDialog extends AbstractUpdateDialog {

    private static final String TITLE = PhetCommonResources.getString( "Common.updates.updateAvailable" );
    private static final String ASK_ME_LATER_BUTTON = PhetCommonResources.getString( "Common.updates.askMeLater" );
    private static final String SKIP_UPDATE_BUTTON = PhetCommonResources.getString( "Common.updates.skipThisUpdate" );
    private static final String ADVANCED_LINK = PhetCommonResources.getString( "Common.updates.advanced" );
    private static final String TRY_IT_LINK = PhetCommonResources.getString( "Common.updates.tryIt" );

    public AutomaticUpdateDialog( Frame frame,ISimInfo info, ITrackingInfo trackingInfo, PhetVersion newVersion ) {
        this( frame,
              info.getProjectName(),
              info.getFlavor(),
              info.getName(),
              info.getVersion(),
              newVersion,
              trackingInfo, 
              new ApplicationConfigManualCheckForUpdates( frame, info ),
              info, 
              new DefaultUpdateTimer(),
              new DefaultVersionSkipper() );
    }

    private AutomaticUpdateDialog( final Frame owner,
                                   final String project, final String sim, final String simName,
                                   final PhetVersion currentVersion, final PhetVersion newVersion,
                                   final ITrackingInfo trackingInfo, final IManualUpdateChecker iManuallyCheckForUpdates,
                                   final ISimInfo config,
                                   final IUpdateTimer updateTimer, final IVersionSkipper versionSkipper ) {
        super( owner, TITLE );
        setResizable( false );
        setModal( true );

        // information about the update that was found
        JLabel versionComparisonLabel = new JLabel( getVersionComparisonHTML( simName, currentVersion.formatForTitleBar(), newVersion.formatForTitleBar() ) );

        // does the update
        JButton updateNowButton = new UpdateButton( project, sim );

        // ignores this update until a later time
        JButton askMeLater = new JButton( ASK_ME_LATER_BUTTON );
        askMeLater.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateTimer.setLastAskMeLaterTime( project, sim, System.currentTimeMillis() );
                dispose();
                TrackingManager.postActionPerformedMessage( TrackingMessage.ASK_ME_LATER_PRESSED );
            }
        } );

        // ignores this version altogether
        JButton skipThisVersion = new JButton( SKIP_UPDATE_BUTTON );
        skipThisVersion.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                versionSkipper.skipThisVersion( config.getProjectName(), config.getFlavor(), newVersion );
                dispose();
                TrackingManager.postActionPerformedMessage( TrackingMessage.SKIP_UPDATE_PRESSED );
            }
        } );
        
        // Advanced link, opens the Preferences dialog
        String advancedHTML = "<html><font size=\"3\"><u>" + ADVANCED_LINK + "</u></font></html>";
        JLabel advancedLink = new JLabel( advancedHTML );
        advancedLink.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        advancedLink.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                TrackingManager.postActionPerformedMessage( TrackingMessage.UPDATES_ADVANCED_PRESSED );
                dispose();
                new PreferencesDialog( owner, trackingInfo, iManuallyCheckForUpdates, new DefaultUpdatePreferences(), new DefaultTrackingPreferences() ).setVisible( true );
            }
        } );
        advancedLink.setForeground( Color.blue );
        
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
        
        JPanel messagePanel = new VerticalLayoutPanel();
        messagePanel.setBorder( BorderFactory.createEmptyBorder( 10, 10, 5, 10 ) );
        messagePanel.add( versionComparisonLabel );
        messagePanel.add( Box.createVerticalStrut( 5 ) );
        messagePanel.add( tryItLink );
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add( updateNowButton );
        buttonPanel.add( askMeLater );
        buttonPanel.add( skipThisVersion );
        buttonPanel.add( advancedLink );
        
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
