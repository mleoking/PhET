package phet.phys2d.laws;

import phet.phys2d.Law;
import phet.phys2d.System2D;

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
