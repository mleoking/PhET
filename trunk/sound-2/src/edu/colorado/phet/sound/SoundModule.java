/**
 * Class: SoundModule
 * Package: edu.colorado.phet.sound
 * Author: Another Guy
 * Date: Aug 11, 2004
 */
package edu.colorado.phet.sound;

import edu.colorado.phet.common.application.ApplicationModel;
import edu.colorado.phet.common.application.Module;
import edu.colorado.phet.common.application.PhetApplication;
import edu.colorado.phet.sound.model.*;
import edu.colorado.phet.sound.view.RgbReporter;

public class SoundModule extends Module implements RgbReporter {
    private boolean audioEnabled = false;
    private boolean savedAudioEnabled = audioEnabled;
    private Wavefront primaryWavefront;
    private Wavefront octaveWavefront;
    private WavefrontOscillator primaryOscillator;
    private WavefrontOscillator octaveOscillator;
    private Listener currentListener;

    public SoundModule( ApplicationModel appModel, String name ) {
        super( name );
        this.setModel( new SoundModel( appModel.getClock() ) );
        initModel();
        initOscillators();
    }

    protected SoundModel getSoundModel() {
        return (SoundModel)getModel();
    }

    public void activate( PhetApplication app ) {
        super.activate( app );
        setAudioEnabled( savedAudioEnabled );
    }

    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        savedAudioEnabled = audioEnabled;
        setAudioEnabled( false );
    }

    public void setAudioEnabled( boolean enabled ) {
        audioEnabled = enabled;
        primaryOscillator.setEnabled( enabled );
        octaveOscillator.setEnabled( enabled && getSoundModel().isOctaveEnabled() );
    }

    private void initModel() {

        // Set up the primary wavefront
        primaryWavefront = new Wavefront( getSoundModel() );
        primaryWavefront.setWaveFunction( new SineWaveFunction( primaryWavefront ) );
        primaryWavefront.setEnabled( true );
        // todo: these lines should be collapsed into one call
        getSoundModel().addWaveFront( primaryWavefront );
        getSoundModel().setPrimaryWavefront( primaryWavefront );

        // Set up the octave wavefront
        octaveWavefront = new Wavefront( getSoundModel() );
        octaveWavefront.setWaveFunction( new SineWaveFunction( octaveWavefront ) );
        octaveWavefront.setMaxAmplitude( 0 );
        octaveWavefront.setEnabled( false );
        // todo: these lines should be collapsed into one call
        getSoundModel().addWaveFront( octaveWavefront );
        getSoundModel().setOctaveWavefront( octaveWavefront );

        getSoundModel().setFrequency( SoundConfig.s_defaultFrequency );
    }

    private void initOscillators() {
        primaryOscillator = new WavefrontOscillator( primaryWavefront, getModel() );
        octaveOscillator = new WavefrontOscillator( octaveWavefront, getModel() );
        setAudioEnabled( audioEnabled );
    }

    /**
     * Determines whether the source for audio is at the speaker or the listener
     *
     * @param source
     */
    public void setAudioSource( int source ) {
        // NOP  This should probably be an abstract method, or the classes refactored so this doesn't
        // need to be here at all.
    }

    public WavefrontOscillator getPrimaryOscillator() {
        return primaryOscillator;
    }

    public WavefrontOscillator getOctaveOscillator() {
        return octaveOscillator;
    }

    public void setListener( Listener listener ) {
        currentListener = listener;
        getPrimaryOscillator().observe( listener );
        getOctaveOscillator().observe( listener );
    }

    public Listener getCurrentListener() {
        return currentListener;
    }

    public void setOscillatorFrequency( double dopplerFrequency ) {
        getPrimaryOscillator().setFrequency( (float)dopplerFrequency );
        getOctaveOscillator().setFrequency( (float)dopplerFrequency * 2 );
    }

    /**
     * Default behavior is to return middle gray
     * @param x
     * @param y
     * @return
     */
    public int rgbAt( int x, int y ) {
        return 128;
    }
}
