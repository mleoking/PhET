/**
 * Class: SoundModel
 * Package: edu.colorado.phet.sound.model
 * Author: Another Guy
 * Date: Aug 3, 2004
 */
package edu.colorado.phet.sound.model;

import edu.colorado.phet.common.model.BaseModel;
import edu.colorado.phet.common.model.clock.AbstractClock;
import edu.colorado.phet.sound.SoundConfig;
import edu.colorado.phet.sound.view.SoundApparatusPanel;

import java.util.Iterator;
import java.awt.geom.Point2D;

public class SoundModel extends BaseModel {

    private WavefrontType wavefrontType;

    private WaveMedium waveMedium = new WaveMedium();
    private AbstractClock clock;
    private int audioSource;
//    private Point2D.Double audioReferencePt;
    private double frequency;

    // Brought over from SoundApplication
    private Wavefront primaryWavefront;
    private Wavefront octaveWavefront;
    private WavefrontOscillator primaryOscillator;
    private boolean audioEnabled;
//    private float octaveAmplitude = 0;
    private WavefrontOscillator octaveOscillator;
    private boolean octaveEnabled = false;
    private Point2D.Double listenerLocation;
    private boolean initialized = false;

    // Used to save and restore audio state when Stop and Run are pressed
    private boolean savedAudioEnabledState = false;


    public SoundModel( AbstractClock clock ) {
        this.clock = clock;
        addModelElement( waveMedium );
    }

    /**
     *
     */
    public WaveMedium getWaveMedium() {
        return waveMedium;
    }

    /**
     *
     */
    public void addWaveFront( Wavefront wavefront ) {
        waveMedium.addWavefront( wavefront );

        // TODO: This should go somewhere else.
        setPropagationSpeed( 3 );
    }

    public void removeWaveFront( Wavefront wavefront ) {
        waveMedium.removeWavefront( wavefront );
    }

    public WavefrontType getWavefrontType() {
        return wavefrontType;
    }

    public void setWavefrontType( WavefrontType wavefrontType ) {
        this.wavefrontType = wavefrontType;
        for( Iterator iterator = waveMedium.getWavefronts().iterator(); iterator.hasNext(); ) {
            Wavefront wavefront = (Wavefront)iterator.next();
            wavefront.setWavefrontType( wavefrontType );
        }
    }

    public void setPropagationSpeed( int propogationSpeed ) {
        for( Iterator iterator = waveMedium.getWavefronts().iterator(); iterator.hasNext(); ) {
            Wavefront wavefront = (Wavefront)iterator.next();
            wavefront.setPropagationSpeed( propogationSpeed );
        }
    }

    public void clear() {
        for( Iterator iterator = waveMedium.getWavefronts().iterator(); iterator.hasNext(); ) {
            Wavefront wavefront = (Wavefront)iterator.next();
            wavefront.clear();
        }
        // Step the system to notify all observers
        this.stepInTime( clock.getDt() );
    }

    /**
     *
     */
    public synchronized void stepInTime( double dt ) {
        super.stepInTime( dt );
    }

    public void setAudioSource( int audioSource ) {
        this.audioSource = audioSource;
        updateOscillators();
    }

    public int getAudioSource() {
        return audioSource;
    }

    public void setAudioReferencePoint( Point2D.Double refPt ) {
//        this.audioReferencePt = refPt;
        primaryOscillator.setReferencePoint( refPt );
        octaveOscillator.setReferencePoint( refPt );
    }

    public void setOscillatorFrequency( double frequency ) {
        this.frequency = frequency;
    }

    public void setAudioEnabled( boolean audioEnabled ) {
//        if( this.initialized ) {
            primaryOscillator.setEnabled( audioEnabled );
            octaveOscillator.setEnabled( audioEnabled && octaveEnabled );
//        }
        this.audioEnabled = audioEnabled;
    }


    /**
     *
     */
    public void setFrequency( double frequency ) {
        primaryWavefront.setFrequency( frequency / SoundConfig.s_frequencyDisplayFactor );
        octaveWavefront.setFrequency( 2 * frequency / SoundConfig.s_frequencyDisplayFactor );
    }

    /**
     *
     * @return Maximum amplitude of the main speaker
     */
    public double getAmplitude() {
        return primaryWavefront.getMaxAmplitude();
    }

    /**
     * TODO: move to command
     */
    public void setAmplitude( double amplitude ) {
        primaryWavefront.setMaxAmplitude( amplitude );
    }

    public void setOctaveWavefront( Wavefront octaveWavefront ) {
        this.octaveWavefront = octaveWavefront;
    }

    /**
     * TODO: move to command
     */
    public void setOctaveAmplitude( double amplitude ) {
//        this.octaveAmplitude = amplitude;
        octaveWavefront.setMaxAmplitude( amplitude );
    }

    public Wavefront getPrimaryWavefront() {
        return primaryWavefront;
    }

    public void setPrimaryWavefront( Wavefront primaryWavefront ) {
        this.primaryWavefront = primaryWavefront;
    }

    public WavefrontOscillator getPrimaryOscillator() {
        return primaryOscillator;
    }

    public void setPrimaryOscillator( WavefrontOscillator primaryOscillator ) {
        this.primaryOscillator = primaryOscillator;
    }

    public WavefrontOscillator getOctaveOscillator() {
        return octaveOscillator;
    }

    public void setOctaveOscillator( WavefrontOscillator octaveOscillator ) {
        this.octaveOscillator = octaveOscillator;
    }

//    public boolean isOctaveEnabled() {
//        return octaveEnabled;
//    }

    public void setOctaveEnabled( boolean octaveEnabled ) {
//        this.octaveEnabled = octaveEnabled;
        this.octaveWavefront.setEnabled( octaveEnabled );
    }

    // Listener-related methods
    /**
     *
     */
    public void setListenerLocation( Point2D.Double location ) {
        this.setListenerLocation( location.getX(), location.getY() );
    }

    /**
     *
     */
    public void setListenerLocation( double x, double y ) {
        if( listenerLocation == null ) {
            listenerLocation = new Point2D.Double();
        }
        System.out.println( "location = " + listenerLocation );

        synchronized( listenerLocation ) {
            this.listenerLocation.setLocation( x, y );
        }
        setOscillatorReferencePoint();
    }

    /**
     *
     */
    public void updateOscillators() {
        setOscillatorReferencePoint();
        if( primaryOscillator != null ) {
            primaryOscillator.update();
        }
        if( octaveOscillator != null ) {
            octaveOscillator.update();
        }
    }

    private void setOscillatorReferencePoint() {
        // HACK!!! Test is stupid
        if( primaryOscillator != null ) {
            if( audioSource == SoundApparatusPanel.LISTENER_SOURCE ) {
                primaryOscillator.setReferencePoint( listenerLocation );
                octaveOscillator.setReferencePoint( listenerLocation );
            }
            else {
                primaryOscillator.setReferencePoint( 0, 0 );
                octaveOscillator.setReferencePoint( 0, 0 );
            }
        }
    }
}
