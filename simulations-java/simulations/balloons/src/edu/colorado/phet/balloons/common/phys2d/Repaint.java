package edu.colorado.phet.balloons.common.phys2d;

import edu.colorado.phet.balloons.common.phys2d.Law;
import edu.colorado.phet.balloons.common.phys2d.System2D;

import java.awt.*;

public class Repaint implements Law {
    Component p;

    public Repaint( Component p ) {
        this.p = p;
    }

    public void iterate( double time, System2D system ) {
        p.repaint();
    }
}
