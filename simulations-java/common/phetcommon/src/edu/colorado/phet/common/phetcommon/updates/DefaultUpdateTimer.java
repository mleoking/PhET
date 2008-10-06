package edu.colorado.phet.common.phetcommon.updates;

import edu.colorado.phet.common.phetcommon.preferences.PhetPreferences;

public class DefaultUpdateTimer implements IUpdateTimer {
    public void setLastAskMeLaterTime( long time ) {
        PhetPreferences.getInstance().setLastAskMeLaterTime(time);
    }
}
