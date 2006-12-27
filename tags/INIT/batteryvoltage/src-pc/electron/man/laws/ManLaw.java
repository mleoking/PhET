package electron.man.laws;

import phys2d.Law;
import phys2d.System2D;

import java.util.Vector;

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
        for( int i = 0; i < movers.size(); i++ ) {
            //o.O.p("Moving "+i);
            ManMover mm = (ManMover)movers.get( i );
            mm.move( dt * timeScale );
        }
    }
}
