// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryvoltage.common.phys2d.laws;

import java.awt.*;

import edu.colorado.phet.batteryvoltage.common.phys2d.Law;
import edu.colorado.phet.batteryvoltage.common.phys2d.System2D;

public class Repaint implements Law {
    Component p;

    public Repaint( Component p ) {
        this.p = p;
    }

    public void iterate( double time, System2D system ) {
        p.repaint();
    }
}
