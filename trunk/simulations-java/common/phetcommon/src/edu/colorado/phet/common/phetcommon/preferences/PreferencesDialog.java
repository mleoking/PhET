package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
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

    private void savePreferences() {
        updatePreferences.setEnabled( updatesPreferencesPanel.isUpdatesEnabled() );
        trackingPreferences.setEnabled( trackingPreferencesPanel.isTrackingEnabled() );
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
