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

import edu.colorado.phet.common.phetcommon.application.PhetApplication;
import edu.colorado.phet.common.phetcommon.application.ISimInfo;
import edu.colorado.phet.common.phetcommon.preferences.*;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.updates.*;
import edu.colorado.phet.common.phetcommon.updates.dialogs.UpdateInstructionsDialog.AutomaticUpdateInstructionsDialog;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.HTMLUtils.InteractiveHTMLPane;

/**
 * The dialog that used to automatically notify the user that an update is available.
 */
public class AutomaticUpdateDialog extends AbstractUpdateDialog {
    
    private static final String TITLE = PhetCommonResources.getString( "Common.updates.updateAvailable" );
    private static final String UPDATE_NOW_BUTTON = PhetCommonResources.getString( "Common.updates.updateNow" );
    private static final String ASK_ME_LATER_BUTTON = PhetCommonResources.getString( "Common.updates.askMeLater" );
    private static final String SKIP_UPDATE_BUTTON = PhetCommonResources.getString( "Common.updates.skipThisUpdate" );
    private static final String EDIT_PREFERENCES_LINK = PhetCommonResources.getString( "Common.updates.editPreferences" );
    
    public AutomaticUpdateDialog( PhetApplication application, PhetVersion newVersion ) {
        this( application.getPhetFrame(), 
              application.getSimInfo().getProjectName(),
              application.getSimInfo().getFlavor(),
              application.getSimInfo().getName(),
              application.getSimInfo().getVersion(),
              newVersion,
              application.getTrackingInfo(), 
              new ApplicationConfigManualCheckForUpdates( application.getPhetFrame(), application.getSimInfo() ),
              application.getSimInfo(),
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
        String html = getAutomaticUpdateMessageHTML( simName, currentVersion.formatForTitleBar(), newVersion.formatForTitleBar() );
        JComponent htmlPane = new InteractiveHTMLPane( html );
        JPanel messagePanel = new JPanel();
        messagePanel.setBorder( BorderFactory.createEmptyBorder( 5, 5, 5, 5 ) );
        messagePanel.add( htmlPane );
        
        // opens a web browser to the sim's web page
        JButton updateNowButton = new JButton( UPDATE_NOW_BUTTON );
        updateNowButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
                JDialog dialog = new AutomaticUpdateInstructionsDialog( owner, project, sim, simName, currentVersion.formatForTitleBar(), newVersion.formatForTitleBar() );
                dialog.setVisible( true );
            }
        } );

        // ignores this update until a later time
        JButton askMeLater = new JButton( ASK_ME_LATER_BUTTON );
        askMeLater.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                updateTimer.setLastAskMeLaterTime( project, sim, System.currentTimeMillis() );
                dispose();
            }
        } );

        // ignores this version altogether
        JButton skipThisVersion = new JButton( SKIP_UPDATE_BUTTON );
        skipThisVersion.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                versionSkipper.skipThisVersion( config.getProjectName(), config.getFlavor(), newVersion );
                dispose();
            }
        } );
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.add( updateNowButton );
        buttonPanel.add( askMeLater );
        buttonPanel.add( skipThisVersion );

        // link to the Preferences dialog
        String preferencesHTML = "<html><font size=\"3\"><u>" + EDIT_PREFERENCES_LINK + "</u></font></html>";
        JLabel preferencesLink = new JLabel( preferencesHTML );
        preferencesLink.setCursor( Cursor.getPredefinedCursor( Cursor.HAND_CURSOR ) );
        preferencesLink.addMouseListener( new MouseAdapter() {
            public void mousePressed( MouseEvent e ) {
                dispose();
                new PreferencesDialog( owner, trackingInfo, iManuallyCheckForUpdates, new DefaultUpdatePreferences(), new DefaultTrackingPreferences() ).setVisible( true );
            }
        } );
        preferencesLink.setForeground( Color.blue );
        buttonPanel.add( preferencesLink );

        // main panel layout
        JPanel panel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        int row = 0;
        int column = 0;
        layout.addComponent( messagePanel, row++, column );
        layout.addFilledComponent( new JSeparator(), row++, column, GridBagConstraints.HORIZONTAL );
        layout.addComponent( buttonPanel, row++, column );

        setContentPane( panel );
        pack();
        center();
    }
}
