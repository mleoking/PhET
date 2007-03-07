// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.electron.core;

import edu.colorado.phet.efield.electron.gui.ParticlePainter;
import edu.colorado.phet.efield.electron.gui.ParticlePanel;
import phys2d_efield.System2D;

public interface SystemFactory {

    public abstract System2D newSystem();

    public abstract void updatePanel( ParticlePanel particlepanel, System2D system2d, ParticlePainter particlepainter );
}
