package edu.colorado.phet.common.gui;

import edu.colorado.phet.common.phys2d.Law;
import edu.colorado.phet.common.phys2d.System2D;

import java.awt.*;

public class RepaintLaw implements Law {
    Component panel;

    public RepaintLaw( Component c ) {
        this.panel = c;
    }

    public void iterate( double dt, System2D sys ) {
        panel.repaint();
    }
}
