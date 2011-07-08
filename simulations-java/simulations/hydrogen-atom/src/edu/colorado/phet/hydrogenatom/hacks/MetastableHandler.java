// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.hydrogenatom.hacks;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.logging.Logger;

import edu.colorado.phet.common.phetcommon.model.clock.ClockAdapter;
import edu.colorado.phet.common.phetcommon.model.clock.ClockEvent;
import edu.colorado.phet.common.phetcommon.model.clock.IClock;
import edu.colorado.phet.common.phetcommon.util.logging.LoggingUtils;
import edu.colorado.phet.hydrogenatom.model.AbstractHydrogenAtom;
import edu.colorado.phet.hydrogenatom.model.Gun;
import edu.colorado.phet.hydrogenatom.model.SchrodingerModel;

/**
 * MetastableHandler handles a case where the Schrodinger model
 * can get stuck in state (n,l,m) = (2,0,0). This state is known as a 
 * metastable state. The only way to get out of this state is to absorb
 * a photon that takes the atom to a higher state.
 * <p>
 * While the gun is shooting white light and the atom is in state (2,0,0),
 * we fire an absorbable photon at the atom's center every MAX_STUCK_TIME milliseconds.
 * <p>
 * This solution assumes that the centers of the gun and atom are vertically aligned.
 * If that is not the case, a warning is printed to System.err.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class MetastableHandler extends ClockAdapter implements Observer {
    
    private static final Logger LOGGER = LoggingUtils.getLogger( MetastableHandler.class.getCanonicalName() );
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------

    // metastable state is (n,l,m) = (2,0,0)
    private static final int METASTABLE_N = 2;
    private static final int METASTABLE_L = 0;
    private static final int METASTABLE_M = 0;
    /*
     * When the atom has been in the metastable state for this amount of
     * simulation time, we will fire an absorbable photon at its center.
     * This is public and non-final because it can be adjusted using a developer control.
     */
    public static double MAX_STUCK_TIME = 100; // dt
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private final IClock _clock; // the clock, for watching how long we're stuck in the metastable state
    private final Gun _gun; // the gun, for firing a photon
    private final SchrodingerModel _atom; // the atom, for observing state changes
    
    private boolean _stuck; // true indicates that the atom is in the metastable state
    private double _stuckTime; // how long the atom has been stuck in the metastable state, in sim time units
    
    private final Random _nRandom; // for generating new values of n (electron state)

    private final ArrayList<MetastableListener> _listeners;
    
    //----------------------------------------------------------------------------
    // Constructors
    //----------------------------------------------------------------------------
    
    /**
     * Constructor.
     * 
     * @param clock
     * @param gun
     * @param atom
     */
    public MetastableHandler( IClock clock, Gun gun, SchrodingerModel atom ) {
        super();
        
        _clock = clock;
        _gun = gun;
        _atom = atom;

        _stuck = false;
        _stuckTime = 0;
        _nRandom = new Random();
        
        _clock.addClockListener( this );
        _gun.addObserver( this );
        _atom.addObserver( this );

        _listeners = new ArrayList<MetastableListener>();
    }
    
    /**
     * Call this before releasing references to this object.
     */
    public void cleanup() {
        _clock.removeClockListener( this );
        _gun.deleteObserver( this );
        _atom.deleteObserver( this );
    }

    public boolean isMonochromaticLightType() {
        return _gun.isMonochromaticLightType();
    }

    //----------------------------------------------------------------------------
    // ClockAdapter overrides
    //----------------------------------------------------------------------------
    
    /**
     * Handles ticks of the clock.
     * While the gun is shooting white light and the atom is in the metastable state,
     * fire an absorbable photon every MAX_STUCK_TIME milliseconds.
     * Time is accumulated only while the gun is enabled and firing white light.
     * 
     * @param event
     */
    public void clockTicked( ClockEvent event ) {
        if ( _stuck && _gun.isEnabled() && _gun.isPhotonsMode() && _gun.isWhiteLightType() ) {
            _stuckTime += event.getSimulationTimeChange();
            if ( _stuckTime >= MAX_STUCK_TIME ) {
                LOGGER.info( "atom has been stuck for " + _stuckTime + " time units" );
                fireRandomAbsorbablePhoton();
                /* 
                 * Restart the timer, but don't clear the stuck flag.
                 * If the photon we fire is not absorbed, we may need to fire another one.
                 */
                _stuckTime = 0;
            }
        }
    }

    // Fires one absorbable photon at the atom's center, higher state chosen at random.
    private void fireRandomAbsorbablePhoton() {
        fireAbsorbablePhoton( METASTABLE_N + 1 + _nRandom.nextInt( SchrodingerModel.getNumberOfStates() - METASTABLE_N ) );
    }

    /*
     * See #2803.
     * Fires one absorbable photon at the atom's center, choosing a higher state that will result in
     * a wavelength that is apt to be visibly obvious to the user.  Specifically, we choose n=3 with
     * wavelength=656nm, which is a red photon. When we get stuck in the metastable state, the user is
     * most likely to be exploring the UV end of the spectrum, so firing a red photon should usually make
     * the photon easy to see. Alternatively we could try to select the higher state that results in the
     * wavelength that is furthest from the gun's current wavelength, but that would be more complicated.
     */
    public void fireObviousAbsorbablePhoton() {
        fireAbsorbablePhoton( METASTABLE_N + 1 );
    }

    private void fireAbsorbablePhoton( int n ) {
        assert( n > _atom.getElectronState() );
        // assumes that the centers of the gun and atom are vertically aligned
        if ( _gun.getPositionRef().getX() != _atom.getPositionRef().getX() ) {
            LOGGER.warning( "photon will not hit atom, centers of gun and atom are not vertically aligned!" );
        }
        // Determine the wavelength needed to move the atom to the higher state.
        double wavelength = SchrodingerModel.getWavelengthAbsorbed( METASTABLE_N, n );
        LOGGER.info( "firing an absorbable photon, n=" + n + " wavelength=" + wavelength );
        // Fire a photon with that wavelength at the atom's center.
        _gun.fireOnePhotonFromCenter( wavelength );
    }

    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Watches for changes in the atom's state.
     * If we enter the metastable state, we mark the state of the atom as "stuck".
     */
    public void update( Observable o, Object arg ) {
        if ( o == _atom ) {
            if ( arg == AbstractHydrogenAtom.PROPERTY_ELECTRON_STATE ) {
                // the value of n has changed
                if ( !_stuck && _atom.stateEquals( METASTABLE_N, METASTABLE_L, METASTABLE_M ) ) {
                    // we have entered the metastable state, set the stuck flag and timer
                    _stuck = true;
                    _stuckTime = 0;
                    LOGGER.info( "atom is stuck in metastable state " + _atom.getStateAsString() );
                    fireEnteredMetastableState();
                }
                else if ( _stuck && !_atom.stateEquals( METASTABLE_N, METASTABLE_L, METASTABLE_M ) ) {
                    // we have transitioned out of the metastable state, clear the stuck flag and timer
                    _stuck = false;
                    _stuckTime = 0;
                    LOGGER.info( "atom is unstuck, transitioned to state " + _atom.getStateAsString() );
                    fireExitedMetastableState();
                }
            }
        }
    }

    //----------------------------------------------------------------------------
    // MetastableListener interface
    //----------------------------------------------------------------------------

    public interface MetastableListener {
        public void enteredMetastableState();
        public void exitedMetastableState();
    }

    public void addMetastableListener( MetastableListener listener ) {
        _listeners.add( listener );
    }

    public void removeMetastableListener( MetastableListener listener ) {
        _listeners.remove( listener );
    }

    private void fireEnteredMetastableState() {
        for ( MetastableListener listener : new ArrayList<MetastableListener>( _listeners ) ) {
            listener.enteredMetastableState();
        }
    }

    private void fireExitedMetastableState() {
        for ( MetastableListener listener : new ArrayList<MetastableListener>( _listeners ) ) {
            listener.exitedMetastableState();
        }
    }
}
