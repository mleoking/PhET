// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.electron.gui;

import java.awt.*;

import edu.colorado.phet.efield.electron.phys2d_efield.Particle;

public interface ParticlePainter {

    public abstract void paint( Particle particle, Graphics2D graphics2d );

    public abstract boolean contains( Particle particle, Point point );
}
