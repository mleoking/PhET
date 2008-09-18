package edu.colorado.phet.batteryvoltage.common.electron.man.motions;

import edu.colorado.phet.batteryvoltage.common.electron.man.Man;
import edu.colorado.phet.batteryvoltage.common.electron.man.Motion;
import edu.colorado.phet.batteryvoltage.common.phys2d.DoublePoint;

public class TranslateToLocation implements Motion {
    Location loc;
    double speed;

    public TranslateToLocation( Location loc, double speed ) {
        this.speed = speed;
        this.loc = loc;
    }

    public void setLocation( Location loc ) {
        this.loc = loc;
    }

    public void update( double dt, Man m ) {
        DoublePoint x = loc.getPosition();
        try {
            DoublePoint dir = x.subtract( m.getNeck().getPosition() ).normalize();
            double dx = dt * dir.getX() * speed;
            double dy = dt * dir.getY() * speed;

            m.getNeck().translate( dx, dy );
        }
        catch( Exception e ) {
        }//got too close.
    }
}
