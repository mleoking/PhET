// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.phetcommon.view.HorizontalLayoutPanel;
import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.view.OscillatorControlPanel;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 2:13:06 PM
 */

public class SpeakerControlPanel extends HorizontalLayoutPanel {
    public SpeakerControlPanel( Oscillator oscillator ) {
        OscillatorControlPanel oscillatorControlPanel = new OscillatorControlPanel( oscillator );
        add( oscillatorControlPanel );
    }
}
