package jass.generators;


/**
 * Vibration model of object, capable of playing sound.
 * Extended to keep track of current excitation and capable of modes being turned
 * on/off.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class QuenchableModalObjectWithOneContact extends ModalObjectWithOneContact {
    /**
     * True for the modes that are on
     */
    protected boolean[] onBit;

    /**
     * Create and initialize, but don't set any modal parameters.
     *
     * @param srate      sampling rate in Hertz.
     * @param nf         number of modes.
     * @param np         number of locations.
     * @param bufferSize Buffer size used for real-time rendering.
     */
    public QuenchableModalObjectWithOneContact( float srate, int nf, int np, int bufferSize ) {
        super( srate, nf, np, bufferSize );
        allocateonBit( nf );
    }

    /**
     * Create and initialize with provided modal data.
     *
     * @param m          modal model to load.
     * @param srate      sampling rate in Hertz.
     * @param bufferSize Buffer size used for real-time rendering.
     */
    public QuenchableModalObjectWithOneContact( ModalModel m, float srate, int bufferSize ) {
        super( m, srate, bufferSize );
        allocateonBit( m.nf );
    }

    /**
     * Allocate and turn on modes
     */
    private void allocateonBit( int nf ) {
        onBit = new boolean[nf];
        for( int i = 0; i < nf; i++ ) {
            onBit[i] = true;
        }
    }

    /**
     * Turn mode on/off.
     *
     * @param mode mode number
     * @param on   true if turn on, fals if turn off
     */
    public void setOnBit( int mode, boolean on ) {
        if( on ) {
            onBit[mode] = true;
        }
        else {
            if( onBit[mode] ) {
                onBit[mode] = false;
                yt_1[mode] = yt_2[mode] = 0;
            } // else was off already
        }
    }

    /**
     * Return current excitation of mode. Approximated as (y(t)^2 + (dy/dt)^2/(2pif)^2)/2).
     * A pulse excitation of strength x  should produce excitation of (ax)^2/2, with "a"
     * gain of mode.
     *
     * @param k mode number
     * @return excitation
     */
    public float getModeExcitation( int k ) {
        double y2term = yt_1[k] * yt_1[k];
        double dy2term = ( yt_1[k] - yt_2[k] ) * srate / ( 6.2831853 * modalModel.fscale * modalModel.f[k] );
        dy2term *= dy2term;
        return (float)( ( y2term + dy2term ) / 2 );
    }


    /**
     * Apply external force[] and compute response through bank of modal filters.
     * As in super class but skip off modes.
     *
     * @param output   user provided output buffer.
     * @param force    input force.
     * @param nsamples number of samples to compute.
     */
    protected void computeModalFilterBank( float[] output, float[] force, int nsamples ) {
        boolean isnul = true;
        for( int k = 0; k < nsamples; k++ ) {
            output[k] = 0;
            if( Math.abs( force[k] ) >= eps ) {
                isnul = false;
            }
        }
        int nf = modalModel.nfUsed;
        if( isnul ) {
            for( int i = 0; i < nf; i++ ) {
                if( Math.abs( yt_1[i] ) >= eps || Math.abs( yt_2[i] ) >= eps ) {
                    isnul = false;
                    break;
                }
            }
        }
        if( isnul ) {
            return;
        }

        for( int i = 0; i < nf; i++ ) {
            if( onBit[i] ) {
                float tmp_twoRCosTheta = twoRCosTheta[i];
                float tmp_R2 = R2[i];
                float tmp_a = ampR[i];
                float tmp_yt_1 = yt_1[i];
                float tmp_yt_2 = yt_2[i];
                for( int k = 0; k < nsamples; k++ ) {
                    float ynew = tmp_twoRCosTheta * tmp_yt_1 -
                                 tmp_R2 * tmp_yt_2 + tmp_a * force[k];
                    // commenting out the force[k] changes performance 650->718
                    tmp_yt_2 = tmp_yt_1;
                    tmp_yt_1 = ynew;
                    output[k] += ynew;
                }
                yt_1[i] = tmp_yt_1;
                yt_2[i] = tmp_yt_2;
            }
        }
    }


}
