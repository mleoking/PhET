/* Copyright 2004, Sam Reid */
package edu.colorado.phet.waveinterference;

import edu.colorado.phet.waveinterference.sound.AudioControlPanel;

import javax.swing.*;

/**
 * User: Sam Reid
 * Date: May 15, 2006
 * Time: 1:15:10 AM
 * Copyright (c) May 15, 2006 by Sam Reid
 */

public class SoundAudioControlPanel extends JPanel {
    private SoundModuleAudio soundModuleAudio;
    private final AudioControlPanel audioControlPanel;

    public SoundAudioControlPanel( SoundModuleAudio soundModuleAudio ) {
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
    }

    private void update() {
        soundModuleAudio.setAudioEnabled( audioControlPanel.isAudioEnabled() );
        soundModuleAudio.setAmplitude( audioControlPanel.getAmplitude() );
    }
}
