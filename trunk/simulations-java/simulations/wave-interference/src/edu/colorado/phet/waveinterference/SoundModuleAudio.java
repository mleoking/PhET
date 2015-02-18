// Copyright 2002-2011, University of Colorado

/*  */
package edu.colorado.phet.waveinterference;

import java.util.ArrayList;

import javax.sound.sampled.LineUnavailableException;

import edu.colorado.phet.waveinterference.model.Oscillator;
import edu.colorado.phet.waveinterference.sound.FourierSoundPlayer;

/**
 * User: Sam Reid
 * Date: May 15, 2006
 * Time: 1:15:40 AM
 */

public class SoundModuleAudio {
    private FourierSoundPlayer fourierSoundPlayer;
    private WaveInterferenceModel waveInterferenceModel;
    private static final double FREQ_SCALE = 440 / 0.5;
    private double volume = 1.0;

    public SoundModuleAudio( WaveInterferenceModel waveInterferenceModel ) {
        this.waveInterferenceModel = waveInterferenceModel;
        try {
            fourierSoundPlayer = new FourierSoundPlayer();
        }
        catch ( LineUnavailableException e ) {
            e.printStackTrace();
        }
        waveInterferenceModel.getPrimaryOscillator().addListener( new Oscillator.Listener() {
            public void enabledStateChanged() {

            }

            public void locationChanged() {
            }

            public void frequencyChanged() {
                updateFrequency();
            }

            public void amplitudeChanged() {
                updateAmplitude();
            }

        } );
        updateFrequency();
        updateAmplitude();
    }

    private void updateAmplitude() {
        if ( fourierSoundPlayer != null ) {
//            System.out.println( "getPrimaryAmplitude() = " + getPrimaryAmplitude() );
            fourierSoundPlayer.setVolume( Math.min( 1.0f, (float) volume * getPrimaryAmplitude() ) );
        }
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.volumeChanged();
        }
    }

    private void updateFrequency() {
//        System.out.println( "getPrimaryFrequency() = " + getPrimaryFrequency() );
        if ( fourierSoundPlayer != null ) {
            fourierSoundPlayer.setFrequency( getPrimaryFrequency() * FREQ_SCALE );
        }
    }

    private double getPrimaryFrequency() {
        return waveInterferenceModel.getPrimaryOscillator().getFrequency();
    }

    public void setAudioEnabled( boolean audioEnabled ) {
        if ( fourierSoundPlayer != null ) {
            fourierSoundPlayer.setSoundEnabled( audioEnabled && isPrimaryOscillatorEnabled() );
        }
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.audioEnabledChanged();
        }
    }

    private ArrayList listeners = new ArrayList();

    public static interface Listener {
        void audioEnabledChanged();

        void volumeChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    private boolean isPrimaryOscillatorEnabled() {
        return waveInterferenceModel.getPrimaryOscillator().isEnabled();
    }

    public void setVolume( double volume ) {
        this.volume = volume;
        updateAmplitude();
    }

    private float getPrimaryAmplitude() {
        return (float) waveInterferenceModel.getPrimaryOscillator().getAmplitude();//todo normalize this by the max
    }

    public void setActive( boolean active ) {
        if ( fourierSoundPlayer != null ) {
            fourierSoundPlayer.setActive( active );
        }
    }

    public void reset() {
        setAudioEnabled( false );
        setVolume( 0.5 );
    }

    public boolean isAudioEnabled() {
        return fourierSoundPlayer != null && fourierSoundPlayer.isEnabled();
    }

    public double getVolume() {
        return volume;
    }
}
