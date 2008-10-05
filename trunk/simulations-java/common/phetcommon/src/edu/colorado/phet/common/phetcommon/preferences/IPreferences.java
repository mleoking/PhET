package edu.colorado.phet.common.phetcommon.preferences;

public interface IPreferences {
    boolean isEnabledForSim();

    boolean isForAllSimulations();

    void setForAllSimulations( boolean selected );
}
