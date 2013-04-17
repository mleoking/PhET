// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.efield.gui.popupMenu;

import javax.swing.*;

import edu.colorado.phet.efield.phys2d_efield.Particle;

public interface MenuConstructor {

    public abstract JMenu getMenu( Particle particle );
}
