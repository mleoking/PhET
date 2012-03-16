// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference.view;

import javax.swing.*;

import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.waveinterference.model.Oscillator;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:38:29 AM
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
