package edu.colorado.phet.signalcircuit.phys2d.laws;

import edu.colorado.phet.signalcircuit.phys2d.Law;
import edu.colorado.phet.signalcircuit.phys2d.System2D;

import java.awt.*;

public class Validate implements Law {
    Container p;

    public Validate( Container p ) {
        this.p = p;
    }

    public void iterate( double time, System2D system ) {
        p.validate();
    }
}
