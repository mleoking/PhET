// Copyright 2002-2011, University of Colorado

package edu.colorado.phet.efield.gui;

import java.awt.*;

import edu.colorado.phet.efield.phys2d_efield.Particle;

public interface ParticlePainter {

    public abstract void paint( Particle particle, Graphics2D graphics2d );

    public abstract boolean contains( Particle particle, Point point );
}
