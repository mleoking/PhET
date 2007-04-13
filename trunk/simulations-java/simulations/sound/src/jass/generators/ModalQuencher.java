package jass.generators;

import jass.engine.InOut;
import jass.engine.Out;

import java.util.Vector;

/**
 * UG that maintains a list of sources and QuenchableModalObjectWithOneContact's and
 * estimates excitations and turns off inaudible modes according to a masking analysis.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class ModalQuencher extends InOut {

    /**
     * This is the level in dB SPL of loudest mode (as hear by listener)
     * Should be estimated with a mike, if possible. Here we just assume 60 dB
     * as a "reference" level.
     */
    protected float dbLevelLoudestMode = 60;

    /**
     * Masking curve offset (how high about masker)
     */
    protected float av = 20;

    /**
     * maximum excitation occurred
     */
    protected float maximumExdBThatOccurred = -100000;

    /**
     * Sampling rate in Hertz.
     */
    public float srate;

    /**
     * Registered QuenchableModalObjectWithOneContact's
     */
    private Vector modalObjectsContainer;

    /**
     * Registered QuenchableModalObjectWithOneContact's
     */
    protected QuenchableModalObjectWithOneContact[] mobs;

    /**
     * Estimated excitations of inputs to the above QuenchableModalObjectWithOneContact
     */
    protected float[] sourceExcitations;

    /**
     * Modal data array
     */
    protected ModeData[] modeData;

    /**
     * How many frames to skip before quenching again
     */
    protected int nFramesToSkip = 1;

    private int nMobs = 0;

    /**
     * For monitoring purpose
     */
    protected int nKilledModes = 0;
    protected int nTotalModes = 0;

    /**
     * @return number of modes that where pruned
     */
    public int getNKilledModes() {
        return nKilledModes;
    }

    /**
     * @return total number of modes
     */
    public int getTotalModes() {
        return nTotalModes;
    }

    /**
     * Constructor.
     *
     * @param bufferSize    internal buffersize
     * @param nFramesToSkip how many frames to skip before quenching again
     */
    public ModalQuencher( int bufferSize, int nFramesToSkip ) {
        super( bufferSize );
        setFramesToSkip( nFramesToSkip );
        modalObjectsContainer = new Vector();
    }

    /**
     * To be called after all sources and mobs have been added
     */
    public void init() {
        mobs = new QuenchableModalObjectWithOneContact[nMobs];
        for( int i = 0; i < nMobs; i++ ) {
            mobs[i] = (QuenchableModalObjectWithOneContact)modalObjectsContainer.get( i );
        }
        makeModeDataStructure();
    }

    /**
     * Set masking curve height.
     *
     * @param val offset in dB (20 is safe, 1 is agressive)
     */
    public void setAv( float val ) {
        av = val;
    }

    /**
     * Set level in dB SPL at observer of loudest mode. This will be the loudest value during a run.
     *
     * @param val loudest mode at loudest moment in dB
     */
    public void setDbLevelLoudestMode( float val ) {
        dbLevelLoudestMode = val;
    }

    /**
     * Reset level
     */
    public void resetLevel() {
        maximumExdBThatOccurred = 0;
    }

    /**
     * How many frames (buffers) to skip before recomputing modal quenching
     *
     * @param n nFramesToSkip
     */
    public void setFramesToSkip( int n ) {
        nFramesToSkip = n;

    }

    /**
     * When this is called, all sources have already been queried (available through
     * getSources(). Estimate excitations from sources for each
     * QuenchableModalObjectWithOneContact, enter them in ModeData[], then
     * turn on/off appropriate modes.
     */
    protected void computeBuffer() {
        if( getTime() % nFramesToSkip == 0 ) {
            doQuenching();
        }
    }

    /**
     * Sort estimate excitations, sort ModeData[], quench
     */
    protected void doQuenching() {
        estimateExcitations();
        sortModeData();
        quench();
    }

    // estimate levels of inputs
    private void getSourceLevels() {
        Object[] arrayOfSources = getSources();
        for( int is = 0; is < nMobs; is++ ) {
            sourceExcitations[is] = 0;
            Out currentSource = (Out)arrayOfSources[is];
            float[] sourceBuffer = currentSource.peekAtBuffer();
            for( int i = 0; i < bufferSize; i++ ) {
                sourceExcitations[is] += sourceBuffer[i] * sourceBuffer[i];
            }
            //            System.out.println("exc["+is+"] = "+ sourceExcitations[is]);
        }
    }

    /**
     * Estimate exitations. Load     protected float[] sourceExcitations
     * with sum_i sourceBuf^2[i], for the source buffers.
     */
    protected void estimateExcitations() {
        getSourceLevels();
        int totalnmodes = modeData.length;
        int point = 0; // works only for 1 location
        for( int i = 0; i < totalnmodes; i++ ) {
            ModeData md = modeData[i];
            QuenchableModalObjectWithOneContact qmob = md.parent;
            int indexOfModeInQMOB = md.indexInParent;
            float currentExcitation = qmob.getModeExcitation( indexOfModeInQMOB );
            float a_mode = qmob.modalModel.a[point][indexOfModeInQMOB];
            float sourceExcitation = a_mode * sourceExcitations[md.indexOfModalObject];
            // set excitation
            md.ex = currentExcitation + sourceExcitation;
            md.exdB = BarkScale.decibel( md.ex );
            md.exMOBdB = BarkScale.decibel( currentExcitation );
            // use exMOB as we use this to set the masker level
            if( md.exMOBdB > maximumExdBThatOccurred ) {
                maximumExdBThatOccurred = md.exdB;
                //System.out.println("max level = " + maximumExdBThatOccurred);
            }
        }

    }

    /**
     */
    protected void quench() {
        MaskingCurve.av = this.av;
        // first set all flag to on
        int ntotalmodes = modeData.length;
        for( int i = 0; i < ntotalmodes; i++ ) {
            modeData[i].on = true;
            //float lev = modeData[i].exdB+ dbLevelLoudestMode - maximumExdBThatOccurred;
            //System.out.println("ex["+i+"] = " + lev);
        }
        // add this to all excitations to normalize absolute levels to the
        // actual level (which may be true or "reference"). I.e., we
        // pretend maximumExdBThatOccurred = dbLevelLoudestMode
        float addToEx = dbLevelLoudestMode - maximumExdBThatOccurred;
        // for every mode, starting with loudest kill all modes it masks. If lies below threshold kill self and continue
        for( int i = 0; i < ntotalmodes - 1; i++ ) {
            if( modeData[i].on == true ) { // skip off ones
                float levelMasker = modeData[i].exMOBdB + addToEx;
                if( ( modeData[i].exdB + addToEx ) < modeData[i].ath ) {
                    modeData[i].on = false;
                }
                else {
                    float bMasker = modeData[i].b;
                    for( int k = i + 1; k < ntotalmodes; k++ ) {
                        if( modeData[k].on == true ) { // skip off ones
                            float levelMasked = modeData[k].exdB + addToEx;
                            if( levelMasked < modeData[i].ath ) {
                                modeData[k].on = false;
                            }
                            else {
                                float bMaskee = modeData[k].b;
                                if( levelMasked < MaskingCurve.masker( bMaskee, bMasker, levelMasker ) ) {
                                    modeData[k].on = false;
                                }
                            }
                        }
                    }
                }
            }
        }
        // loaded ModeData[], now kill modes
        int nkilled = 0;
        for( int i = 0; i < ntotalmodes; i++ ) {
            modeData[i].parent.setOnBit( modeData[i].indexInParent, modeData[i].on );
            if( !modeData[i].on ) {
                nkilled++;
            }
        }
        double perc = 100 * ( ntotalmodes - nkilled ) / ( (double)ntotalmodes );
        //System.out.println("killed: "+nkilled +"/"+ntotalmodes+" using: "+perc+"%");
        this.nKilledModes = nkilled;
        this.nTotalModes = ntotalmodes;
    }

    /**
     * Add QuenchableModalObjectWithOneContact to Sink.
     *
     * @param s QuenchableModalObjectWithOneContact to add.
     * @return object representing Source in Sink (may be null).
     */
    public synchronized void addModalObject( QuenchableModalObjectWithOneContact s ) {
        modalObjectsContainer.addElement( s );
        nMobs++;
    }

    /**
     * Get array of QuenchableModalObjectWithOneContact's
     *
     * @return array of QuenchableModalObjectWithOneContact's, null if there are none.
     */
    public Object[] getModalObjects() {
        return modalObjectsContainer.toArray();
    }

    protected void sortModeData() {
        QSort.sort( modeData );
    }


    /**
     * Make data structure (to be called after all Sources and
     * QuenchableModalObjectWithOneContact's have been added).
     * Also allocate the array of excitations. Each source is attached to 1 ModalObject.
     */
    private void makeModeDataStructure() {
        int nmodes = 0;
        for( int i = 0; i < mobs.length; i++ ) {
            nmodes += mobs[i].modalModel.nfUsed;
        }
        modeData = new ModeData[nmodes];
        for( int i = 0; i < nmodes; i++ ) {
            modeData[i] = new ModeData();
        }
        sourceExcitations = new float[mobs.length];
        int imode = 0;
        for( int i = 0; i < mobs.length; i++ ) {
            sourceExcitations[i] = 0;
            ModalModel mm = mobs[i].modalModel;
            int nfInHere = mm.nfUsed;
            for( int k = 0; k < nfInHere; k++ ) {
                modeData[imode].f = mm.f[k] * mm.fscale;
                modeData[imode].b = BarkScale.bark( mm.f[k] );
                modeData[imode].d = mm.d[k] * mm.dscale;
                modeData[imode].a = mm.a[0][k] * mm.ascale;
                if( modeData[imode].d != 0 ) {
                    modeData[imode].a2_d =
                    modeData[imode].a * modeData[imode].a / modeData[imode].d;
                }
                else {
                    modeData[imode].a2_d = 0;
                }
                modeData[imode].ath = 0; // simple model for this for now
                modeData[imode].ex = 0;
                modeData[imode].exdB = BarkScale.decibel( modeData[imode].ex );
                modeData[imode].exMOBdB = BarkScale.decibel( 0 );
                modeData[imode].parent = mobs[i];
                modeData[imode].indexOfModalObject = i;
                modeData[imode].indexInParent = k;
                modeData[imode].on = true;
                imode++;
            }
        }
    }

}

