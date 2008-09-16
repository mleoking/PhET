// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.gui.popupMenu;

import javax.swing.*;

import edu.colorado.phet.efield.phys2d_efield.Particle;

public interface MenuConstructor {

    public abstract JMenu getMenu( Particle particle );
}
