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
import edu.colorado.phet.sound.view.WavefrontOscillator;

public class SoundModule extends Module implements RgbReporter {

    private static WavefrontOscillator primaryOscillator = new WavefrontOscillator();
    private static WavefrontOscillator octaveOscillator = new WavefrontOscillator();
    static {
        primaryOscillator.run();
        octaveOscillator.run();
    }

    private boolean audioEnabled = false;
    private Wavefront primaryWavefront;
    private Wavefront octaveWavefront;
    private Listener currentListener;

    public SoundModule( ApplicationModel appModel, String name ) {
        super( name );
        this.setModel( new SoundModel( appModel.getClock() ) );
        initModel();
    }

    protected SoundModel getSoundModel() {
        return (SoundModel)getModel();
    }

    public void activate( PhetApplication app ) {
        super.activate( app );
        primaryOscillator.setWavefront( this.primaryWavefront );
        octaveOscillator.setWavefront( this.octaveWavefront );
        setAudioEnabled( audioEnabled );
        if( currentListener != null ) {
            setListener( currentListener );
        }
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

    /**
     * Determines whether the source for audio is at the speaker or the listener
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

        primaryOscillator.setWavefront( this.primaryWavefront );
        octaveOscillator.setWavefront( this.octaveWavefront );

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
     * This method is provided so subclass modules can get the background color at
     * a particular pixel. It was introduced so we could write the module that
     * evacuates air from a box. Default behavior is to return middle gray.
     * @param x
     * @param y
     * @return
     */
    public int rgbAt( int x, int y ) {
        return 128;
    }
}
