// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.waveinterference;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.waveinterference.util.WIStrings;

/**
 * Created by: Sam
 * Feb 26, 2008 at 8:18:06 AM
 */
public class AddWallPotentialButton extends JButton {
    public AddWallPotentialButton( final WaveInterferenceModel waveInterferenceModel ) {
        this( waveInterferenceModel, WIStrings.getString( "controls.add-wall" ) );
    }

    public AddWallPotentialButton( final WaveInterferenceModel waveInterferenceModel, String buttonLabel ) {
        super( buttonLabel );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                waveInterferenceModel.getWallPotentials().addPotential( new WallPotential( new Point( 10, 10 ), new Point( 50, 50 ), waveInterferenceModel.getWaveModel() ) );
            }
        } );
    }

}
