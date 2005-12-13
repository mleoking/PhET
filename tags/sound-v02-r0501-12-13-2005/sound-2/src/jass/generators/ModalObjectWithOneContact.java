package jass.generators;

import jass.engine.InOut;
import jass.engine.SinkIsFullException;
import jass.engine.Source;

/**
 * Vibration model of object, capable of playing sound.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class ModalObjectWithOneContact extends InOut {
    /**
     * Sampling rate in Hertz.
     */
    public float srate;

    /**
     * Modal data.
     */
    public ModalModel modalModel;

    /**
     * Current barycentric location points.
     */
    private int p1 = 0, p2 = 0, p3 = 0;

    /**
     * Current barycentric coordinates of location.
     */
    private float b1 = 1, b2 = 0, b3 = 0;

    /**
     * State of filters.
     */
    protected float[] yt_1, yt_2;
    private float[] yt_1_secondary, yt_2_secondary;
    /**
     * The transfer function of a reson filter is H(z) = 1/(1-twoRCosTheta/z + R2/z*z).
     */
    protected float[] R2;

    /**
     * The transfer function of a reson filter is H(z) = 1/(1-twoRCosTheta/z + R2/z*z).
     */
    protected float[] twoRCosTheta;

    /**
     * Reson filter gain.
     */
    protected float[] ampR;

    /**
     * Cached values.
     */
    protected float[] c_i;

    /**
     * Temp storage
     */
    protected float[] tmpBuf = null;

    /**
     * Add a Source. Overrides Sink interface implementation from
     * InOut. Allow only one Source.
     *
     * @param s Source to add.
     */
    public Object addSource( Source s ) throws SinkIsFullException {
        if( sourceContainer.size() > 0 ) {
            throw new SinkIsFullException();
        }
        else {
            sourceContainer.addElement( s );
        }
        return null;
    }

    /**
     * Scale dampings.
     *
     * @param d damping scale.
     */
    public void setDamping( float dscale ) {
        modalModel.dscale = dscale;
        computeFilter();
    }


    /**
     * Scale gains.
     *
     * @param a gain scale.
     */
    public void setGain( float a ) {
        modalModel.ascale = a;
        computeFilter();
    }

    /**
     * Scale frequencies.
     *
     * @param fscale frequency scale.
     */
    public void setFrequencyScale( float fscale ) {
        modalModel.fscale = fscale;
        computeFilter();
    }

    /**
     * Constructor for derived classes to call super
     *
     * @param bufferSize Buffer size used for real-time rendering.
     */
    public ModalObjectWithOneContact( int bufferSize ) {
        super( bufferSize );
    }

    /**
     * Create and initialize, but don't set any modal parameters.
     *
     * @param srate      sampling rate in Hertz.
     * @param nf         number of modes.
     * @param np         number of locations.
     * @param bufferSize Buffer size used for real-time rendering.
     */
    public ModalObjectWithOneContact( float srate, int nf, int np, int bufferSize ) {
        super( bufferSize );
        this.srate = srate;
        modalModel = new ModalModel( nf, np );
        allocate( nf, np );
        tmpBuf = new float[bufferSize];
    }

    /**
     * Create and initialize with provided modal data.
     *
     * @param m          modal model to load.
     * @param srate      sampling rate in Hertz.
     * @param bufferSize Buffer size used for real-time rendering.
     */
    public ModalObjectWithOneContact( ModalModel m, float srate, int bufferSize ) {
        super( bufferSize );
        this.srate = srate;
        modalModel = m;
        allocate( modalModel.nf, modalModel.np );
        computeFilter();
        tmpBuf = new float[bufferSize];
    }

    /**
     * Reduce number of modes used.
     *
     * @param nf number of modes to use.
     */
    public void setNf( int nf ) {
        if( nf < modalModel.nf ) {
            modalModel.nfUsed = nf;
        }
    }

    /**
     * Allocate data.
     *
     * @param nf number of modes.
     * @param np number of locations.
     */
    protected void allocate( int nf, int np ) {
        R2 = new float[nf];
        twoRCosTheta = new float[nf];
        ampR = new float[nf];
        yt_1 = new float[nf];
        yt_2 = new float[nf];
        yt_1_secondary = new float[nf];
        yt_2_secondary = new float[nf];
        c_i = new float[nf];
        clearHistory();
    }

    /**
     * Compute the filter coefficients used for real-time rendering
     * from the modal model parameters.
     */
    public void computeFilter() {
        computeResonCoeff();
        computeLocation();
    }

    /**
     * Compute the reson coefficients from the modal model parameters.
     * Cache values used in {@link #setLocation}.
     */
    public void computeResonCoeff() {
        for( int i = 0; i < modalModel.nf; i++ ) {
            float tmp_r = (float)( Math.exp( -modalModel.dscale * modalModel.d[i] / srate ) );
            R2[i] = tmp_r * tmp_r;
            twoRCosTheta[i] = (float)( 2 * Math.cos( 2 * Math.PI * modalModel.fscale *
                                                     modalModel.f[i] / srate ) * tmp_r );
            c_i[i] = (float)( Math.sin( 2 * Math.PI * modalModel.fscale *
                                        modalModel.f[i] / srate ) * tmp_r );
        }
    }

    /**
     * Compute gains. Check also if any frequency is above Nyquist rate.
     * If so set its gain to zero.
     */
    private void computeLocation() {
        for( int i = 0; i < modalModel.nf; i++ ) {
            if( modalModel.fscale * modalModel.f[i] < srate / 2 ) {
                ampR[i] = modalModel.ascale * c_i[i] *
                          ( b1 * modalModel.a[p1][i] + b2 * modalModel.a[p2][i] +
                            b3 * modalModel.a[p3][i] );
            }
            else {
                ampR[i] = 0; // kill stuff above nyquist rate
            }
        }
    }

    /**
     * Compute the gain coefficients  from the modal model parameters at point p,
     * given inside triangle of point p1,p2,p3, with barycentric coordinated b1,b2,b3
     *
     * @param p1 location index 1.
     * @param p2 location index 2.
     * @param p3 location index 3.
     * @param b1 barycentric coordinate 1.
     * @param b2 barycentric coordinate 2.
     * @param b3 barycentric coordinate 3.
     */
    public void setLocation( int p1, int p2, int p3, float b1, float b2, float b3 ) {
        this.p1 = p1;
        this.p2 = p2;
        this.p3 = p3;
        this.b1 = b1;
        this.b2 = b2;
        this.b3 = b3;
        computeLocation();
    }

    /**
     * Set state to non-vibrating.
     */
    public void clearHistory() {
        for( int i = 0; i < modalModel.nf; i++ ) {
            yt_1[i] = yt_2[i] = 0;
            yt_1_secondary[i] = yt_2_secondary[i] = 0;
        }
    }

    protected float rollGain = .0f; // mix ratio of roll comp

    /**
     * Compute the next buffer and store in member float[] buf.
     */
    protected void computeBuffer() {
        computeModalFilterBank( this.buf, srcBuffers[0], getBufferSize() );
    }

    // 0.001 works to prevent strange underflow (??) bug
    static final float eps = 0.001f;

    /**
     * Apply external force[] and compute response through bank of modal filters.
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

    /**
     * Apply external force[] and compute response through bank of modal filters.
     * Has different state.
     *
     * @param output   user provided output buffer.
     * @param force    input force.
     * @param nsamples number of samples to compute.
     */
    private void computeModalFilterBank_2( float[] output, float[] force,
                                           int nsamples ) {
        for( int k = 0; k < nsamples; k++ ) {
            output[k] = 0;
        }
        int nf = modalModel.nfUsed;
        for( int i = 0; i < nf; i++ ) {
            float tmp_twoRCosTheta = twoRCosTheta[i];
            float tmp_R2 = R2[i];
            float tmp_a = ampR[i];
            float tmp_yt_1 = yt_1_secondary[i];
            float tmp_yt_2 = yt_2_secondary[i];
            for( int k = 0; k < nsamples; k++ ) {
                float ynew = tmp_twoRCosTheta * tmp_yt_1 -
                             tmp_R2 * tmp_yt_2 + tmp_a * force[k];
                // commenting out the force[k] changes performance 650->718
                tmp_yt_2 = tmp_yt_1;
                tmp_yt_1 = ynew;
                output[k] += ynew;
            }
            yt_1_secondary[i] = tmp_yt_1;
            yt_2_secondary[i] = tmp_yt_2;
        }
    }

}
