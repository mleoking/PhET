// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.efield.core;

import edu.colorado.phet.efield.gui.ParticlePainter;
import edu.colorado.phet.efield.gui.ParticlePanel;
import edu.colorado.phet.efield.phys2d_efield.System2D;

public interface SystemFactory {

    public abstract System2D newSystem();

    public abstract void updatePanel( ParticlePanel particlepanel, System2D system2d, ParticlePainter particlepainter );
}
