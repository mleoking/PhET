package edu.colorado.phet.common.phetcommon.application;

import edu.colorado.phet.common.phetcommon.resources.PhetVersion;
import edu.colorado.phet.common.phetcommon.preferences.ITrackingInfo;

public interface ISimInfo extends ITrackingInfo {
    String getTitle();
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
