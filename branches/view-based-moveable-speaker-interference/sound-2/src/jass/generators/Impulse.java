package jass.generators;

import jass.engine.Out;

/**
 * Generate impulses with time intervals.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class Impulse extends Out {

    /**
     * time interval
     */
    protected float t_pulse = 1.0f;

    // dt in samples
    private int dt_samples;

    // current time
    private long sampleTime = 0;

    // last sample time impulse was generated
    private long lastImpulseTime = 0;

    public float srate;

    protected float volume = 1;

    /**
     * Construct
     *
     * @param srate      sampling rate in Hertz.
     * @param bufferSize bufferSize of this Out
     */
    public Impulse( float srate, int bufferSize ) {
        super( bufferSize );
        this.srate = srate;
        reset();
    }

    public void reset() {
        sampleTime = 0;
        lastImpulseTime = 0;
    }

    /**
     * Set magnitude.
     *
     * @param val Volume.
     */
    public void setVolume( float val ) {
        volume = val;
    }

    /**
     * Set impulse period
     *
     * @param dt period
     */
    public void setPeriod( float dt ) {
        t_pulse = dt;
        dt_samples = (int)( t_pulse * srate );
    }

    /**
     * Compute the next buffer.
     */
    public void computeBuffer() {
        int bufsz = getBufferSize();
        for( int k = 0; k < bufsz; k++ ) {
            sampleTime++;
            if( sampleTime - lastImpulseTime > dt_samples ) {
                lastImpulseTime = sampleTime;
                buf[k] = volume;
            }
            else {
                buf[k] = 0;
            }
        }
    }

}


