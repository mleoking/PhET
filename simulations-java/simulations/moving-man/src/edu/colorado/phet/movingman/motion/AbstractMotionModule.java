package edu.colorado.phet.movingman.motion;

import edu.colorado.phet.common.phetcommon.application.Module;
import edu.colorado.phet.common.phetcommon.model.clock.ConstantDtClock;
import edu.colorado.phet.common.phetcommon.view.util.PhetAudioClip;

/**
 * Created by: Sam
 * Dec 6, 2007 at 7:07:39 AM
 */
public class AbstractMotionModule extends Module implements ISoundObject {
    private boolean audioEnabled = true;

    public AbstractMotionModule( String name, ConstantDtClock clock ) {
        super( name, clock );
    }

    public boolean isAudioEnabled() {
        return audioEnabled;
    }

    public void setAudioEnabled( boolean selected ) {
        this.audioEnabled = selected;
    }

    public void playSound( String s ) {
        if ( audioEnabled ) {
            new PhetAudioClip( s ).play();
        }
    }
}
