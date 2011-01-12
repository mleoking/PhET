// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.simsharing.gravityandorbits;

import java.io.Serializable;
import java.util.ArrayList;

import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsMode;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;

/**
 * @author Sam Reid
 */
public class GravityAndOrbitsModuleState implements Serializable {

    private boolean showGravityForce = false;
    private boolean showPaths = false;
    private boolean showVelocity = false;
    private boolean showMass = false;
    private boolean gravityEnabled = false;
    private ArrayList<GravityAndOrbitsModeState> modeStates;

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
    }

    public void apply( GravityAndOrbitsModule gravityAndOrbitsModule ) {
        gravityAndOrbitsModule.getShowGravityForceProperty().setValue( showGravityForce );
        gravityAndOrbitsModule.getShowPathProperty().setValue( showPaths );
        gravityAndOrbitsModule.getShowVelocityProperty().setValue( showVelocity );
        gravityAndOrbitsModule.getShowMassProperty().setValue( showMass );
        gravityAndOrbitsModule.getGravityEnabledProperty().setValue( gravityEnabled );
        for ( int i = 0; i < modeStates.size(); i++ ) {
            modeStates.get( i ).apply( gravityAndOrbitsModule.getModes().get( i ) );
        }
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
