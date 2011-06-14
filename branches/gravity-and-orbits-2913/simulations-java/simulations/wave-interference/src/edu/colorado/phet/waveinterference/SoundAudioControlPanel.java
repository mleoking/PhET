// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference;

import javax.swing.*;

import edu.colorado.phet.waveinterference.sound.AudioControlPanel;

/**
 * User: Sam Reid
 * Date: May 15, 2006
 * Time: 1:15:10 AM
 */

public class SoundAudioControlPanel extends JPanel {
    private SoundModuleAudio soundModuleAudio;
    private final AudioControlPanel audioControlPanel;

    public SoundAudioControlPanel( final SoundModuleAudio soundModuleAudio ) {
        this.soundModuleAudio = soundModuleAudio;
        audioControlPanel = new AudioControlPanel();
        add( audioControlPanel );
        audioControlPanel.addListener( new AudioControlPanel.Listener() {
            public void audioEnabledChanged() {
                update();
            }

            public void amplitudeChanged() {
                update();
            }
        } );
        soundModuleAudio.addListener( new SoundModuleAudio.Listener() {
            public void audioEnabledChanged() {
                audioControlPanel.updateAudioEnabled( soundModuleAudio.isAudioEnabled() );
            }

            public void volumeChanged() {
                audioControlPanel.updateVolume( soundModuleAudio.getVolume() );
            }
        } );
    }

    private void update() {
        soundModuleAudio.setAudioEnabled( audioControlPanel.isAudioEnabled() );
        soundModuleAudio.setVolume( audioControlPanel.getVolume() );
    }
}
