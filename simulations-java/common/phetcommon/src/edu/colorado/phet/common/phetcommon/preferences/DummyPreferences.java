package edu.colorado.phet.common.phetcommon.preferences;

public class DummyPreferences implements IPreferences {
    public boolean isEnabledForSim() {
        return true;
    }

    public boolean isApplyToAllSimulations() {
        return true;
    }

    public void setApplyToAllSimulations( boolean selected ) {
    }

    public void setEnabledForSelection( boolean selected ) {
    }

    public boolean isEnabledForSelection() {
        return false;
    }
}
