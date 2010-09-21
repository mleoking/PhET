/**
 * Class: Wavefront
 * Package: edu.colorado.phet.sound.model
 * Author: Another Guy
 * Date: Aug 3, 2004
 */
package edu.colorado.phet.sound.model;

import java.awt.geom.Point2D;

import edu.colorado.phet.common.phetcommon.util.SimpleObservable;
import edu.colorado.phet.sound.SoundConfig;

public class Wavefront extends SimpleObservable /*implements ModelElement*/ {

    private WaveFunction waveFunction;
    private WavefrontType wavefrontType = new SphericalWavefront();

    private double maxAmplitude = 0.5;
    private double frequency = 25.0;
    private double[] amplitude = new double[s_length];

    // Tracks the frequency and max amplitude at which each entry
    // in the amplitude array was generated
    private double[] frequencyAtTime = new double[s_length];
    private double[] maxAmplitudeAtTime = new double[s_length];

    // Tracks the previous values for frequency and max amplitude so
    // we can tell if the listener's oscillator needs to be updated
    private double[] prevFrequencyAtTime = new double[s_length];
    private double[] prevMaxAmplitudeAtTime = new double[s_length];

    private float time = 0;
    private int propagationSpeed = 1;
    // "Enabled" means that the wavefront should be added into the
    // wave medium. The waveform runs continuously whether it is enabled
    // or not, so it does not get out of phase from when it was started.
    private boolean enabled = true;
    private Point2D.Double origin;

    public Point2D.Double getOrigin() {
        return origin;
    }

    public void setOrigin( Point2D.Double origin ) {
        this.origin = origin;
    }

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
    public void setWaveFunction( WaveFunction waveFunction ) {
        this.waveFunction = waveFunction;
    }

    /**
     * Moves the wave front forward through time
     */
    public void stepInTime( double dt, AttenuationFunction attenuationFunction ) {

        time += dt;
        int stepSize = propagationSpeed;

        // Move the existing elements of the wavefront up in the array of amplitudes
        for( int i = s_length - 1; i > stepSize - 1; i-- ) {

            // Has the amplitude changed for the listener since the last time step?
            if( ( i - stepSize ) == listenerLocation && maxAmplitudeAtTime[i] != prevMaxAmplitudeAtTime[i] ) {
                notifyObservers();
            }
            prevMaxAmplitudeAtTime[i] = maxAmplitudeAtTime[i];
            amplitude[i] = amplitude[i - stepSize];

            // Amplitude must be adjusted for distance from source, and attenuation due to
            // the density of the wave medium
            amplitude[i] = wavefrontType.computeAmplitudeAtDistance( this, amplitude[i], (float)i )
                           * attenuationFunction.getAttenuation( i * this.propagationSpeed, 0 );

            // Has the frequency changed for the listener since the last time step?
            if( ( i - stepSize ) == listenerLocation && frequencyAtTime[i] != prevFrequencyAtTime[i] ) {
                notifyObservers();
            }
            prevFrequencyAtTime[i] = frequencyAtTime[i];
            frequencyAtTime[i] = frequencyAtTime[i - stepSize];
            maxAmplitudeAtTime[i] = maxAmplitudeAtTime[i - stepSize];
            maxAmplitudeAtTime[i] = wavefrontType.computeAmplitudeAtDistance( this, maxAmplitudeAtTime[i], i )
                                    * attenuationFunction.getAttenuation( i * this.propagationSpeed, 0 );

            if( maxAmplitudeAtTime[i] < 0 ) {
                throw new RuntimeException( "Negative amplitude" );
            }
        }

        // Generate the new element(s) of the wavefront
        double a = waveFunction.waveAmplitude( time );
        for( int i = 0; i < stepSize; i++ ) {
            amplitude[i] = a;
            if( frequencyAtTime[i] != frequency ) {
                frequencyAtTime[i] = frequency;
            }
            if( maxAmplitudeAtTime[i] != maxAmplitude ) {
                maxAmplitudeAtTime[i] = maxAmplitude;
            }
        }
    }

    public void setPropagationSpeed( int propagationSpeed ) {
        this.propagationSpeed = propagationSpeed;
    }

    protected float getTime() {
        return time;
    }

    public double[] getAmplitude() {
        return amplitude;
    }

    public double getMaxAmplitude() {
        return maxAmplitude;
    }

    public void setMaxAmplitude( double maxAmplitude ) {
        this.maxAmplitude = maxAmplitude;
        notifyObservers();
    }

    public double getFrequency() {
        return frequency;
    }

    public void setFrequency( double frequency ) {
        this.frequency = frequency;
        notifyObservers();
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
     * @return todo: rename this to reflect that it is at a distance, not a time
     */
    public double getFrequencyAtTime( int frequencyIdx ) {
        frequencyIdx = Math.max( 0, Math.min( s_length - 1, frequencyIdx ) );
        return frequencyAtTime[frequencyIdx];
    }

    /**
     *
     */
    public double getMaxAmplitudeAtTime( int maxAmplitudeIdx ) {
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
    public double getWavelengthAtTime( int t ) {
        double lambda = propagationSpeed / ( SoundConfig.s_timeStep * getFrequencyAtTime( t ) );

        // I'm sorry to say I'm not sure just why 6.2 is the right factor here, but
        // it works
        return lambda * 6.2;
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
        for( int i = 0; i < s_length; i++ ) {
            this.amplitude[i] = 0;
            this.frequencyAtTime[i] = 0;
            this.maxAmplitudeAtTime[i] = 0;
        }
        this.notifyObservers();
    }

    private int listenerLocation;

    public void setListenerLocation( int x ) {
        listenerLocation = x;
    }


    //
    // Static fields and methods
    //
    public static int s_length = 400;
}
