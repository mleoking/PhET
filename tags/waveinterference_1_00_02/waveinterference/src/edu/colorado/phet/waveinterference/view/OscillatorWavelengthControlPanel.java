/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.waveinterference.model.Oscillator;

import javax.swing.*;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:38:29 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class OscillatorWavelengthControlPanel extends VerticalLayoutPanel {
    private WavelengthControlPanel frequencySlider;

    public OscillatorWavelengthControlPanel( WaveModelGraphic waveModelGraphic, final Oscillator oscillator ) {
        frequencySlider = new WavelengthControlPanel( waveModelGraphic, oscillator );
        add( frequencySlider );
        final JComponent amplitudeControl = new AmplitudeControl( oscillator );
        add( amplitudeControl );
    }

    public void addChangeListener( ChangeListener changeListener ) {
        frequencySlider.addChangeListener( changeListener );
    }
}
