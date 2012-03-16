// Copyright 2002-2011, University of Colorado
package jass.generators;

/**
 * Reson filter.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */

public class ResonFilter implements Filter {

    static final double cosh( double x ) {
        return ( Math.exp( x ) + Math.exp( -x ) ) / 2;
    }

    static final double sinh( double x ) {
        return ( Math.exp( x ) - Math.exp( -x ) ) / 2;
    }

    /**
     * Sampling rate in Hertz.
     */
    protected float srate;

    /**
     * State of filter.
     */
    private float yt_1, yt_2;

    /**
     * Cached value.
     */
    private float c_i;

    /**
     * The transfer function of a reson filter is H(z) = 1/(1-twoRCosTheta/z + R2/z*z).
     */
    private float R2;

    /**
     * The transfer function of a reson filter is H(z) = 1/(1-twoRCosTheta/z + R2/z*z).
     */
    private float twoRCosTheta;

    /**
     * Reson filter gain.
     */
    private float ampR;

    /**
     * Create and initialize.
     *
     * @param srate sampling rate in Hertz.
     */
    public ResonFilter( float srate ) {
        this.srate = srate;
        reset();
    }

    /**
     * Set the reson coefficients and gain. Cache c_i.
     *
     * @param f resonance frequency in Hertz.
     * @param d damping in radians/s.
     * @param a gain.
     */
    public void setResonCoeff( float f, float d, float a ) {
        float tmp_r = (float)( Math.exp( -d / srate ) );
        R2 = tmp_r * tmp_r;
        if( f > 0 ) {
            twoRCosTheta = (float)( 2 * Math.cos( 2 * Math.PI * f / srate ) * tmp_r );
            c_i = (float)( Math.sin( 2 * Math.PI * f / srate ) * tmp_r );
        }
        else {
            twoRCosTheta = (float)( cosh( 2 * Math.PI * f / srate ) * tmp_r );
            //c_i = (float)(sinh(-2*Math.PI*f/srate)*tmp_r);
            c_i = 1;
        }
        setGain( a );
    }

    /**
     * Set gain.
     *
     * @param a gain.
     */
    public void setGain( float a ) {
        ampR = a * c_i;
    }

    /**
     * Reset state.
     */
    public void reset() {
        yt_1 = yt_2 = 0;
    }

    /**
     * Proces input (may be same as output).
     *
     * @param output      user provided buffer for returned result.
     * @param input       user provided input buffer.
     * @param nsamples    number of samples written to output buffer.
     * @param inputOffset where to start in circular buffer input.
     */
    public void filter( float[] output, float[] input, int nsamples, int inputOffset ) {
        float ynew = 0;
        if( inputOffset == 0 ) {
            for( int k = 0; k < nsamples; k++ ) {
                ynew = twoRCosTheta * yt_1 - R2 * yt_2 + ampR * input[k];
                yt_2 = yt_1;
                yt_1 = ynew;
                output[k] = ynew;
            }
            //            System.out.println(R2+" "+ynew);
        }
        else {
            int inputLen = input.length;
            int ii = inputOffset;
            for( int k = 0; k < nsamples; k++ ) {
                ynew = twoRCosTheta * yt_1 - R2 * yt_2 + ampR * input[ii];
                yt_2 = yt_1;
                yt_1 = ynew;
                output[k] = ynew;
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
