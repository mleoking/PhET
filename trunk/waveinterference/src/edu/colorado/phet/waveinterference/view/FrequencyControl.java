/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference.view;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.waveinterference.model.Oscillator;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 8:24:25 PM
 * Copyright (c) Mar 28, 2006 by Sam Reid
 */

public class FrequencyControl extends VerticalLayoutPanel {
    public FrequencyControl( final Oscillator oscillator ) {
        final ModelSlider frequencySlider = new ModelSlider( "Frequency", "Hz", 0, 3, oscillator.getFrequency() );
        frequencySlider.setBorder( null );
        frequencySlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                oscillator.setFrequency( frequencySlider.getValue() );
            }
        } );
        frequencySlider.setTextFieldVisible( false );
        frequencySlider.setPaintLabels( false );
        add( frequencySlider );
    }
}
