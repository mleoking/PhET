package jass.generators;

import jass.engine.FilterUG;

/**
 * Delay line. H(z) = z^_{L}
 * y(t) = x(t-L)
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */

public class Delay extends FilterUG {

    protected float srate;
    protected int rawDelay; // Delay in computeBuffer()
    protected float[] cbuf; // circular buffer of size bufferSize + rawDelay
    protected int cbuf_head; // Head of circular buffer cbuf

    /**
     * Create and initialize.
     *
     * @param bufferSize Buffer size used for real-time rendering.
     * @param srate      sampling rate in Hertz.
     */
    public Delay( int bufferSize, float srate ) {
        super( bufferSize );
        this.srate = srate;
    }

    /**
     * Create. For derived classes.
     *
     * @param bufferSize Buffer size used for real-time rendering.
     */
    public Delay( int bufferSize ) {
        super( bufferSize );
    }

    /**
     * Init and allocate.
     */
    protected void init() {
        int n = rawDelay + getBufferSize();
        cbuf = new float[n];
        for( int i = 0; i < n; i++ ) {
            cbuf[i] = 0;
        }
        cbuf_head = 0;
    }

    /**
     * Set raw delay. This does not take the additional delay in closed
     * loops into account.
     * Also allocates buffers and inits the state.
     *
     * @param del delay in seconds
     */
    public void setRawDelay( float del ) {
        rawDelay = (int)( srate * del );
        init();
    }

    /**
     * Get raw delay. This does not take the additional delay in closed
     * loops into account.
     *
     * @return raw delay in seconds
     */
    public float getRawDelay() {
        return rawDelay / srate;
    }

    /**
     * Set recursive delay. This does take the additional delay in closed
     * loops into account. Delay can not be smaller than the bufferSize.
     * Also allocates buffers and inits the state.
     *
     * @param del delay in seconds.
     */
    public void setRecursiveDelay( float del ) throws IllegalArgumentException {
        rawDelay = (int)( srate * del ) - getBufferSize();
        if( rawDelay <= 0 ) {
            rawDelay = 0;
            throw new IllegalArgumentException();
        }
        init();
    }

    /**
     * Get recursive delay. This does take the additional delay in closed
     * loops into account. Delay can not be smaller than the bufferSize.
     *
     * @return delay in seconds.
     */
    public float getRecursiveDelay() {
        return ( rawDelay + getBufferSize() ) / srate;
    }

    /**
     * Compute the next buffer and store in member float[] buf.
     * Insert srcBuffers[0][] into queue at head and move head forward.
     * Then copy new head into buf[]
     */
    protected void computeBuffer() {
        int n = getBufferSize();
        int totlen = n + rawDelay;
        float[] srcBuf = srcBuffers[0];
        // insert srcBuf
        for( int i = 0, k = cbuf_head; i < n; i++ ) {
            cbuf[k] = srcBuf[i];
            k++;
            if( k == totlen ) {
                k = 0;
            }
        }
        cbuf_head += n;
        if( cbuf_head >= totlen ) {
            cbuf_head -= totlen;
        }
        // get buf[]
        for( int i = 0, k = cbuf_head; i < n; i++ ) {
            buf[i] = cbuf[k];
            k++;
            if( k == totlen ) {
                k = 0;
            }
        }
    }
}
