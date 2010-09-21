package jass.generators;

import java.net.URL;

/**
 * Jump through a buffer, loaded from an audio
 * file or provided by caller. No speed or volume control is provided.
 * Sample is divided into segments with segmentation array segs[].
 * seg[k] is start of segment k, except last element of segs[], which denotes
 * the end of the last segment (in order not to run into the artificial buffer end).
 * Playback starts at segment i, then makes transition to setgment k, with transition
 * probability tprob[i][k] (user provided matrix).
 * Generally you must define first the segments, then the transition matrix.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class GranularConstantLoopBuffer extends ConstantLoopBuffer {

    /**
     * Locations of segments in samples. Last entry marks end only
     */
    protected int[] segs;

    /**
     * Transition probabilities. Last segment start not transited to
     */
    protected double[][] tprob;

    /**
     * Current segment index
     */
    protected int currentSegIndex = 0;

    /**
     * array telling you segment index of sample k in loopBuffer[].
     * This includes last unheard segment
     */
    protected int[] isSeg;

    /**
     * Fade-in time for grains
     */
    protected float fadeTime = 0;

    /**
     * If 1 will have linear wavetable playback, 0 total random
     */
    protected float linearBias = 0;

    /**
     * volume
     */
    protected float volume = 1;

    /**
     * Construct from named file.
     *
     * @param srate      sampling rate in Hertz.
     * @param bufferSize bufferSize of this Out
     * @param fn         Audio file name.
     */
    public GranularConstantLoopBuffer( float srate, int bufferSize, String fn ) {
        super( srate, bufferSize, fn );
    }

    /**
     * Construct from url.
     *
     * @param srate      sampling rate in Hertz.
     * @param bufferSize bufferSize of this Out
     * @param url        Audio file url.
     */
    public GranularConstantLoopBuffer( float srate, int bufferSize, URL url ) {
        super( srate, bufferSize, url );
    }

    /**
     * Construct from provided buffer
     *
     * @param srate      sampling rate in Hertz.
     * @param bufferSize bufferSize of this Out.
     * @param loopBuffer looping buffer.
     */
    public GranularConstantLoopBuffer( float srate, int bufferSize, float[] loopBuffer ) {
        super( srate, bufferSize, loopBuffer ); // this is the internal buffer size
    }

    /**
     * Random initialization with n segments (not counting last unused one)
     *
     * @param n          number of segments
     * @param linearBias how likely a linear playback is (1 will give looping, 0 total randomness)
     */
    public void initRandom( int n, float linearBias ) {
        this.linearBias = linearBias;
        setSegments( n );
        setTransitionMatrix();
    }

    /**
     * Set duration of fade-in at the grain boundaries
     * This method only works if the segments are ordered sequentually in time.
     *
     * @param fadeTime fade-in time in seconds
     */
    public void setFadeTime( float dur ) {
        this.fadeTime = dur;
    }

    // modify loopBuffer[] to smooth out segment boundaries
    // assumes segments are ordered in time
    private void makeFadeins() {
        if( fadeTime > 0 ) {
            // smooth beginnings
            int ns = (int)( fadeTime * srate ); // number of samples to smooth
            for( int i = 0; i < segs.length - 1; i++ ) {
                int ibegin = segs[i];
                int iend = segs[i + 1];
                for( int k = 0; k < ns && ibegin + k < iend; k++ ) {
                    float factor = (float)( .5 * ( 1 - Math.cos( 2 * Math.PI * k / ( srate * fadeTime ) ) ) );
                    loopBuffer[ibegin + k] *= factor;
                }
            }
            // smooth endings
            for( int i = 1; i < segs.length; i++ ) {
                int iend = segs[i];
                int ibegin = segs[i - 1];
                for( int k = 0; k < ns && iend - k > ibegin; k++ ) {
                    float factor = (float)( .5 * ( 1 - Math.cos( 2 * Math.PI * k / ( srate * fadeTime ) ) ) );
                    loopBuffer[iend - k] *= factor;
                }
            }
        }
    }

    /**
     * Set probability that InitRandom() generated random transition matrix
     * will favour sequential playback
     *
     * @param val probability for linear order
     */
    public void setLinearBias( float val ) {
        linearBias = val;
    }

    // cache stuff
    private void precomputeSegmentDataStructures() {
        int n = loopBuffer.length;
        isSeg = new int[n];
        for( int i = 0; i < n; i++ ) {
            isSeg[i] = -1;
        }
        // mark beginnings
        for( int k = 0; k < segs.length; k++ ) {
            isSeg[segs[k]] = k;
        }
        // fill rest (skipping unused begin before segment) 0
        for( int i = segs[0]; i < n - 1; i++ ) {
            if( isSeg[i + 1] == -1 ) {
                isSeg[i + 1] = isSeg[i];
            }
        }
        ix = segs[currentSegIndex]; // start here
        makeFadeins();
    }


    /**
     * Define segments as float array of times.
     *
     * @param segs segments. Last segment is unused, but used to make end of prev.
     */
    public void setSegments( float[] segs ) {
        this.segs = new int[segs.length];
        for( int i = 0; i < segs.length; i++ ) {
            this.segs[i] = (int)( srate * segs[i] );
            //System.out.println("seg["+i+"]="+this.segs[i]);
        }
        precomputeSegmentDataStructures();
    }

    /**
     * Define n segments randomly
     *
     * @param n number of segments (not including last one)
     */
    public void setSegments( int n ) {
        float dur = loopBuffer.length / srate;
        float s[] = new float[n + 1];
        for( int i = 0; i < s.length; i++ ) {
            s[i] = (float)( dur * ( i + 1 ) / ( s.length + 1 ) );
        }
        setSegments( s );
    }

    /**
     * Define transition matrix
     *
     * @param t transition matrix of size (segs.length-1)*(segs.length-1)
     */
    public void setTransitionMatrix( double[][] t ) {
        this.tprob = t;
    }

    // make sure all prob. add up to one
    private void normalizeTransitionMatrix() {
        int n = segs.length - 1;
        for( int i = 0; i < n; i++ ) {
            double iToAnywhere = 0;
            for( int k = 0; k < n; k++ ) {
                iToAnywhere += tprob[i][k];
            }
            for( int k = 0; k < n; k++ ) {
                tprob[i][k] /= iToAnywhere;
            }
        }
    }

    // return next segment with appropriate probability, starting from segment i
    private int getNextSegment( int sfrom ) {
        int n = segs.length - 1;
        double r = Math.random();
        double p = 0;
        int sto = 0;
        for( int i = 0; i < n; i++ ) {
            p += tprob[sfrom][i];
            if( r <= p ) {
                sto = i;
                break;
            }
        }
        // must have valid sto now or else probabilities were messed up
        return sto;
    }

    /**
     * Define random transition matrix. Muse have segments defined before use.
     *
     * @param n transition matrix of size (segs.length-1)*(segs.length-1)
     */
    public void setTransitionMatrix() {
        //System.out.println("transm set with bias "+linearBias);
        int n = segs.length - 1;
        double p = 0;
        this.tprob = new double[n][n];
        // first init to linear, i.e., i->k =1 if k==i+1 (cyclically)
        for( int i = 0; i < n - 1; i++ ) {
            tprob[i][i + 1] = 1;
        }
        tprob[n - 1][0] = 1;
        // now add values for non-sequential transitions
        for( int i = 0; i < n; i++ ) {
            for( int k = 0; k < n; k++ ) {
                if( k != i + 1 && !( k == 0 && i == n - 1 ) ) {
                    tprob[i][k] = ( 1 - linearBias ) * Math.random();
                }
            }
        }
        normalizeTransitionMatrix();
    }

    private void incrementIx() {
        ix++;
        if( isSeg[ix] != currentSegIndex ) {
            // transition
            currentSegIndex = getNextSegment( currentSegIndex );
            ix = segs[currentSegIndex];
        }
    }

    /**
     * Set volume
     *
     * @param vol volume
     */
    public void setVolume( float vol ) {
        volume = vol;
    }

    /**
     * Compute the next buffer.
     */
    public void computeBuffer() {
        int bufsz = getBufferSize();
        for( int k = 0; k < bufsz; k++ ) {
            buf[k] = volume * loopBuffer[ix];
            incrementIx();
        }
    }

}


