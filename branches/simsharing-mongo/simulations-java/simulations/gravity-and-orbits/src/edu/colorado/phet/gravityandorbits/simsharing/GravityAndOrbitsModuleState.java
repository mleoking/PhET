// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.simsharing;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsMode;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;

/**
 * Serializable state for simsharing, stores the state of an entire module.
 *
 * @author Sam Reid
 */
public class GravityAndOrbitsModuleState implements IProguardKeepClass {
    private boolean showGravityForce;
    private boolean showPaths;
    private boolean showVelocity;
    private boolean showMass;
    private boolean showGrid;
    private boolean gravityEnabled;
    private ArrayList<GravityAndOrbitsModeState> modeStates;
    private int selectedMode;
    private boolean showMeasuringTape;

    public GravityAndOrbitsModuleState( GravityAndOrbitsModule module ) {
        showGravityForce = module.showGravityForceProperty.get();
        showPaths = module.showPathProperty.get();
        showVelocity = module.showVelocityProperty.get();
        showMass = module.showMassProperty.get();
        gravityEnabled = module.gravityEnabledProperty.get();
        modeStates = new ArrayList<GravityAndOrbitsModeState>();
        for ( GravityAndOrbitsMode mode : module.getModes() ) {
            modeStates.add( new GravityAndOrbitsModeState( mode ) );
        }
        selectedMode = module.getModeIndex();
        showGrid = module.showGridProperty.get();
        showMeasuringTape = module.measuringTapeVisibleProperty.get();
    }

    public void apply( GravityAndOrbitsModule m ) {
        m.showGravityForceProperty.set( showGravityForce );
        m.showPathProperty.set( showPaths );
        m.showVelocityProperty.set( showVelocity );
        m.showMassProperty.set( showMass );
        m.gravityEnabledProperty.set( gravityEnabled );
        for ( int i = 0; i < modeStates.size(); i++ ) {
            modeStates.get( i ).apply( m.getModes().get( i ) );
        }
        m.setModeIndex( selectedMode );
        m.showGridProperty.set( showGrid );
        m.measuringTapeVisibleProperty.set( showMeasuringTape );
    }

    @Override public String toString() {
        return "GravityAndOrbitsModuleState{" +
               "showGravityForce=" + showGravityForce +
               ", showPaths=" + showPaths +
               ", showVelocity=" + showVelocity +
               ", showMass=" + showMass +
               ", showGrid=" + showGrid +
               ", gravityEnabled=" + gravityEnabled +
               ", modeStates=" + modeStates +
               ", selectedMode=" + selectedMode +
               ", showMeasuringTape=" + showMeasuringTape +
               '}';
    }
}
