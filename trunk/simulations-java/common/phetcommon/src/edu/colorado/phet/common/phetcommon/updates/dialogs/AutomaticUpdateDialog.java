package edu.colorado.phet.common.phetcommon.updates.dialogs;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Frame;
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

/**
 * The dialog that used to automatically notify the user that an update is available.
 */
public class AutomaticUpdateDialog extends AbstractUpdateDialog {

    private static final String TITLE = PhetCommonResources.getString( "Common.updates.updateAvailable" );
    private static final String ASK_ME_LATER_BUTTON = PhetCommonResources.getString( "Common.updates.askMeLater" );
    private static final String SKIP_UPDATE_BUTTON = PhetCommonResources.getString( "Common.updates.skipThisUpdate" );
    private static final String ADVANCED_LINK = PhetCommonResources.getString( "Common.updates.advanced" );

    private final Frame owner;
    private final ITrackingInfo trackingInfo;
    private final IManualUpdateChecker manualUpdateChecker;
    private final IUpdateTimer updateTimer;
    private final IVersionSkipper versionSkipper;
    
    public AutomaticUpdateDialog( Frame owner, ISimInfo simInfo, ITrackingInfo trackingInfo, PhetVersion newVersion, IUpdateTimer updateTimer, IVersionSkipper versionSkipper ) {
        super( owner, TITLE, simInfo.getProjectName(), simInfo.getFlavor(), simInfo.getName(), simInfo.getVersion(), newVersion,simInfo.getLocaleString() );
        
        this.owner = owner;
        this.trackingInfo = trackingInfo;
        this.manualUpdateChecker = new DefaultManualUpdateChecker( owner, simInfo );
        this.updateTimer = updateTimer;
        this.versionSkipper = versionSkipper;
    }
    
    protected JPanel createButtonPanel( final String project, final String sim, final String simName, final PhetVersion currentVersion, final PhetVersion newVersion, String locale ) {
        
        // does the update
        JButton updateNowButton = new UpdateButton( project, sim,locale );

        // ignores this update until a later time
        JButton askMeLater = new JButton( ASK_ME_LATER_BUTTON );
        askMeLater.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateTimer.setStartTime( System.currentTimeMillis() );
                dispose();
                TrackingManager.postActionPerformedMessage( TrackingMessage.ASK_ME_LATER_PRESSED );
            }
        } );

        // ignores this version altogether
        JButton skipThisVersion = new JButton( SKIP_UPDATE_BUTTON );
        skipThisVersion.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                versionSkipper.setSkippedVersion( newVersion.getRevisionAsInt() );
                dispose();
                TrackingManager.postActionPerformedMessage( TrackingMessage.SKIP_UPDATE_PRESSED );
            }
        } );
        
        // panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add( updateNowButton );
        buttonPanel.add( Box.createHorizontalStrut( 30 ) );
        buttonPanel.add( askMeLater );
        buttonPanel.add( skipThisVersion );
        
        return buttonPanel;
    }
    
    protected JComponent createAdditionalMessageComponent() {
        // Advanced link, opens the Preferences dialog
        String advancedHTML = "<html><font size=\"2\"><u>" + ADVANCED_LINK + "</u></font></html>";
        JLabel advancedLink = new JLabel( advancedHTML );
        advancedLink.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        advancedLink.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                TrackingManager.postActionPerformedMessage( TrackingMessage.UPDATES_ADVANCED_PRESSED );
                dispose();
                new PreferencesDialog( owner, trackingInfo, manualUpdateChecker, new DefaultUpdatePreferences(), new DefaultTrackingPreferences() ).setVisible( true );
            }
        } );
        advancedLink.setForeground( Color.blue );
        return advancedLink;
    }

}
