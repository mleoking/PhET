/*  */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.model.WaveModel;
import edu.colorado.phet.waveinterference.util.WIStrings;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * User: Sam Reid
 * Date: May 15, 2006
 * Time: 12:32:35 AM
 *
 */

public class ClearWaveControl extends JPanel {
    private WaveModel waveModel;

    public ClearWaveControl( WaveModel waveModel ) {
        this.waveModel = waveModel;
        JButton clearWave = new JButton( WIStrings.getString( "clear.wave" ) );
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
