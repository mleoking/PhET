// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.simsharing;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsMode;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;

/**
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

    public GravityAndOrbitsModuleState() {
    }

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

    public boolean isShowGravityForce() {
        return showGravityForce;
    }

    public void setShowGravityForce( boolean showGravityForce ) {
        this.showGravityForce = showGravityForce;
    }

    public boolean isShowPaths() {
        return showPaths;
    }

    public void setShowPaths( boolean showPaths ) {
        this.showPaths = showPaths;
    }

    public boolean isShowVelocity() {
        return showVelocity;
    }

    public void setShowVelocity( boolean showVelocity ) {
        this.showVelocity = showVelocity;
    }

    public boolean isShowMass() {
        return showMass;
    }

    public void setShowMass( boolean showMass ) {
        this.showMass = showMass;
    }

    public boolean isShowGrid() {
        return showGrid;
    }

    public void setShowGrid( boolean showGrid ) {
        this.showGrid = showGrid;
    }

    public boolean isGravityEnabled() {
        return gravityEnabled;
    }

    public void setGravityEnabled( boolean gravityEnabled ) {
        this.gravityEnabled = gravityEnabled;
    }

    public ArrayList<GravityAndOrbitsModeState> getModeStates() {
        return modeStates;
    }

    public void setModeStates( ArrayList<GravityAndOrbitsModeState> modeStates ) {
        this.modeStates = modeStates;
    }

    public int getSelectedMode() {
        return selectedMode;
    }

    public void setSelectedMode( int selectedMode ) {
        this.selectedMode = selectedMode;
    }

    public boolean isShowMeasuringTape() {
        return showMeasuringTape;
    }

    public void setShowMeasuringTape( boolean showMeasuringTape ) {
        this.showMeasuringTape = showMeasuringTape;
    }
}
