package edu.colorado.phet.electron.man.laws;

import edu.colorado.phet.electron.man.Man;
import edu.colorado.phet.electron.man.Motion;

/*Rules for moving a single man.*/

public class ThoughtfulMover extends ManMover {
    MotionChooser mc;

    public ThoughtfulMover( Man target, MotionChooser mc ) {
        super( target ); //Super Target
        this.mc = mc;
    }

    public void move( double dt ) {
        Motion m = mc.getMotion();
        if( m != null ) {
            m.update( dt, getMan() );
        }
    }
}
