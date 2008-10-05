package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.resources.PhetCommonResources;

public class UpdatesPreferencesPanel extends JPanel {

    public UpdatesPreferencesPanel( final IManuallyCheckForUpdates iCheckForUpdates ) {
        setLayout( new GridBagLayout() );
        GridBagConstraints constraints = new GridBagConstraints();
        constraints.gridy = GridBagConstraints.RELATIVE;
        constraints.gridx = 0;
        constraints.gridwidth = 1;

        JCheckBox autoCheck = new JCheckBox( "Automatically check for updates", true );
        add( Box.createRigidArea( new Dimension( 50, 20 ) ), constraints );
        add( autoCheck, constraints );
        add( Box.createRigidArea( new Dimension( 50, 10 ) ), constraints );
        add( new PreferencesScopePanel(), constraints );
        add( Box.createRigidArea( new Dimension( 50, 10 ) ), constraints );
        JButton button = new JButton( PhetCommonResources.getString( "Common.HelpMenu.CheckForUpdates" ) );
        button.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                iCheckForUpdates.checkForUpdates();
            }
        } );
        add( button, constraints );
    }

}
