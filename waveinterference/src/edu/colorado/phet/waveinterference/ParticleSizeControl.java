/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.common.view.ModelSlider;
import edu.colorado.phet.common.view.VerticalLayoutPanel;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 5:20:27 PM
 * Copyright (c) Mar 28, 2006 by Sam Reid
 */

public class ParticleSizeControl extends VerticalLayoutPanel {
    private SoundWaveGraphic soundWaveGraphic;

    public ParticleSizeControl( final SoundWaveGraphic soundWaveGraphic ) {
        this.soundWaveGraphic = soundWaveGraphic;
        final ModelSlider modelSlider = new ModelSlider( "Magnify Particles", "", 0, 1, soundWaveGraphic.getParticleSize() );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                soundWaveGraphic.setParticleSize( modelSlider.getValue() );
            }
        } );
        add( modelSlider );
    }
}
