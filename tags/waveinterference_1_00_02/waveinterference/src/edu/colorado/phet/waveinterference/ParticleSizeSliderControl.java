/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;
import edu.colorado.phet.waveinterference.util.WIStrings;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 5:20:27 PM
 * Copyright (c) Mar 28, 2006 by Sam Reid
 */

public class ParticleSizeSliderControl extends VerticalLayoutPanel {
    private SoundWaveGraphic soundWaveGraphic;

    public ParticleSizeSliderControl( final SoundWaveGraphic soundWaveGraphic ) {
        this.soundWaveGraphic = soundWaveGraphic;
        final ModelSlider modelSlider = new ModelSlider( WIStrings.getString( "magnify.particles" ), "", 0, 1, soundWaveGraphic.getParticleSize() );
        modelSlider.setTextFieldVisible( false );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                soundWaveGraphic.setParticleSize( modelSlider.getValue() );
            }
        } );
        add( modelSlider );
    }
}
