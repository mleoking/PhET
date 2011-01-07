// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import edu.colorado.phet.common.phetcommon.view.ModelSlider;
import edu.colorado.phet.common.phetcommon.view.VerticalLayoutPanel;
import edu.colorado.phet.waveinterference.util.WIStrings;

/**
 * User: Sam Reid
 * Date: Mar 28, 2006
 * Time: 5:20:27 PM
 */

public class ParticleSizeSliderControl extends VerticalLayoutPanel {
    private SoundWaveGraphic soundWaveGraphic;

    public ParticleSizeSliderControl( final SoundWaveGraphic soundWaveGraphic ) {
        this.soundWaveGraphic = soundWaveGraphic;
        final ModelSlider modelSlider = new ModelSlider( WIStrings.getString( "sound.magnify-particles" ), "", 0, 1, soundWaveGraphic.getParticleSize() );
        modelSlider.setTextFieldVisible( false );
        modelSlider.addChangeListener( new ChangeListener() {
            public void stateChanged( ChangeEvent e ) {
                soundWaveGraphic.setParticleSize( modelSlider.getValue() );
            }
        } );
        add( modelSlider );
    }
}
