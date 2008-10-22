package edu.colorado.phet.common.phetcommon.application;

import edu.colorado.phet.common.phetcommon.resources.PhetVersion;

public interface ISimInfo {
    String getName();
    String getDescription();
    PhetVersion getVersion();
    String getCredits();
    boolean isUpdatesEnabled();
    boolean isTrackingEnabled();
    String getProjectName();
    String getFlavor();
    String[] getCommandLineArgs();
    boolean isDev();
    long getSimStartTimeMillis();
}
