package jass.generators;

import jass.engine.InOut;

/**
 * Mixer UG.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class Mixer extends InOut {
    // gains of sources
    protected float[] gains;

    protected float[] tmp_buf; // scratchpad

    /**
     * Set input gain control vector.
     *
     * @param k     index of gain
     * @param gains input gain
     */
    public void setGain( int k, float g ) {
        if( k < 0 || k >= gains.length ) {
            return;
        }
        else {
            gains[k] = g;
        }
    }

    /**
     * Get input gain control vector.
     *
     * @param gains input gains
     */
    public float[] getGains() {
        return gains;
    }

    /**
     * Clear gains to zero
     */
    public void clear() {
        for( int i = 0; i < gains.length; i++ ) {
            gains[i] = 0;
        }
    }

    /**
     * Create
     *
     * @param bufferSize Buffer size used for real-time rendering.
     * @param n          no inputs
     */
    public Mixer( int bufferSize, int n ) {
        super( bufferSize );
        gains = new float[n];
        clear();
        tmp_buf = new float[bufferSize];
    }

    /**
     * Create. For superclasses
     *
     * @param bufferSize Buffer size used for real-time rendering.
     */
    public Mixer( int bufferSize ) {
        super( bufferSize );
    }

    /**
     * Compute the next buffer and store in member float[] buf.
     */
    protected void computeBuffer() {
        int bufsz = getBufferSize();
        int nsrc = sourceContainer.size();
        if( nsrc > gains.length ) {
            nsrc = gains.length;
            System.out.println( "Warning: Mixer has more sources than allowed" );
        }
        for( int k = 0; k < bufsz; k++ ) {
            // can't overwrite buf[] yet as one of the srcBuffers may be pointing to it!
            tmp_buf[k] = 0;
        }
        for( int i = 0; i < nsrc; i++ ) {
            float[] tmpsrc = srcBuffers[i];
            //System.out.println("i= "+i+ "src[] = " + srcBuffers[i][5]);
            float g = gains[i];
            for( int k = 0; k < bufsz; k++ ) {
                tmp_buf[k] += g * tmpsrc[k];
            }
        }
        for( int k = 0; k < bufsz; k++ ) {
            buf[k] = tmp_buf[k];
        }
    }
}
