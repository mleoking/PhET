package edu.colorado.phet.common.gui;

import edu.colorado.phet.common.phys2d.Particle;
import edu.colorado.phet.common.phys2d.System2D;

import javax.swing.*;
import java.awt.*;
import java.util.Vector;

public class ParticlePanel extends JPanel {
    Vector particles = new Vector();
    Vector painters = new Vector();
    Vector graphicsPainters = new Vector();
    Vector postPainters = new Vector();

    public ParticlePanel() {
    }

    public boolean containsPostParticlePainter( Painter p ) {
        return postPainters.contains( p );
    }

    public void addPostParticlePainter( Painter p ) {
        postPainters.add( p );
    }

    public void removePostParticlePainter( Painter p ) {
        postPainters.remove( p );
    }

    public void add( Painter p ) {
        graphicsPainters.add( p );
    }

    public int numParticles() {
        return particles.size();
    }

    public void removeParticle( int i ) {
        //edu.colorado.phet.common.util.Debug.traceln("Removing particle: "+i);
        particles.remove( i );
        painters.remove( i );
    }

    public void remove( Particle p ) {
        int index = particles.indexOf( p );
        if( index == -1 ) {
            return;
        }
        removeParticle( index );
    }

    public Particle particleAt( int i ) {
        return (Particle)particles.get( i );
    }

    public void setPainter( ParticlePainter pp, Particle p ) {
        setPainter( pp, particles.indexOf( p ) );
    }

    public void setPainter( ParticlePainter pp, int i ) {
        painters.setElementAt( pp, i );
    }

    public ParticlePainter painterAt( int i ) {
        return (ParticlePainter)painters.get( i );
    }

    public void reset() {
        particles = new Vector();
        painters = new Vector();
    }

    public void addAll( System2D system, ParticlePainter pp ) {
        for( int i = 0; i < system.numParticles(); i++ ) {
            add( system.particleAt( i ), pp );
        }
    }

    public void add( Particle p, ParticlePainter pp ) {
        particles.add( p );
        painters.add( pp );
    }

    public void paintComponent( Graphics g ) {
        super.paintComponent( g );
        Graphics2D g2 = (Graphics2D)g;
        for( int i = 0; i < graphicsPainters.size(); i++ ) {
            Painter p = (Painter)graphicsPainters.get( i );
            //edu.colorado.phet.common.util.Debug.traceln("num graphics painters: "+graphicsPainters.size());
            //edu.colorado.phet.common.util.Debug.traceln("Painter["+i+"]="+p);
            p.paint( g2 );
        }
        for( int i = 0; i < painters.size(); i++ ) {
            Particle p = (Particle)particles.get( i );
            ParticlePainter pa = (ParticlePainter)painters.get( i );
            pa.paint( p, g2 );
        }
        for( int i = 0; i < postPainters.size(); i++ ) {
            Painter p = (Painter)postPainters.get( i );
            //edu.colorado.phet.common.util.Debug.traceln("num graphics painters: "+graphicsPainters.size());
            //edu.colorado.phet.common.util.Debug.traceln("Painter["+i+"]="+p);
            p.paint( g2 );
        }
    }
}
