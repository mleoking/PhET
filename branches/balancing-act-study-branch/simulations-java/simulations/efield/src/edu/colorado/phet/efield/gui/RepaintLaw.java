// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.efield.gui;

import java.awt.*;

import edu.colorado.phet.efield.phys2d_efield.Law;
import edu.colorado.phet.efield.phys2d_efield.System2D;

public class RepaintLaw
        implements Law {

    public RepaintLaw( Component component ) {
        panel = component;
    }

    public void iterate( double d, System2D system2d ) {
        panel.repaint();
    }

    Component panel;
}
