/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.waveinterference.model.Oscillator;

import javax.swing.*;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 27, 2006
 * Time: 9:59:46 AM
 * Copyright (c) Mar 27, 2006 by Sam Reid
 */

public class LaserControlPanel extends HorizontalLayoutPanel {
    private Oscillator oscillator;
    private OscillatorWavelengthControlPanel wavelengthControlPanel;

    public LaserControlPanel( WaveModelGraphic waveModelGraphic, Oscillator oscillator ) {
        this.oscillator = oscillator;
        wavelengthControlPanel = new OscillatorWavelengthControlPanel( waveModelGraphic, oscillator );
        addControl( wavelengthControlPanel );
//        addControl( new OscillatorOnOffControlPanel( oscillator ) );
//        setBorder( BorderFactory.createRaisedBevelBorder() );
    }

    private void addControl( JComponent component ) {
        add( component );
    }

    public Oscillator getOscillator() {
        return oscillator;
    }

    public void addChangeListener( ChangeListener changeListener ) {
        wavelengthControlPanel.addChangeListener( changeListener );
    }
}
