// Decompiled by Jad v1.5.8f. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.kpdus.com/jad.html
// Decompiler options: packimports(3) 

package edu.colorado.phet.efield.gui;

import java.awt.*;
import java.util.Vector;

import javax.swing.*;

import edu.colorado.phet.efield.phys2d_efield.Particle;
import edu.colorado.phet.efield.phys2d_efield.System2D;

// Referenced classes of package edu.colorado.phet.efield.gui:
//            ParticlePainter, Painter

public class ParticlePanel extends JPanel {

    public ParticlePanel() {
        particles = new Vector();
        painters = new Vector();
        graphicsPainters = new Vector();
        postPainters = new Vector();
    }

    public void add( Painter painter ) {
        graphicsPainters.add( painter );
    }

    public int numParticles() {
        return particles.size();
    }

    public void removeParticle( int i ) {
        particles.remove( i );
        painters.remove( i );
    }

    public void remove( Particle particle ) {
        int i = particles.indexOf( particle );
        if ( i == -1 ) {
            return;
        }
        else {
            removeParticle( i );
            return;
        }
    }

    public Particle particleAt( int i ) {
        return (Particle) particles.get( i );
    }

    public ParticlePainter painterAt( int i ) {
        return (ParticlePainter) painters.get( i );
    }

    public void reset() {
        particles = new Vector();
        painters = new Vector();
    }

    public void addAll( System2D system2d, ParticlePainter particlepainter ) {
        for ( int i = 0; i < system2d.numParticles(); i++ ) {
            add( system2d.particleAt( i ), particlepainter );
        }

    }

    public void add( Particle particle, ParticlePainter particlepainter ) {
        particles.add( particle );
        painters.add( particlepainter );
    }

    public void paintComponent( Graphics g ) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
        super.paintComponent( g );
        Graphics2D graphics2d = (Graphics2D) g;
        for ( int i = 0; i < graphicsPainters.size(); i++ ) {
            Painter painter = (Painter) graphicsPainters.get( i );
            painter.paint( graphics2d );
        }

        for ( int j = 0; j < painters.size(); j++ ) {
            Particle particle = (Particle) particles.get( j );
            ParticlePainter particlepainter = (ParticlePainter) painters.get( j );
            particlepainter.paint( particle, graphics2d );
        }

        for ( int k = 0; k < postPainters.size(); k++ ) {
            Painter painter1 = (Painter) postPainters.get( k );
            painter1.paint( graphics2d );
        }

    }

    Vector particles;
    Vector painters;
    Vector graphicsPainters;
    Vector postPainters;
}
