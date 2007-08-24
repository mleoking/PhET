package edu.colorado.phet.lasers.view;

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
        if( listener != null ) {
            listener.putMatch( beam, matchState );
        }
    }

    public MatchState getMatch() {
        double energyAboveGround = atomicState.getEnergyLevel() - model.getGroundState().getEnergyLevel();
        boolean match = beam.isEnabled() &&
                        Math.abs( PhysicsUtil.wavelengthToEnergy( beam.getWavelength() ) - energyAboveGround ) <= QuantumConfig.ENERGY_TOLERANCE
                        && beam.getPhotonsPerSecond() > 0;
        return new MatchState( match, System.currentTimeMillis(), PhysicsUtil.wavelengthToEnergy( beam.getWavelength() ) + model.getGroundState().getEnergyLevel() );
    }

    public void meanLifetimechanged( AtomicState.Event event ) {
    }

    public void wavelengthChanged( Beam.WavelengthChangeEvent event ) {
        checkForMatch();
    }
}
