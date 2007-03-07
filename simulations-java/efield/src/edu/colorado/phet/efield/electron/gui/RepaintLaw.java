// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package electron.gui;

import java.awt.Component;
import phys2d.Law;
import phys2d.System2D;

public class RepaintLaw
    implements Law
{

    public RepaintLaw(Component component)
    {
        panel = component;
    }

    public void iterate(double d, System2D system2d)
    {
        panel.repaint();
    }

    Component panel;
}
