// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.util.WIStrings;

/**
 * User: Sam Reid
 * Date: May 15, 2006
 * Time: 12:32:35 AM
 */

public class ClearWaveControl extends JPanel {
    private WaveModel waveModel;

    public ClearWaveControl( WaveModel waveModel ) {
        this.waveModel = waveModel;
        JButton clearWave = new JButton( WIStrings.getString( "controls.clear-wave" ) );
        clearWave.addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                clearWave();
            }
        } );
        add( clearWave );
    }

    private void clearWave() {
        waveModel.clear();
    }
}
