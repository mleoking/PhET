// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package electron.gui;

import java.awt.Graphics2D;
import java.awt.Point;
import phys2d.Particle;

public interface ParticlePainter
{

    public abstract void paint(Particle particle, Graphics2D graphics2d);

    public abstract boolean contains(Particle particle, Point point);
}
