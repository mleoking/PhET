/**
 * Class: SoundModel
 * Package: edu.colorado.phet.sound.model
 * Author: Another Guy
 * Date: Aug 3, 2004
 */
package edu.colorado.phet.sound.model;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.sound.SoundConfig;

import java.util.Iterator;

public class SoundModel extends BaseModel {

    private WavefrontType wavefrontType;

    private WaveMedium waveMedium = new WaveMedium();
    private AbstractClock clock;

    private Wavefront primaryWavefront;
    private Wavefront octaveWavefront;
    private boolean octaveEnabled = false;

    // Used to save and restore audio state when Stop and Run are pressed
//    private boolean savedAudioEnabledState = false;


    public SoundModel( AbstractClock clock ) {
        this.clock = clock;
        addModelElement( waveMedium );
    }

    /**
     *
     */
    public WaveMedium getWaveMedium() {
        return waveMedium;
    }

    /**
     *
     */
    public void addWaveFront( Wavefront wavefront ) {
        waveMedium.addWavefront( wavefront );

        // TODO: This should go somewhere else.
        setPropagationSpeed( 3 );
    }

    public void removeWaveFront( Wavefront wavefront ) {
        waveMedium.removeWavefront( wavefront );
    }

    public WavefrontType getWavefrontType() {
        return wavefrontType;
    }

    public void setWavefrontType( WavefrontType wavefrontType ) {
        this.wavefrontType = wavefrontType;
        for( Iterator iterator = waveMedium.getWavefronts().iterator(); iterator.hasNext(); ) {
            Wavefront wavefront = (Wavefront)iterator.next();
            wavefront.setWavefrontType( wavefrontType );
        }
    }

    public void setPropagationSpeed( int propogationSpeed ) {
        for( Iterator iterator = waveMedium.getWavefronts().iterator(); iterator.hasNext(); ) {
            Wavefront wavefront = (Wavefront)iterator.next();
            wavefront.setPropagationSpeed( propogationSpeed );
        }
    }

    public void clear() {
        for( Iterator iterator = waveMedium.getWavefronts().iterator(); iterator.hasNext(); ) {
            Wavefront wavefront = (Wavefront)iterator.next();
            wavefront.clear();
        }
        // Step the system to notify all observers
        this.stepInTime( clock.getDt() );
    }

    /**
     *
     */
    public synchronized void stepInTime( double dt ) {
        super.stepInTime( dt );
    }

    /**
     *
     */
    public void setFrequency( double frequency ) {
        primaryWavefront.setFrequency( frequency / SoundConfig.s_frequencyDisplayFactor );
        octaveWavefront.setFrequency( 2 * frequency / SoundConfig.s_frequencyDisplayFactor );
    }

    /**
     *
     * @return Maximum amplitude of the main speaker
     */
    public double getAmplitude() {
        return primaryWavefront.getMaxAmplitude();
    }

    /**
     * TODO: move to command
     */
    public void setAmplitude( double amplitude ) {
        primaryWavefront.setMaxAmplitude( amplitude );
    }

    public void setOctaveWavefront( Wavefront octaveWavefront ) {
        this.octaveWavefront = octaveWavefront;
    }

    /**
     * TODO: move to command
     */
    public void setOctaveAmplitude( double amplitude ) {
//        this.octaveAmplitude = amplitude;
        octaveWavefront.setMaxAmplitude( amplitude );
    }

    public Wavefront getPrimaryWavefront() {
        return primaryWavefront;
    }

    public void setPrimaryWavefront( Wavefront primaryWavefront ) {
        this.primaryWavefront = primaryWavefront;
    }

    public void setOctaveEnabled( boolean octaveEnabled ) {
        this.octaveEnabled = octaveEnabled;
        this.octaveWavefront.setEnabled( octaveEnabled );
    }

    public boolean isOctaveEnabled() {
        return octaveEnabled;
    }

}
