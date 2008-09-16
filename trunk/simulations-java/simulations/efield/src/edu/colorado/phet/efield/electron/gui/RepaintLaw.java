// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.electron.gui;

import java.awt.*;

import edu.colorado.phet.efield.electron.phys2d_efield.Law;
import edu.colorado.phet.efield.electron.phys2d_efield.System2D;

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
