/* Copyright 2004, Sam Reid */
package org.reid.particles.view;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import org.reid.particles.model.Particle;
import org.reid.particles.model.ParticleModel;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Aug 10, 2005
 * Time: 12:18:56 AM
 * Copyright (c) Aug 10, 2005 by Sam Reid
 */

public class ParticleInfluenceGraphic extends PNode {
    private ParticlePanel particlePanel;
    private Particle p;
    private PPath child;

    public ParticleInfluenceGraphic( ParticlePanel particlePanel, Particle p ) {
        this.particlePanel = particlePanel;
        this.p = p;

//        Ellipse2D.Double shape = new Ellipse2D.Double( -5, -5, 10, 10 );
        double radius = getRadius();
        Ellipse2D.Double shape = new Ellipse2D.Double( -radius, -radius, radius * 2, radius * 2 );
        child = new PPath( shape );
        child.setStrokePaint( null );
//        child.setStroke( new PFixedWidthStroke( 1 ) );
        child.setPaint( Color.yellow );
        addChild( child );

        p.addListener( new Particle.Listener() {
            public void locationChanged() {
                update();
            }
        } );
        particlePanel.getParticleModel().addListener( new ParticleModel.Listener() {
            public void radiusChanged() {
                update();
            }
        } );
    }

    static Random random = new Random();

    private Paint newRandomColor() {
        float h = random.nextFloat();
        float s = random.nextFloat();
        float b = (float)( random.nextFloat() * 0.7 );
        Color color = Color.getHSBColor( h, s, b );
        return color;
    }

    private double getRadius() {
        double radius = particlePanel.getParticleModel().getRadius()/2.0;
        return radius;
    }

    private void update() {
        double radius = getRadius();
        Ellipse2D.Double shape = new Ellipse2D.Double( -radius, -radius, radius * 2, radius * 2 );
        child.setPathTo( shape );
        setOffset( p.getX(), p.getY() );
    }
}
