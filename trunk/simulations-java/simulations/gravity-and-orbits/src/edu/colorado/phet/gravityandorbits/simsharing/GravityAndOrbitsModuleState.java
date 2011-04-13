// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.gravityandorbits.simsharing;

import java.util.ArrayList;

import edu.colorado.phet.common.phetcommon.util.IProguardKeepClass;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsMode;
import edu.colorado.phet.gravityandorbits.module.GravityAndOrbitsModule;

//REVIEW class doc

/**
 * Serializable state for simsharing
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

    public GravityAndOrbitsModuleState() {
    }

    public GravityAndOrbitsModuleState( GravityAndOrbitsModule module ) {
        showGravityForce = module.showGravityForceProperty.getValue();
        showPaths = module.showPathProperty.getValue();
        showVelocity = module.showVelocityProperty.getValue();
        showMass = module.showMassProperty.getValue();
        gravityEnabled = module.gravityEnabledProperty.getValue();
        modeStates = new ArrayList<GravityAndOrbitsModeState>();
        for ( GravityAndOrbitsMode mode : module.getModes() ) {
            modeStates.add( new GravityAndOrbitsModeState( mode ) );
        }
        selectedMode = module.getModeIndex();
        showGrid = module.showGridProperty.getValue();
        showMeasuringTape = module.measuringTapeVisibleProperty.getValue();
    }

    public void apply( GravityAndOrbitsModule m ) {
        m.showGravityForceProperty.setValue( showGravityForce );
        m.showPathProperty.setValue( showPaths );
        m.showVelocityProperty.setValue( showVelocity );
        m.showMassProperty.setValue( showMass );
        m.gravityEnabledProperty.setValue( gravityEnabled );
        for ( int i = 0; i < modeStates.size(); i++ ) {
            modeStates.get( i ).apply( m.getModes().get( i ) );
        }
        m.setModeIndex( selectedMode );
        m.showGridProperty.setValue( showGrid );
        m.measuringTapeVisibleProperty.setValue( showMeasuringTape );
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
