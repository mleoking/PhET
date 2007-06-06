/* Copyright 2004, Sam Reid */
package org.reid.particles.view;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
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
    private Particle p;
    private PPath child;

    public ParticleGraphic( Particle p ) {
        this( p, 8.0, newRandomColor() );
    }

    public ParticleGraphic( Particle p, double radius, Color color ) {
        this.p = p;

//        Ellipse2D.Double shape = new Ellipse2D.Double( -5, -5, 10, 10 );
//        int radius = 8;
        Ellipse2D.Double shape = new Ellipse2D.Double( -radius, -radius, radius * 2, radius * 2 );
        child = new PPath( shape );
//        child.setStroke( new BasicStroke( 1 ) );
//        child.setStrokePaint( Color.black );
        child.setStroke( null );
        child.setStrokePaint( null );
        child.setPaint( color );
        addChild( child );

        p.addListener( new Particle.Listener() {
            public void locationChanged() {
                update();
            }
        } );
        update();
    }

    static Random random = new Random();

    public static Color newRandomColor() {
        float h = random.nextFloat();
        float s = random.nextFloat();
        float b = (float)( (float)( random.nextFloat() * 0.8 ) + 0.07 );
        Color color = Color.getHSBColor( h, s, b );
        return color;
    }

    private void update() {
        setOffset( p.getX(), p.getY() );
    }

    public void setColor( Color blue ) {
        child.setPaint( blue );
    }
}
