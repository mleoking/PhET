/* Copyright 2004, Sam Reid */
package org.reid.particles.view;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolox.util.PFixedWidthStroke;
import org.reid.particles.model.Particle;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Aug 10, 2005
 * Time: 12:18:56 AM
 * Copyright (c) Aug 10, 2005 by Sam Reid
 */

public class ParticleGraphic extends PNode {
    private ParticlePanel particlePanel;
    private Particle p;

    public ParticleGraphic( ParticlePanel particlePanel, Particle p ) {
        this.particlePanel = particlePanel;
        this.p = p;

//        Ellipse2D.Double shape = new Ellipse2D.Double( -5, -5, 10, 10 );
        int radius=8;
        Ellipse2D.Double shape = new Ellipse2D.Double( -radius, -radius, radius*2,radius*2);
        PPath child = new PPath( shape );
        child.setStroke( new PFixedWidthStroke( 1 ) );
        child.setStrokePaint( newRandomColor() );
        child.setPaint( null);
        addChild( child );

        p.addListener( new Particle.Listener() {
            public void locationChanged() {
                update();
            }
        } );
    }

    static Random random = new Random();

    private Paint newRandomColor() {
        float h = random.nextFloat();
        float s = random.nextFloat();
        float b = (float)( random.nextFloat()*0.7 );
        Color color = Color.getHSBColor( h, s, b );
        return color;
    }

    private void update() {
        setOffset( p.getX(), p.getY() );
    }
}
