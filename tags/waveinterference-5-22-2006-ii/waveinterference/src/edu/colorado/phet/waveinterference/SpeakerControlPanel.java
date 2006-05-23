/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.view.HorizontalLayoutPanel;
import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.view.OscillatorControlPanel;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 2:13:06 PM
 * Copyright (c) Mar 28, 2006 by Sam Reid
 */

public class SpeakerControlPanel extends HorizontalLayoutPanel {
    public SpeakerControlPanel( Oscillator oscillator ) {
        OscillatorControlPanel oscillatorControlPanel = new OscillatorControlPanel( oscillator );
        add( oscillatorControlPanel );
    }
}
