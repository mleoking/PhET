package edu.colorado.phet.common.phetcommon.preferences;

public interface IPreferences {

    boolean isApplyToAllSimulations();

    void setApplyToAllSimulations( boolean selected );

    void setEnabledForSelection( boolean selected );

    boolean isEnabledForSelection();
}
