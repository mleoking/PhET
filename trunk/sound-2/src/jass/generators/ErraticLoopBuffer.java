package jass.generators;

import java.net.URL;

/**
 * A force model based on looping through a buffer, loaded from an audio
 * file or provided by caller.  Adds a probablility to "misfire", i.e.,
 * skip a section of the loop.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class ErraticLoopBuffer extends LoopBuffer {
    /**
     * misfire probability per buffer
     */
    private float misfireProb = 0;

    /**
     * Set probability of misfiring per call to computeBuffer
     *
     * @param val probability.
     */
    public void setMisfireProb( float val ) {
        misfireProb = val;
    }

    /**
     * Get probability of misfiring per call to computeBuffer
     *
     * @return probability.
     */
    public float getMisfireProb() {
        return misfireProb;
    }

    /**
     * For derived classes
     *
     * @param bufferSize biffer size
     */
    public ErraticLoopBuffer( int bufferSize ) {
        super( bufferSize ); // this is the internal buffer size
    }

    /**
     * Construct loop force from named file.
     *
     * @param srate      sampling rate in Hertz.
     * @param bufferSize bufferSize of this Out
     * @param fn         Audio file name.
     */
    public ErraticLoopBuffer( float srate, int bufferSize, String fn ) {
        super( srate, bufferSize, fn );
    }


    /**
     * Construct loop force from url
     *
     * @param srate      sampling rate in Hertz.
     * @param bufferSize bufferSize of this Out
     * @param url        Audio file url.
     */
    public ErraticLoopBuffer( float srate, int bufferSize, URL url ) {
        super( srate, bufferSize, url );
    }

    /**
     * Construct loop force and provide buffer
     *
     * @param srate      sampling rate in Hertz.
     * @param bufferSize bufferSize of this Out.
     * @param loopBuffer looping buffer.
     */
    public ErraticLoopBuffer( float srate, int bufferSize, float[] loopBuffer ) {
        super( srate, bufferSize, loopBuffer );
    }

    /**
     * Compute the next buffer.
     * Skip 1/2 loop with probability misfireProb.
     */
    public void computeBuffer() {
        if( Math.random() < misfireProb ) {
            // Skip 1/3 a sample forward
            int bufsz = getBufferSize();
            ix += loopBufferLength / 3;
            if( ix >= loopBufferLength ) {
                ix -= loopBufferLength;
            }
        }
        super.computeBuffer();
    }

}


