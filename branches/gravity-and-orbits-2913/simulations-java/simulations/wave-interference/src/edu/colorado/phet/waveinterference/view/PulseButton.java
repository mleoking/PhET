// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.waveinterference.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.util.WIStrings;

/**
 * Created by: Sam
 * Feb 6, 2008 at 10:53:55 AM
 */
public class PulseButton extends JButton {
    public PulseButton( final Oscillator oscillator ) {
        super( WIStrings.getString( "controls.pulse" ) );
        addActionListener( new ActionListener() {
            public void actionPerformed( ActionEvent e ) {
                oscillator.firePulse();
            }
        } );
    }
}
