package edu.colorado.phet.common.phetcommon.updates;

import edu.colorado.phet.common.phetcommon.preferences.PhetPreferences;

public class DefaultUpdateTimer implements IUpdateTimer {
    public void setTimerStartTime( String project, String name, long time ) {
        PhetPreferences.getInstance().setAskMeLater(project, name, time);
    }
}
