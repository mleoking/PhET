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
import edu.colorado.phet.sound.model.Listener;
import edu.colorado.phet.sound.model.SineWaveFunction;
import edu.colorado.phet.sound.model.SoundModel;
import edu.colorado.phet.sound.model.Wavefront;
import edu.colorado.phet.sound.view.RgbReporter;
import edu.colorado.phet.sound.view.WavefrontOscillator;

public class SoundModule extends Module implements RgbReporter {

    private static WavefrontOscillator primaryOscillator;
    private static WavefrontOscillator octaveOscillator;

    private boolean audioEnabled = false;
    private Wavefront primaryWavefront;
    private Wavefront octaveWavefront;
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
        primaryOscillator.run();

        // We need to let the oscillator get going before we do the rest here,
        // otherwise the volume gets stuck on full
        try {
            Thread.sleep( 200 );
        }
        catch( InterruptedException e ) {
            e.printStackTrace();
        }
        setAudioEnabled( audioEnabled );
        if( currentListener != null ) {
            setListener( currentListener );
        }
    }

    public void deactivate( PhetApplication app ) {
        super.deactivate( app );
        primaryOscillator.stop();
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
        if( primaryOscillator == null ) {
            primaryOscillator = new WavefrontOscillator( primaryWavefront );
        }
        if( octaveOscillator == null ) {
            octaveOscillator = new WavefrontOscillator( octaveWavefront );
        }
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
     *
     * @param x
     * @param y
     * @return
     */
    public int rgbAt( int x, int y ) {
        return 128;
    }
}
