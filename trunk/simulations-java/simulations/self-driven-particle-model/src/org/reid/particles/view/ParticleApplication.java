/* Copyright 2004, Sam Reid */
package org.reid.particles.view;

import edu.umd.cs.piccolo.activities.PActivity;
import org.reid.particles.model.Particle;
import org.reid.particles.model.ParticleModel;

import javax.swing.*;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Aug 10, 2005
 * Time: 12:20:12 AM
 * Copyright (c) Aug 10, 2005 by Sam Reid
 */

public class ParticleApplication {
    private JFrame frame;
    static final Random random = new Random();
    private int maxX = 600;
    private int maxY = 600;

    public ParticleApplication() {
        final ParticleModel model = new ParticleModel( maxX, maxY );
        for( int i = 0; i < 200; i++ ) {
            Particle p = new Particle( random.nextInt( maxX ), random.nextInt( maxY ), random.nextDouble() * Math.PI * 2 );
            model.addParticle( p );
        }

        ParticlePanel panel = new ParticlePanel( model );
        frame = new JFrame( "Self-Driven Particles" );
        frame.setContentPane( panel );
        frame.setSize( 900, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        panel.getRoot().addActivity( new PActivity( -1 ) {
            protected void activityStep( long elapsedTime ) {
                super.activityStep( elapsedTime );
                model.step( 1.0 );
            }
        } );
    }

    public static void main( String[] args ) {
        new ParticleApplication().start();
    }

    private void start() {
        frame.show();
    }
}
