// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.lasers.view;

import edu.colorado.phet.common.phetcommon.math.MathUtil;
import edu.colorado.phet.common.phetcommon.util.PhysicsUtil;
import edu.colorado.phet.common.quantum.QuantumConfig;
import edu.colorado.phet.common.quantum.model.AtomicState;
import edu.colorado.phet.common.quantum.model.Beam;
import edu.colorado.phet.common.quantum.model.PhotonSource;
import edu.colorado.phet.lasers.model.LaserModel;

/**
 * Flashes a PhetGraphic when the wavelength of a beam matches (or is acceptably close to) the
 * difference in energy between an atomic state and the model's ground state
 */
public class EnergyMatchDetector implements AtomicState.Listener, Beam.WavelengthChangeListener {
    private LaserModel model;
    private AtomicState atomicState;
    private Beam beam;
    private Listener listener;

    public EnergyMatchDetector( LaserModel model, AtomicState atomicState, Beam beam ) {
        this( model, atomicState, beam, null );
    }

    public EnergyMatchDetector( LaserModel model, AtomicState atomicState, Beam beam, Listener listener ) {
        this.model = model;
        this.atomicState = atomicState;
        this.beam = beam;
        this.listener = listener;
        atomicState.addListener( this );
        beam.addWavelengthChangeListener( this );
        beam.addRateChangeListener( new PhotonSource.RateChangeListener() {
            public void rateChangeOccurred( PhotonSource.RateChangeEvent event ) {
                checkForMatch();
            }
        } );
//        checkForMatch();
    }

    public static interface Listener {
        void putMatch( Beam beam, MatchState matchState );
    }

    public void energyLevelChanged( AtomicState.Event event ) {
        checkForMatch();
    }

    private void checkForMatch() {
        MatchState matchState = getMatch();
        if ( listener != null ) {
            listener.putMatch( beam, matchState );
        }
    }

    public MatchState getMatch() {
        double e0 = model.getGroundState().getEnergyLevel();
        double transitionEnergy = atomicState.getEnergyLevel() - e0;
        double beamEnergy = PhysicsUtil.wavelengthToEnergy( beam.getWavelength() );
        boolean match = beam.isEnabled() && beam.getPhotonsPerSecond() > 0
                        && MathUtil.isApproxEqual( beamEnergy, transitionEnergy, QuantumConfig.ENERGY_TOLERANCE );
        return new MatchState( match, System.currentTimeMillis(), beamEnergy + e0, e0, transitionEnergy, beamEnergy );
    }

    public void meanLifetimeChanged( AtomicState.Event event ) {
    }

    public void wavelengthChanged( Beam.WavelengthChangeEvent event ) {
        checkForMatch();
    }
}
