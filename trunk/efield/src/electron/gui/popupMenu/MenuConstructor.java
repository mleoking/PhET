// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package electron.gui.popupMenu;

import javax.swing.JMenu;
import phys2d.Particle;

public interface MenuConstructor
{

    public abstract JMenu getMenu(Particle particle);
}
