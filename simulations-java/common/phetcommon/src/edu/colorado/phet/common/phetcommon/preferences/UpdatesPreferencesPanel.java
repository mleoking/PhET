package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;
import edu.colorado.phet.common.phetcommon.updates.IManualUpdateChecker;
import edu.colorado.phet.common.phetcommon.util.DeploymentScenario;

/**
 * Panel for displaying preferences for the updates feature.
 */
public class UpdatesPreferencesPanel extends JPanel {
    
    private static final String UPDATES_ENABLED = PhetCommonResources.getString( "Common.updates.automaticallyCheck" );
    private static final String CHECK_FOR_SIM_UPDATES = PhetCommonResources.getString( "Common.updates.checkForSimUpdate" );
    private static final String CHECK_FOR_INSTALLER_UPDATE = PhetCommonResources.getString( "Common.updates.checkForInstallerUpdate" );
    
    private final PhetPreferences preferences;
    private final JCheckBox updatesEnabledCheckBox;
    
    public UpdatesPreferencesPanel( final IManualUpdateChecker iCheckForUpdates, PhetPreferences preferences ) {

        this.preferences = preferences;
        
        // enable
        updatesEnabledCheckBox = new JCheckBox( UPDATES_ENABLED, preferences.isUpdatesEnabled() );
        
        // check for sim update
        JButton checkForSimUpdateButton = new JButton( CHECK_FOR_SIM_UPDATES );
        checkForSimUpdateButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                iCheckForUpdates.checkForUpdates();
            }
        } );
        
        // check for installer update
        JButton checkForInstallerUpdateButton = new JButton( CHECK_FOR_INSTALLER_UPDATE );
        checkForInstallerUpdateButton.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                //TODO
            }
        } );
        
        // layout
        setLayout( new GridBagLayout() );
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridx = 0;
        constraints.gridwidth = 1;
        add( Box.createRigidArea( new Dimension( 50, 20 ) ), constraints );
        add( updatesEnabledCheckBox, constraints );
        add( Box.createRigidArea( new Dimension( 50, 10 ) ), constraints );
        add( checkForSimUpdateButton, constraints );
        if ( DeploymentScenario.getInstance() == DeploymentScenario.PHET_INSTALLATION ) {
            add( checkForInstallerUpdateButton, constraints );
        }
    }
    
    /**
     * Saves the preference values in this panel.
     */
    public void save() {
        preferences.setUpdatesEnabled( updatesEnabledCheckBox.isSelected() );
    }
}
