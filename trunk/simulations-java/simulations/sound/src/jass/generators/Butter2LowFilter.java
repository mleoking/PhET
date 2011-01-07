// Copyright 2002-2011, University of Colorado
package jass.generators;

/**
 * Second order Butterworth lowpass filter.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */

public class Butter2LowFilter implements Filter {

    /**
     * Sampling rate in Hertz.
     */
    protected float srate;

    /**
     * State of filter.
     */
    private float yt_1, yt_2, xt_1, xt_2;

    /**
     * Cutoff frequency in Hertz.
     */
    private float f;

    /**
     * Coefficients (Steiglitz notation).
     */
    private float cc, dd;

    /**
     * Gain. A0 is normalized. A is actual gain, gain is user settable
     */
    private float A = 1.f, A0 = 1.f, gain = 1;

    /**
     * Create and initialize.
     *
     * @param srate sampling rate in Hertz.
     */
    public Butter2LowFilter( float srate ) {
        this.srate = srate;
        reset();
    }

    /**
     * Set the filter cutoff.
     *
     * @param f cutoff frequency in Hertz.
     */
    public synchronized void setCutoffFrequency( float f ) {
        float tt = (float)( Math.sqrt( 2 ) / Math.tan( 2 * Math.PI * f / srate ) );
        float tp = tt + 1, tm = tt - 1;
        float den = tp * tp + 1;
        float re = ( tm * tp - 1 ) / den;
        float im = 2 * tt / den;
        cc = -2 * re;
        dd = re * re + im * im;
        A0 = ( 1 + cc + dd ) / 4;
        A = gain * A0;
    }

    /**
     * Set gain.
     *
     * @param gain gain.
     */
    public synchronized void setGain( float gain ) {
        A = gain * A0;
        this.gain = gain;
    }

    /**
     * Reset state.
     */
    public synchronized void reset() {
        yt_1 = yt_2 = xt_1 = xt_2 = 0;
    }

    /**
     * Proces input (may be same as output).
     *
     * @param output      user provided buffer for returned result.
     * @param input       user provided input buffer.
     * @param nsamples    number of samples written to output buffer.
     * @param inputOffset where to start in circular buffer input.
     */
    public synchronized void filter( float[] output, float[] input, int nsamples, int inputOffset ) {
        if( inputOffset == 0 ) {
            for( int k = 0; k < nsamples; k++ ) {
                float ynew = input[k] + 2 * xt_1 + xt_2 - cc * yt_1 - dd * yt_2;
                yt_2 = yt_1;
                yt_1 = ynew;
                xt_2 = xt_1;
                xt_1 = input[k];
                output[k] = A * ynew;
            }
        }
        else {
            int inputLen = input.length;
            int ii = inputOffset;
            for( int k = 0; k < nsamples; k++ ) {
                float ynew = input[ii] + 2 * xt_1 + xt_2 - cc * yt_1 - dd * yt_2;
                yt_2 = yt_1;
                yt_1 = ynew;
                xt_2 = xt_1;
                xt_1 = input[ii];
                output[k] = A * ynew;
                if( ii == inputLen - 1 ) {
                    ii = 0;
                }
                else {
                    ii++;
                }
            }
        }
    }

}
