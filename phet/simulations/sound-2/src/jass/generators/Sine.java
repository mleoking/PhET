package jass.generators;

import jass.engine.Out;

/**
 * Output sine wave. (Highly inefficient implementation.)
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */

public class Sine extends Out {
    /**
     * Sampling rate in Hertz of Out.
     */
    public float srate;

    /**
     * Amplitude or volume of sine
     */
    protected float volume = 1;

    /**
     * Current phase
     */
    protected float phase = 0;

    /**
     * Freq. in Hertz
     */
    protected float freq = 440;

    public static final float TWOPI = (float)( 2 * Math.PI );

    public Sine( float srate, int bufferSize ) {
        super( bufferSize );
        this.srate = srate;
    }

    /**
     * Set amplitude
     *
     * @param val Volume.
     */
    public void setVolume( float val ) {
        volume = val;
    }

    /**
     * Set frequency
     *
     * @param f frequency.
     */
    public void setFrequency( float f ) {
        freq = f;
    }

    protected void computeBuffer() {
        int bufsz = getBufferSize();
        for( int i = 0; i < bufsz; i++ ) {
            phase += (float)( TWOPI * freq / srate );
            buf[i] = (float)( volume * Math.sin( (double)phase ) );
        }
        while( phase > TWOPI ) {
            phase -= TWOPI;
        }
    }

}

