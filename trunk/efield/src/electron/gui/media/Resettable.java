// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package electron.gui.media;

import electron.gui.ParticlePanel;
import phys2d.System2D;

public interface Resettable
{

    public abstract void fireResetAction(System2D system2d, ParticlePanel particlepanel);
}
