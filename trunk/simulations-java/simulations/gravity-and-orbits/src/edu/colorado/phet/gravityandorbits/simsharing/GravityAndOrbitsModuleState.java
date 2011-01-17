// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.simsharing;

import java.io.Serializable;
import java.util.ArrayList;

import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsMode;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;
import edu.colorado.phet.gravityandorbits.view.Scale;

/**
 * @author Sam Reid
 */
public class GravityAndOrbitsModuleState implements Serializable {

    private final boolean showGravityForce;
    private final boolean showPaths;
    private final boolean showVelocity;
    private final boolean showMass;
    private final boolean showGrid;
    private final boolean gravityEnabled;
    private final ArrayList<GravityAndOrbitsModeState> modeStates;
    private final int selectedMode;
    private final boolean cartoonScale;
    private final boolean showMeasuringTape;

    public GravityAndOrbitsModuleState( GravityAndOrbitsModule module ) {
        showGravityForce = module.getShowGravityForceProperty().getValue();
        showPaths = module.getShowPathProperty().getValue();
        showVelocity = module.getShowVelocityProperty().getValue();
        showMass = module.getShowMassProperty().getValue();
        gravityEnabled = module.getGravityEnabledProperty().getValue();
        modeStates = new ArrayList<GravityAndOrbitsModeState>();
        for ( GravityAndOrbitsMode mode : module.getModes() ) {
            modeStates.add( new GravityAndOrbitsModeState( mode ) );
        }
        selectedMode = module.getModeIndex();
        showGrid = module.getShowGridProperty().getValue();
        cartoonScale = module.getScaleProperty().getValue() == Scale.CARTOON;
        showMeasuringTape = module.getMeasuringTapeVisibleProperty().getValue();
    }

    public void apply( GravityAndOrbitsModule m ) {
        m.getShowGravityForceProperty().setValue( showGravityForce );
        m.getShowPathProperty().setValue( showPaths );
        m.getShowVelocityProperty().setValue( showVelocity );
        m.getShowMassProperty().setValue( showMass );
        m.getGravityEnabledProperty().setValue( gravityEnabled );
        for ( int i = 0; i < modeStates.size(); i++ ) {
            modeStates.get( i ).apply( m.getModes().get( i ) );
        }
        m.setModeIndex( selectedMode );
        m.getShowGridProperty().setValue( showGrid );
        m.getScaleProperty().setValue( cartoonScale ? Scale.CARTOON : Scale.REAL );
        m.getMeasuringTapeVisibleProperty().setValue( showMeasuringTape );
    }

    @Override
    public String toString() {
        return "GravityAndOrbitsModuleState{" +
               "showGravityForce=" + showGravityForce +
               ", showPaths=" + showPaths +
               ", showVelocity=" + showVelocity +
               ", showMass=" + showMass +
               ", gravityEnabled=" + gravityEnabled +
               ", modelStates=" + modeStates +
               '}';
    }
}
