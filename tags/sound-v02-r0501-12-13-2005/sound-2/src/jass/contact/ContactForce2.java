package jass.contact;

import jass.engine.BufferNotAvailableException;
import jass.engine.Out;
import jass.engine.SinkIsFullException;
import jass.generators.LoopBuffer;
import jass.generators.ModalModel;
import jass.generators.ModalObjectWithOneContact;

/**
 * A force model with impact, slide, and slide modes based on looping wav files
 * Roll force is fed through modal filter.
 *
 * @author Kees van den Doel (kvdoel@cs.ubc.ca)
 */
public class ContactForce2 extends Out {

    // Has these protected sources:
    protected BangForce bangForce;
    protected LoopBuffer slideForce;
    protected LoopBuffer rollForceRaw;
    // modal filter for rolling
    protected ModalObjectWithOneContact rollForce;
    protected float dryRollGain = 0; // 1 for raw wav force, 0 for pure filtered

    // properties defining how physical parameters are mapped to audio parameters
    // [x0,x1] is range of x

    // maximum audio speeds, 1 is original wav file
    protected float slideSpeed1 = 1f, rollSpeed1 = 1f;

    // physical speed ranges
    protected float vslide0 = .1f, vslide1 = 1f, vroll0 = .1f, vroll1 = 1f;

    // gain ratios
    protected float physicalToAudioGainSlide = 1, physicalToAudioGainRoll = 1, physicalToAudioGainImpact = 1;

    /**
     * Set model parameters mapping physical units to audio units
     *
     * @param slideSpeed1               maximum audio loop speed (1 = original recording)
     * @param rollSpeed1                maximum audio loop speed (1 = original recording)
     * @param vslide0                   minimum physical speed (lower than this is considered to be zero)
     * @param vslide1                   maximum physical speed (higher than this is set to this value)
     * @param vroll0                    minimum physical speed (lower than this is considered to be zero)
     * @param vroll1                    maximum physical speed (higher than this is set to this value)
     * @param physicalToAudioGainSlide  multiplies normal force to get slide gain
     * @param physicalToAudioGainRoll   multiplies normal force to get roll gain
     * @param physicalToAudioGainImpact multiplies impact force to get impact gain
     */
    public void setStaticContactModelParameters( float slideSpeed1, float rollSpeed1, float vslide0,
                                                 float vslide1, float vroll0, float vroll1,
                                                 float physicalToAudioGainSlide,
                                                 float physicalToAudioGainRoll, float physicalToAudioGainImpact ) {
        this.slideSpeed1 = slideSpeed1;
        this.rollSpeed1 = rollSpeed1;
        this.vslide0 = vslide0;
        this.vslide1 = vslide1;
        this.vroll0 = vroll0;
        this.vroll1 = vroll1;
        this.physicalToAudioGainSlide = physicalToAudioGainSlide;
        this.physicalToAudioGainRoll = physicalToAudioGainRoll;
        this.physicalToAudioGainImpact = physicalToAudioGainImpact;
    }

    /**
     * Contructor intended only for subclass constructors (super(bufferSize);)
     */
    protected ContactForce2( int bufferSize ) {
        super( bufferSize );
    }

    /**
     * Construct contact force from named files.
     *
     * @param srate      sampling rate in Hertz.
     * @param bufferSize bufferSize of this Out.
     * @param fnImpact   audio file name for impact. (For example cos20ms.wav.)
     * @param fnSlide    audio file name for slide. (For example grid.wav.)
     * @param fnRoll     audio file name for slide. (For example roll.wav.)
     * @param m          modal model to build roll filter from.
     */
    public ContactForce2( float srate, int bufferSize, String fnImpact, String fnSlide, String fnRoll, ModalModel m ) {
        super( bufferSize );
        bangForce = new BangForce( srate, bufferSize, fnImpact );
        slideForce = new LoopBuffer( srate, bufferSize, fnSlide );
        rollForceRaw = new LoopBuffer( srate, bufferSize, fnRoll );
        bangForce.setTime( getTime() );
        slideForce.setTime( getTime() );
        rollForceRaw.setTime( getTime() );
        rollForce = new ModalObjectWithOneContact( m, srate, bufferSize );
        try {
            rollForce.addSource( rollForceRaw );
        }
        catch( SinkIsFullException e ) {
            System.out.println( this + " " + e );
        }
        bangForce.setTime( getTime() );
        slideForce.setTime( getTime() );
        rollForceRaw.setTime( getTime() );
        rollForce.setTime( getTime() );
    }

    /**
     * Generate impact force  in physical units.
     *
     * @param force magnitude.
     * @param dur   duration in seconds of impact.
     */
    public synchronized void bang( float force, float dur ) {
        bangForce.bang( physicalToAudioGainImpact * force, dur );
    }

    /**
     * Set slide speed and normal force  in physical units.
     *
     * @param force normal force.
     * @param speed relative surface velocity.
     */
    public synchronized void setSlideProperties( float force, float speed ) {
        if( speed > vslide1 ) {
            speed = vslide1;
        }
        else if( speed < vslide0 ) {
            speed = 0;
            force = 0;
        }
        slideForce.setSpeed( speed * slideSpeed1 / vslide1 );
        float vol = (float)( Math.sqrt( force * speed / vslide1 ) );
        slideForce.setVolume( physicalToAudioGainSlide * vol );
    }

    /**
     * Set roll speed and normal force in physical units.
     *
     * @param force normal force.
     * @param speed roll velocity.
     */
    public synchronized void setRollProperties( float force, float speed ) {
        if( speed > vroll1 ) {
            speed = vroll1;
        }
        else if( speed < vroll0 ) {
            speed = 0;
            force = 0;
        }
        rollForceRaw.setSpeed( speed * rollSpeed1 / vroll1 );
        float vol = (float)( Math.sqrt( force * speed / vroll1 ) );
        rollForceRaw.setVolume( physicalToAudioGainRoll * vol );
    }


    /**
     * Set roll force filter properties
     */
    public synchronized void setRollFilter( float damping, float dryRollGain ) {
        rollForce.setDamping( damping );
        this.dryRollGain = dryRollGain;
    }

    /**
     * Set roll force filter number of modes
     *
     * @param nf number of modes
     */
    public synchronized void setNf( int nf ) {
        rollForce.setNf( nf );
    }

    /**
     * Compute the next buffer.
     */
    public synchronized void computeBuffer() {
        try {
            float[] b1 = bangForce.getBuffer( getTime() );
            float[] b2 = slideForce.getBuffer( getTime() );
            float[] b3 = rollForceRaw.getBuffer( getTime() );
            float[] b4 = rollForce.getBuffer( getTime() );
            int bufsz = getBufferSize();
            for( int k = 0; k < bufsz; k++ ) {
                buf[k] = b1[k] + b2[k] + dryRollGain * b3[k] + ( 1 - dryRollGain ) * b4[k];
            }
        }
        catch( BufferNotAvailableException e ) {
            System.out.println( this + " " + e );
        }
    }
}


