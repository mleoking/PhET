package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.util.WIStrings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: Aug 23, 2006
 * Time: 10:23:15 AM
 * Copyright (c) Aug 23, 2006 by Sam Reid
 */

public class ResetModuleControl extends JPanel {
    private WaveInterferenceModule module;

    public ResetModuleControl( final WaveInterferenceModule module ) {
        this.module = module;
        JButton resetAll = new JButton( WIStrings.getString( "reset.all" ) );
        resetAll.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clearWave();
            }

            private void clearWave() {
                module.queryResetAll();
            }
        } );
        add( resetAll );
    }
}
