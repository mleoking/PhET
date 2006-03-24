/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.waveinterference.model.Oscillator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 24, 2006
 * Time: 2:38:29 AM
 * Copyright (c) Mar 24, 2006 by Sam Reid
 */

public class FrequencyAmplitudeControlPanel extends VerticalLayoutPanel {
    public FrequencyAmplitudeControlPanel( final Oscillator oscillator ) {
        final ModelSlider frequencySlider = new ModelSlider( "Frequency", "Hz", 0, 10, oscillator.getFrequency() );
        frequencySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                oscillator.setFrequency( frequencySlider.getValue() );
            }
        } );
        add( frequencySlider );
        frequencySlider.setTextFieldVisible( false );

        final ModelSlider amplitudeSlider = new ModelSlider( "Amplitude", "cm", 0, 2, oscillator.getAmplitude() );
        amplitudeSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                oscillator.setAmplitude( amplitudeSlider.getValue() );
            }
        } );
        amplitudeSlider.setTextFieldVisible( false );
        add( amplitudeSlider );
    }
}
