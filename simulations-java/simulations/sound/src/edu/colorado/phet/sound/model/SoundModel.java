/**
 * Class: SoundModel
 * Package: edu.colorado.phet.sound.model
 * Author: Another Guy
 * Date: Aug 3, 2004
 */
package edu.colorado.phet.sound.model;

import java.util.Iterator;

import edu.colorado.phet.common.phetcommon.model.BaseModel;
import edu.colorado.phet.sound.SoundConfig;

public class SoundModel extends BaseModel {

    private WavefrontType wavefrontType;

    private WaveMedium waveMedium = new WaveMedium();

    private Wavefront primaryWavefront;
    private Wavefront octaveWavefront;
    private boolean octaveEnabled = false;

    /**
     * @param clock
     */
    public SoundModel() {
        super();
        setWaveMedium( waveMedium );
    }
    
    /**
     *
     */
    public WaveMedium getWaveMedium() {
        return waveMedium;
    }

    public void setWaveMedium( WaveMedium waveMedium ) {
        if( this.waveMedium != null ) {
            removeModelElement( this.waveMedium );
        }
        this.waveMedium = waveMedium;
        addModelElement( waveMedium );
    }

    /**
     *
     */
    public void addWaveFront( Wavefront wavefront ) {
        waveMedium.addWavefront( wavefront );

        // TODO: This should go somewhere else.
        setPropagationSpeed( SoundConfig.PROPOGATION_SPEED );
    }

    public void removeWaveFront( Wavefront wavefront ) {
        waveMedium.removeWavefront( wavefront );
    }

    public WavefrontType getWavefrontType() {
        return wavefrontType;
    }

    //    public void setWavefrontType( WavefrontType wavefrontType ) {
    //        this.wavefrontType = wavefrontType;
    //        for( Iterator iterator = waveMedium.getWavefronts().iterator(); iterator.hasNext(); ) {
    //            Wavefront wavefront = (Wavefront)iterator.next();
    //            wavefront.setWavefrontType( wavefrontType );
    //        }
    //    }

    public void setPropagationSpeed( int propogationSpeed ) {
        for( Iterator iterator = waveMedium.getWavefronts().iterator(); iterator.hasNext(); ) {
            Wavefront wavefront = (Wavefront)iterator.next();
            wavefront.setPropagationSpeed( propogationSpeed );
        }
    }

    /**
     *
     */
    public void setFrequency( double frequency ) {
        primaryWavefront.setFrequency( frequency / SoundConfig.s_frequencyDisplayFactor );
        octaveWavefront.setFrequency( 2 * frequency / SoundConfig.s_frequencyDisplayFactor );
    }

    /**
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

    public Wavefront getOctaveWavefront() {
        return octaveWavefront;
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
