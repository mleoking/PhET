/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.waveinterference.model.Oscillator;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:38:29 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class OscillatorControlPanel extends VerticalLayoutPanel {
    public OscillatorControlPanel( final Oscillator oscillator ) {
        final JComponent frequencySlider = new FrequencyControl( oscillator );
        add( frequencySlider );
        final JComponent amplitudeControl = new AmplitudeControl( oscillator );
        add( amplitudeControl );
//        setPreferredSize( new Dimension( (int)( getPreferredSize().width*0.8 ), getPreferredSize().height) );
    }
}
