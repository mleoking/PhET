/**
 * Class: SingleSourceApparatusPanel
 * Package: edu.colorado.phet.sound.view
 * Author: Another Guy
 * Date: Aug 4, 2004
 */
package edu.colorado.phet.sound.view;

import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.SimpleObserver;
import edu.colorado.phet.sound.SoundConfig;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.model.WaveMedium;

public class SingleSourceApparatusPanel extends SoundApparatusPanel {

    private SpeakerGraphic speakerGraphic;


    /**
     * @param model
     */
    public SingleSourceApparatusPanel( SoundModel model, IClock clock ) {
        super( model, clock );
        this.setBackground( SoundConfig.MIDDLE_GRAY );

        // Set up the speaker
        final WaveMedium waveMedium = model.getWaveMedium();
        speakerGraphic = new SpeakerGraphic( this, waveMedium );
        this.addGraphic( speakerGraphic, 8 );
        waveMedium.addObserver( new SimpleObserver() {
            private int s_maxSpeakerConeExcursion = 6;

            public void update() {
                int coneOffset = (int)( waveMedium.getAmplitudeAt( 0 ) / SoundConfig.s_maxAmplitude * s_maxSpeakerConeExcursion );
                speakerGraphic.setConePosition( coneOffset );
            }
        } );
    }
}
