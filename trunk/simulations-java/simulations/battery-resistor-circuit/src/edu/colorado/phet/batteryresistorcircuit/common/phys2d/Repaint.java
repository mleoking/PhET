// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.batteryresistorcircuit.common.phys2d;

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
