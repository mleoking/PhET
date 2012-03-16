// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.waveinterference;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.waveinterference.util.WIStrings;

/**
 * User: Sam Reid
 * Date: Aug 23, 2006
 * Time: 10:23:15 AM
 */

public class ResetModuleControl extends JPanel {
    private WaveInterferenceModule module;

    public ResetModuleControl( final WaveInterferenceModule module ) {
        this.module = module;
        JButton resetAll = new JButton( WIStrings.getString( "controls.reset-all" ) );
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
