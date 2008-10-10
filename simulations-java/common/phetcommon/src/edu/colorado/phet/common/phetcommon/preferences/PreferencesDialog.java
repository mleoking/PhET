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
import edu.colorado.phet.common.phetcommon.application.PhetApplicationConfig.NullApplicationConstructor;
import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.updates.ApplicationConfigManualCheckForUpdates;
import edu.colorado.phet.common.phetcommon.view.util.EasyGridBagLayout;
import edu.colorado.phet.common.phetcommon.view.util.SwingUtils;

public class PreferencesDialog extends JDialog {
    
    private final IUpdatesPreferences updatePreferences;
    private final ITrackingPreferences trackingPreferences;
    private final UpdatesPreferencesPanel updatesPreferencesPanel;
    private final TrackingPreferencesPanel trackingPreferencesPanel;
    
    public PreferencesDialog( Frame owner, ITrackingInfo tracker, IManualUpdateChecker iCheckForUpdates, IUpdatesPreferences updatePreferences, ITrackingPreferences trackingPreferences ) {
        super( owner, "Preferences", false /* modal */ );
        setResizable( false );
        
        this.updatePreferences = updatePreferences;
        this.trackingPreferences = trackingPreferences;
        
        JPanel userInputPanel = new JPanel();
        JTabbedPane jTabbedPane = new JTabbedPane();
        userInputPanel.add( jTabbedPane );
        updatesPreferencesPanel = new UpdatesPreferencesPanel( iCheckForUpdates, updatePreferences.isEnabled() );
        jTabbedPane.addTab( "Updates", updatesPreferencesPanel );
        trackingPreferencesPanel = new TrackingPreferencesPanel( tracker, trackingPreferences.isEnabled() );
        jTabbedPane.addTab( "Tracking", trackingPreferencesPanel );

        JButton okButton = new JButton( PhetCommonResources.getString( "Common.choice.ok" ) );
        okButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                savePreferences();
                dispose();
            }
        } );
        
        JButton cancelButton = new JButton( PhetCommonResources.getString( "Common.choice.cancel" ) );
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
        System.out.println( "PreferencesDialog.savePreferences" );//XXX
        updatePreferences.setEnabled( updatesPreferencesPanel.isUpdatesEnabled() );
        trackingPreferences.setEnabled( trackingPreferencesPanel.isTrackingEnabled() );
    }
    
    /**
     * Test, this edits the real preferences file!
     */
    public static void main( String[] args ) {
        final PhetApplicationConfig config = new PhetApplicationConfig( args, new NullApplicationConstructor(), "balloons" );
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