class ModeData implements Comparable {
    float f; // freq in Hz
    float b; //freq in Barks
    float d; // damping in 1/s
    float a; // gain
    float a2_d; // a^2/d
    float ath; // absolute threshold for this mode (in terms of y^2, y audio signal)
    float ex; // current excitation (energy)
    float exdB; // current excitation in dB (10log_10(energy))
    float exMOBdB; // current excitation of ModalObject only (not including source) in dB (10log_10(energy))
    QuenchableModalObjectWithOneContact parent; // ModalObject it belongs to
    int indexOfModalObject; // 0,.., n-1 for n objects, refers to order in patch
    int indexInParent; // index of mode in parent
    boolean on; // true if this mode is on, false otherwise

    public int compareTo( Object o ) {
        ModeData m = (ModeData)o;
        if( this.ex < m.ex ) {
            return 1;
        }
        else if( this.ex > m.ex ) {
            return -1;
            // this.ex == m.ex:
        }
        else if( this.a2_d < m.a2_d ) {
            return 1;
        }
        else if( this.a2_d > m.a2_d ) {
            return -1;
            // this.a2_d == m.a2_d
        }
        else if( this.f < m.f ) {
            return 1;
        }
        else if( this.f > m.f ) {
            return -1;
        }
        else {
            return 0;
        }
    }
}

