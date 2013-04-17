// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.view;

import java.awt.*;
import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.selfdrivenparticlemodel.model.Particle;
import edu.umd.cs.piccolo.PNode;

public class ParticleGraphicWithTail extends PNode {
    private ParticleGraphic[] particleGraphics;
    private Particle p;
    private ArrayList storedLocations = new ArrayList();
    private int mod;

    public ParticleGraphicWithTail( Particle p ) {
        this( p, new double[] { 8, 7, 6, 5, 4 }, 1 );
    }

    public ParticleGraphicWithTail( Particle p, double[] segSizes, int mod ) {
        this.p = p;
        this.mod = mod;
        particleGraphics = new ParticleGraphic[segSizes.length];
        Color color = ParticleGraphic.newRandomColor();
        for ( int i = 0; i < segSizes.length; i++ ) {
            double segSize = segSizes[i];
            particleGraphics[i] = new ParticleGraphic( p, segSize, color );
            addChild( particleGraphics[i] );
        }
        p.addListener( new Particle.Listener() {
            public void locationChanged() {
                update();
            }

        } );
        for ( int i = 0; i < segSizes.length; i++ ) {
            update();
        }
    }

    private void update() {
        storedLocations.add( new Point2D.Double( p.getX(), p.getY() ) );
        while ( storedLocations.size() > particleGraphics.length * mod ) {
            storedLocations.remove( 0 );
        }
        for ( int i = 0; i < particleGraphics.length && i < storedLocations.size(); i += mod ) {
            ParticleGraphic particleGraphic = particleGraphics[i];
            Point2D loc = (Point2D) storedLocations.get( storedLocations.size() - 1 - i );
            particleGraphic.setOffset( loc );
        }
    }

    public Particle getParticle() {
        return p;
    }

    public void setColor( Color blue ) {
        for ( int i = 0; i < particleGraphics.length; i++ ) {
            ParticleGraphic particleGraphic = particleGraphics[i];
            particleGraphic.setColor( blue );
        }
    }
}
