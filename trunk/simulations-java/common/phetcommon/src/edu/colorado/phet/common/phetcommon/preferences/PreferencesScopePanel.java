package edu.colorado.phet.common.phetcommon.preferences;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class PreferencesScopePanel extends JPanel {
    public PreferencesScopePanel( final IPreferences iTrackingPreferences ) {
        setLayout( new BoxLayout( this, BoxLayout.Y_AXIS ) );
        add( new JLabel( "Make my preferences apply to:" ) );
        ButtonGroup buttonGroup = new ButtonGroup();
        JRadioButton thisOnly = new JRadioButton( "this simulation", !iTrackingPreferences.isApplyToAllSimulations() );
        thisOnly.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                iTrackingPreferences.setApplyToAllSimulations( false );
            }
        } );
        JRadioButton all = new JRadioButton( "all PhET simulations", iTrackingPreferences.isApplyToAllSimulations() );
        all.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                iTrackingPreferences.setApplyToAllSimulations( true );
            }
        } );
        buttonGroup.add( thisOnly );
        buttonGroup.add( all );
        add( thisOnly );
        add( all );
    }
}