// IDEA: since excitation is overestimated, may have 2 much masking, so correct for this
// by using only the ACTUAL excitation in ModalObject to set masker, but use SUM
// for maskees

/**
 * Defines masking curve for source at given freq. in Barks.
 */
class MaskingCurve {

    static private final float sl = 25; // lower slope; constant

    /**
     * magic number defining level of masker.
     * 20dB suggested by literature, experimentally 65dB is OK
     */
    static public float av = 5;

    /**
     * float lm = level in dB SPL of source
     */
    static private final float su( float lm ) {
        return (float)( 22 - lm / 5 ); // upper slope
    }

    /**
     * Masking curve (function of maskee bMaskee in Barks) for masker of level
     * lm (dB SPL) and Bark bMasker.
     *
     * @param bMaskee freq. in Barks of masked freq.
     * @param bMasker freq. in Barks of masking freq.
     * @param lm      dB SPL loudness of masker
     * @return dB SPL value of masking curve
     */
    public static final float masker( float bMaskee, float bMasker, float lm ) {
        if( bMaskee < bMasker ) {
            return (float)( lm - av + sl * ( bMaskee - bMasker ) );
        }
        else {
            return (float)( lm - av + su( lm ) * ( bMasker - bMaskee ) );
        }
    }
}

/**
 * Convert to and from Barkscale
 * Use:    Schroeder (1977): bark = 7*asinh(f/650);
 */
class BarkScale {

    static private final double FACTOR = 10 / Math.log( 10 );
    static private final double MIN = 1.e-10;

    static private final double asinh( double x ) {
        return Math.log( Math.sqrt( x * x + 1 ) + x );
    }

    static private final double sinh( double x ) {
        return ( Math.exp( x ) - Math.exp( -x ) ) / 2;
    }

    static public final float bark( float hertz ) {
        return (float)( 7 * asinh( (float)( hertz / 650 ) ) );
    }

    static public final float hertz( float bark ) {
        return (float)( 650 * sinh( bark / 7 ) );
    }

    static public final float decibel( float energy ) {
        return (float)( FACTOR * Math.log( Math.max( MIN, Math.abs( energy ) ) ) );
    }
}

class QSort {

    static final public void sort( ModeData[] list ) {
        //        System.out.println("sorting "+list.length +" items");
        quicksort( list, 0, list.length - 1 );
    }

    static final private void quicksort( ModeData[] list, int p, int r ) {
        if( p < r ) {
            int q = partition( list, p, r );
            if( q == r ) {
                q--;
            }
            quicksort( list, p, q );
            quicksort( list, q + 1, r );
        }
    }

    static final private int partition( ModeData[] list, int p, int r ) {
        ModeData pivot = list[p];
        int lo = p;
        int hi = r;

        while( true ) {
            while( list[hi].compareTo( pivot ) >= 0 && lo < hi ) {
                hi--;
            }
            while( list[lo].compareTo( pivot ) < 0 && lo < hi ) {
                lo++;
            }
            if( lo < hi ) {
                ModeData T = list[lo];
                list[lo] = list[hi];
                list[hi] = T;
            }
            else {
                return hi;
            }
        }
    }
}


