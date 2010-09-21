package edu.colorado.phet.batteryvoltage.common.electron.man.laws;

import java.util.Vector;

import edu.colorado.phet.batteryvoltage.common.phys2d.Law;
import edu.colorado.phet.batteryvoltage.common.phys2d.System2D;

/*Uses the time step from a System2D for motion.*/

public class ManLaw implements Law {
    Vector movers = new Vector();
    double timeScale;

    public ManLaw( double timeScale ) {
        this.timeScale = timeScale;
    }

    public void add( ManMover mm ) {
        movers.add( mm );
    }

    public void iterate( double dt, System2D sys ) {
        for ( int i = 0; i < movers.size(); i++ ) {
            //o.O.p("Moving "+i);
            ManMover mm = (ManMover) movers.get( i );
            mm.move( dt * timeScale );
        }
    }
}
