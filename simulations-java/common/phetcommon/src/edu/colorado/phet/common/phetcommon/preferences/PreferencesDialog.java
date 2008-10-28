package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.tracking.TrackingManager;
import edu.colorado.phet.common.phetcommon.tracking.TrackingMessage;
import edu.colorado.phet.common.phetcommon.updates.ApplicationConfigManualCheckForUpdates;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

public class PreferencesDialog extends JDialog {

    private static final String TITLE = PhetCommonResources.getString( "Common.preferences.title" );
    private static final String UPDATES_TAB = PhetCommonResources.getString( "Common.preferences.updates" );
    private static final String TRACKING_TAB = PhetCommonResources.getString( "Common.preferences.tracking" );
    private static final String OK_BUTTON = PhetCommonResources.getString( "Common.choice.ok" );
    private static final String CANCEL_BUTTON = PhetCommonResources.getString( "Common.choice.cancel" );

    private final IUpdatesPreferences updatePreferences;
    private final ITrackingPreferences trackingPreferences;
    private final UpdatesPreferencesPanel updatesPreferencesPanel;
    private final TrackingPreferencesPanel trackingPreferencesPanel;

    public PreferencesDialog( Frame owner, ITrackingInfo trackingInfo, IManualUpdateChecker iCheckForUpdates, IUpdatesPreferences updatePreferences, ITrackingPreferences trackingPreferences ) {
        super( owner, TITLE );
        setResizable( false );
        setModal( false );

        this.updatePreferences = updatePreferences;
        this.trackingPreferences = trackingPreferences;

        JPanel userInputPanel = new JPanel();
        JTabbedPane jTabbedPane = new JTabbedPane();
        userInputPanel.add( jTabbedPane );
        updatesPreferencesPanel = new UpdatesPreferencesPanel( iCheckForUpdates, updatePreferences.isEnabled() );
        jTabbedPane.addTab( UPDATES_TAB, updatesPreferencesPanel );
        trackingPreferencesPanel = new TrackingPreferencesPanel( trackingInfo, trackingPreferences.isEnabled() );
        jTabbedPane.addTab( TRACKING_TAB, trackingPreferencesPanel );

        JButton okButton = new JButton( OK_BUTTON );
        okButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                savePreferences();
                dispose();
            }
        } );

        JButton cancelButton = new JButton( CANCEL_BUTTON );
        cancelButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                dispose();
            }
        } );

        JPanel buttonPanel = new JPanel( new FlowLayout( FlowLayout.CENTER ) );
        buttonPanel.add( okButton );
        buttonPanel.add( cancelButton );

        JPanel panel = new JPanel();
        EasyGridBagLayout layout = new EasyGridBagLayout( panel );
        panel.setLayout( layout );
        layout.addComponent( userInputPanel, 0, 0 );
        layout.addAnchoredComponent( buttonPanel, 2, 0, GridBagConstraints.CENTER );
        setContentPane( panel );
        pack();
        if ( owner != null ) {
            SwingUtils.centerDialogInParent( this );
        }
        else {
            SwingUtils.centerWindowOnScreen( this );
        }
    }

    public void dispose() {
        boolean wasVisible = isVisible();
        super.dispose();
        //this is to simplify things in the report generation, since disposing the dialog doesn't call setVisible(false)
        TrackingManager.postStateChangedMessage( TrackingMessage.PREFERENCES_DIALOG_VISIBLE, wasVisible, false );
    }

    public void setVisible( boolean b ) {
        boolean wasVisible = isVisible();
        super.setVisible( b );
        TrackingManager.postStateChangedMessage( TrackingMessage.PREFERENCES_DIALOG_VISIBLE, wasVisible, b );
    }

    private void savePreferences() {
        if ( updatePreferences.isEnabled() != updatesPreferencesPanel.isUpdatesEnabled() ) {
            updatePreferences.setEnabled( updatesPreferencesPanel.isUpdatesEnabled() );
            TrackingManager.postActionPerformedMessage( updatesPreferencesPanel.isUpdatesEnabled() ?
                                                        TrackingMessage.UPDATES_ENABLED : TrackingMessage.UPDATES_DISABLED );
        }

        if ( trackingPreferences.isEnabled() != trackingPreferencesPanel.isTrackingEnabled() ) {
            trackingPreferences.setEnabled( trackingPreferencesPanel.isTrackingEnabled() );

            //we should never see a tracking disabled message, since tracking should be disabled before we try to send that message.
            //can track number of people who disable tracking by checking whether their preferences dialog was opened and then we never hear from them again.
            TrackingManager.postActionPerformedMessage( trackingPreferencesPanel.isTrackingEnabled() ?
                                                        TrackingMessage.TRACKING_ENABLED : TrackingMessage.TRACKING_DISABLED );
        }

    }

    /*
    * Test, this edits the real preferences file!
    */
    public static void main( String[] args ) {
        final PhetApplicationConfig config = new PhetApplicationConfig( args, "balloons" );
        PreferencesDialog preferencesDialog = new PreferencesDialog( null, config, new ApplicationConfigManualCheckForUpdates( null, config ), new DefaultUpdatePreferences(),
                                                                     new DefaultTrackingPreferences() );
        preferencesDialog.addWindowListener( new WindowAdapter() {
            public void windowClosing( WindowEvent e ) {
                System.exit( 0 );
            }
        } );
        preferencesDialog.setVisible( true );
    }
}
