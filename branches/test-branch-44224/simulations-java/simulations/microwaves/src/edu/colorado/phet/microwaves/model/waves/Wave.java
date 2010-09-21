/**
 * Class: Wave
 * Package: edu.colorado.phet.microwave.model
 * Author: Another Guy
 * Date: May 23, 2003
 */
package edu.colorado.phet.microwaves.model.waves;

import java.util.Observable;

import edu.colorado.phet.common.phetcommon.model.ModelElement;

/**
 * <p/>
 * TODO: Make this 2D. It is currently pnly 1D
 */
public class Wave extends Observable implements ModelElement {

    private PeriodicFunction waveFunction;
    private WavefrontType wavefrontType;

    private float maxAmplitude;
    private float frequency;
    private float[] amplitude = new float[s_length];

    // Tracks the frequency and max amplitude at which each entry
    // in the amplitude array was generated
    private float[] frequencyAtTime = new float[s_length];
    private float[] maxAmplitudeAtTime = new float[s_length];

    // Tracks the previous values for frequency and max amplitude so
    // we can tell if the listener's oscillator needs to be updated
    private float[] prevFrequencyAtTime = new float[s_length];
    private float[] prevMaxAmplitudeAtTime = new float[s_length];

    private float time = 0;
    private int propagationSpeed = 10;
    // "Enabled" means that the wavefront should be added into the
    // wave medium. The waveform runs continuously whether it is enabled
    // or not, so it does not get out of phase from when it was started.
    private boolean enabled = true;

    /**
     * @param wavefrontType
     */
    public Wave( WavefrontType wavefrontType,
                 PeriodicFunction waveFunction,
                 float frequency,
                 float maxAmplitude ) {
        this.wavefrontType = wavefrontType;
        this.waveFunction = waveFunction;
        this.frequency = frequency;
        this.maxAmplitude = maxAmplitude;
    }

    /**
     * @return
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * @param enabled
     */
    public void setEnabled( boolean enabled ) {
        this.enabled = enabled;
    }

    /**
     * @param waveFunction
     */
    public void setWaveFunction( PeriodicFunction waveFunction ) {
        this.waveFunction = waveFunction;
    }

    /**
     * Moves the wave front forward through time
     */
    public void stepInTime( double dt ) {

        time += dt;
        int stepSize = propagationSpeed;

        // Move the existing elements of the wavefront up in the array of amplitudes
        for ( int i = s_length - 1; i > stepSize - 1; i-- ) {

            prevMaxAmplitudeAtTime[i] = maxAmplitudeAtTime[i];
            amplitude[i] = amplitude[i - stepSize];

            // Amplitude must be adjusted for distance from source
            amplitude[i] = wavefrontType.computeAmplitudeAtDistance( this, amplitude[i], (float) i );

            prevFrequencyAtTime[i] = frequencyAtTime[i];
            frequencyAtTime[i] = frequencyAtTime[i - stepSize];
            maxAmplitudeAtTime[i] = maxAmplitudeAtTime[i - stepSize];
            maxAmplitudeAtTime[i] = wavefrontType.computeAmplitudeAtDistance( this, maxAmplitudeAtTime[i], (float) i );
        }

        // Generate the new element(s) of the wavefront
        for ( int i = 0; i < stepSize; i++ ) {
            amplitude[i] = waveFunction.valueAtTime( this.frequency, this.maxAmplitude, time );
            if ( frequencyAtTime[i] != frequency ) {
                frequencyAtTime[i] = frequency;
            }
            if ( maxAmplitudeAtTime[i] != maxAmplitude ) {
                maxAmplitudeAtTime[i] = maxAmplitude;
            }
        }
        this.setChanged();
        this.notifyObservers();
    }

    public void setPropagationSpeed( int propagationSpeed ) {
        this.propagationSpeed = propagationSpeed;
    }

    protected float getTime() {
        return time;
    }

    public float[] getAmplitude() {
        return amplitude;
    }

    public float getMaxAmplitude() {
        return maxAmplitude;
    }

    public void setMaxAmplitude( float maxAmplitude ) {
        this.maxAmplitude = maxAmplitude;
        this.setChanged();
        this.notifyObservers();
    }

    public float getFrequency() {
        return frequency;
    }

    public void setFrequency( float newFrequency ) {

        // This computation attempts to keep things in phase when
        // the frequency changes,
        if ( newFrequency != 0 ) {
            double phi = time * ( ( frequency / newFrequency ) - 1 );
            time += (float) phi;
        }
        this.frequency = newFrequency;
        this.setChanged();
        this.notifyObservers();
    }


    /**
     * Returns the frequency at which the amplitude at a particular index
     * was generated. This enables a client to get the frequency at some
     * point in the wave train other than what is being generated right now.
     * <p/>
     * This method protects against attempted references outside the range of
     * legitimate indexes.
     *
     * @param frequencyIdx
     * @return
     */
    public float getFrequencyAtTime( int frequencyIdx ) {
        frequencyIdx = Math.max( 0, Math.min( s_length - 1, frequencyIdx ) );
        return frequencyAtTime[frequencyIdx];
    }

    /**
     *
     */
    public float getMaxAmplitudeAtTime( int maxAmplitudeIdx ) {
        maxAmplitudeIdx = Math.max( 0, Math.min( s_length - 1, maxAmplitudeIdx ) );
        return maxAmplitudeAtTime[maxAmplitudeIdx];
    }

    /**
     *
     */
    public int getPropagationSpeed() {
        return propagationSpeed;
    }

    /**
     * @return
     */
    public float getWavelengthAtTime( int t, float dt ) {
        float lambda = propagationSpeed / ( dt * getFrequencyAtTime( t ) );

        // I'm sorry to say I'm not sure just why 6.2 is the right factor here, but
        // it works
        return lambda * 6.2f;
    }

    /**
     *
     */
    public void setWavefrontType( WavefrontType wavefrontType ) {
        this.wavefrontType = wavefrontType;

        // Clear the values in the amplitude array
        this.clear();
    }

    public void clear() {
        for ( int i = 0; i < s_length; i++ ) {
            this.amplitude[i] = 0;
            this.frequencyAtTime[i] = 0;
            this.maxAmplitudeAtTime[i] = 0;
        }
        this.notifyObservers();
    }


    //
    // Static fields and methods
    //
    public static int s_length = 1000;

}
