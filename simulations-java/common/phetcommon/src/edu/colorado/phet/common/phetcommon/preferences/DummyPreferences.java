package edu.colorado.phet.common.phetcommon.preferences;

public class DummyPreferences implements IPreferences {
    public boolean isEnabledForSim() {
        return true;
    }

    public boolean isForAllSimulations() {
        return true;
    }

    public void setForAllSimulations( boolean selected ) {
    }
}
