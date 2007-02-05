/* Copyright 2006, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */

package edu.colorado.phet.hydrogenatom.hacks;

import java.util.Observable;
import java.util.Observer;
import java.util.Random;

import edu.colorado.phet.common.model.clock.ClockAdapter;
import edu.colorado.phet.common.model.clock.ClockEvent;
import edu.colorado.phet.common.model.clock.IClock;
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
 * @version $Revision$
 */
public class MetastableHandler extends ClockAdapter implements Observer {
    
    //----------------------------------------------------------------------------
    // Debug
    //----------------------------------------------------------------------------
    
    // enables debugging output to System.out
    private static final boolean DEBUG_OUTPUT = false;
    
    //----------------------------------------------------------------------------
    // Class data
    //----------------------------------------------------------------------------
    
    /* 
     * When the atom has been in state (2,0,0) for this amount of
     * simulation time, we will fire an absorbable photon at its center.
     */
    public static double MAX_STUCK_SIM_TIME = 125; // dt
    
    //----------------------------------------------------------------------------
    // Instance data
    //----------------------------------------------------------------------------
    
    private IClock _clock; // the clock, for watching how long we're stuck in (2,0,0)
    private Gun _gun; // the gun, for firing a photon
    private SchrodingerModel _atom; // the atom, for observing state changes
    
    private boolean _stuck; // true indicates that the atom is in state (2,0,0)
    private double _stuckSimTime;
    
    private Random _nRandom; // for generating new values of n
    
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
        
        // This implementation works for n = [1...6]
        assert( SchrodingerModel.getGroundState() == 1 );
        assert( SchrodingerModel.getNumberOfStates() == 6 );
        
        _clock = clock;
        _gun = gun;
        _atom = atom;

        _stuck = false;
        _stuckSimTime = 0;
        _nRandom = new Random();
        
        _clock.addClockListener( this );
        _gun.addObserver( this );
        _atom.addObserver( this );
    }
    
    /**
     * Call this before releasing references to this object.
     */
    public void cleanup() {
        _clock.removeClockListener( this );
        _gun.deleteObserver( this );
        _atom.deleteObserver( this );
    }

    //----------------------------------------------------------------------------
    // ClockAdapter overrides
    //----------------------------------------------------------------------------
    
    /**
     * Handles ticks of the clock.
     * While the gun is shooting white light and the atom is in state (2,0,0),
     * fire an absorbable photon every MAX_STUCK_TIME milliseconds.
     * Time is accumulated only while the gun is enabled and firing white light.
     * 
     * @param event
     */
    public void clockTicked( ClockEvent event ) {
        if ( _stuck && _gun.isEnabled() && _gun.isPhotonsMode() && _gun.isWhiteLightType() ) {
            _stuckSimTime += event.getSimulationTimeChange();
            if ( _stuckSimTime >= MAX_STUCK_SIM_TIME ) {
                System.out.println( "stuckSimTime=" + _stuckSimTime );//XXX
                fireOneAbsorbablePhoton();
                /* 
                 * Restart the timer, but don't clear the stuck flag.
                 * If the photon we fire is not absorbed, we may need to fire another one.
                 */
                _stuckSimTime = 0;
            }
        }
    }
    
    /*
     * Fires one absorbable photon at the atom's center.
     */
    private void fireOneAbsorbablePhoton() {
        // assumes that the centers of the gun and atom are vertically aligned
        if ( _gun.getPositionRef().getX() != _atom.getPositionRef().getX() ) {
            System.err.println( "WARNING! SchrodingerUnstucker.fireOneAbsorbablePhoton: centers of gun and atom are not vertically aligned" );
        }
        // Select a state higher than n=2, choose from n=[3,4,5,6]
        int nNew = 3 + _nRandom.nextInt( 4 );
        // Determine the wavelength needed to move the atom to the higher state.
        double wavelength = SchrodingerModel.getWavelengthAbsorbed( 2, nNew );
        if ( DEBUG_OUTPUT ) {
            System.out.println( "SchrodingerUnstucker.fireOneAbsorbablePhoton: nNew=" + nNew + " wavelength=" + wavelength );
        }
        // Fire a photon with that wavelength at the atom's center.
        _gun.fireOnePhotonFromCenter( wavelength );
    }
    
    //----------------------------------------------------------------------------
    // Observer implementation
    //----------------------------------------------------------------------------
    
    /**
     * Watches for changes in the atom's state.
     * If the state becomes (2,0,0), we mark the state of the atom as "stuck".
     */
    public void update( Observable o, Object arg ) {
        if ( o == _atom ) {
            if ( arg == AbstractHydrogenAtom.PROPERTY_ELECTRON_STATE ) {
                // the value of n has changed
                int n = _atom.getElectronState();
                int l = _atom.getSecondaryElectronState();
                if ( !_stuck && ( n == 2 && l == 0 ) ) {
                    // we have entered (2,0,0), set the stuck flag and timer
                    _stuck = true;
                    _stuckSimTime = 0;
                    if ( DEBUG_OUTPUT ) {
                        System.out.println( "SchrodingerUnstucker.update: atom is stuck" );
                    }
                }
                else if ( _stuck && !( n == 2 && l == 0 ) ) {
                    // we have transitioned out of (2,0,0), clear the stuck flag and timer
                    _stuck = false;
                    _stuckSimTime = 0;
                    if ( DEBUG_OUTPUT ) {
                        System.out.println( "SchrodingerUnstucker.update: atom is unstuck" );
                    }
                }
            }
        }
    }

}
