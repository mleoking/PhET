// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.electron.gui.media;

import edu.colorado.phet.efield.electron.gui.ParticlePanel;
import phys2d_efield.System2D;

public interface Resettable
{

    public abstract void fireResetAction(System2D system2d, ParticlePanel particlepanel);
}
