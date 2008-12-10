package edu.colorado.phet.common.phetcommon.application;

import edu.colorado.phet.common.phetcommon.resources.PhetVersion;

public interface ISimInfo {
    
    String getName();
    String getDescription();
    PhetVersion getVersion();
    String getCredits();
    
    /**
     * Should the updates feature be included at runtime?
     * @return
     */
    boolean isUpdatesFeatureIncluded();
    
    /**
     * Should the tracking feature be included at runtime?
     * @return
     */
    boolean isTrackingFeatureIncluded();
    
    /**
     * Is automatic checking for updates enabled?
     * This is based on the users preferences and whether the feature is included at runtime.
     * @return
     */
    boolean isUpdatesEnabled();
    
    /**
     * Is tracking enabled?
     * This is based on the users preferences and whether the feature is included at runtime.
     * @return
     */
    boolean isTrackingEnabled();
    
    /**
     * Should the user have access to the Preferences dialog?
     * This is true if at least one of the features in the preferences dialog is included at runtime.
     * @return
     */
    boolean isPreferencesEnabled();
    
    String getProjectName();
    String getFlavor();
    String[] getCommandLineArgs();
    
    /**
     * Was this sim run in developer mode (-dev program arg)?
     * @return
     */
    boolean isDev();
    
    long getSimStartTimeMillis();
    String getLocaleString();
    Integer getSessionCount();
}
