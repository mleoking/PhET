package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.*;

import javax.swing.*;

public class UpdatesPreferencesPanel extends JPanel {
    public UpdatesPreferencesPanel() {
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
        add( new JButton( "Check for update..." ), constraints );
    }
}
