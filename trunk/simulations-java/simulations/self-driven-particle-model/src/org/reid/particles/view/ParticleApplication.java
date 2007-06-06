/* Copyright 2004, Sam Reid */
package org.reid.particles.view;

import edu.umd.cs.piccolo.activities.PActivity;
import org.reid.particles.model.Particle;
import org.reid.particles.model.ParticleModel;

import javax.swing.*;
import java.lang.reflect.InvocationTargetException;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Aug 10, 2005
 * Time: 12:20:12 AM
 * Copyright (c) Aug 10, 2005 by Sam Reid, comment
 */

public class ParticleApplication implements IParticleApp {
    private JFrame frame;
    static final Random random = new Random();
    private int maxX = 600;
    private int maxY = 600;
    private ParticleModel particleModel;
    private ParticlePanel particlePanel;

    public ParticleApplication() {
        particleModel = new ParticleModel( maxX, maxY );
        particlePanel = new ParticlePanel( particleModel, this );
        frame = new JFrame( "Self-Driven Particles" );
        frame.setContentPane( particlePanel );
        frame.setSize( 900, 600 );
        frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        particlePanel.getRoot().addActivity( new PActivity( -1 ) {
            protected void activityStep( long elapsedTime ) {
                super.activityStep( elapsedTime );
                particleModel.step( 1.0 );
            }
        } );

        for( int i = 0; i < 100; i++ ) {
//        for( int i = 0; i < 3; i++ ) {
            addParticle();
        }
    }

    private void addParticle() {
        Particle p = new Particle( random.nextInt( maxX ), random.nextInt( maxY ), random.nextDouble() * Math.PI * 2 );
        particleModel.addParticle( p );
        particlePanel.addGraphic( p );
    }

    public static void main( String[] args ) throws InvocationTargetException, InterruptedException {
        SwingUtilities.invokeAndWait( new Runnable() {
            public void run() {
                ParticleApplication particleApplication = new ParticleApplication();
                particleApplication.start();

//                JFreeChartExperiment jFreeChartExperiment = new JFreeChartExperiment( particleApplication );
            }
        } );

    }

    private void start() {
        frame.show();
    }

    public void setNumberParticles( int numParticles ) {
        while( numParticles < particleModel.numParticles() ) {
            removeParticle( particleModel.lastParticle() );
        }
        while( numParticles > particleModel.numParticles() ) {
            addParticle();
        }
    }

    private void removeParticle( Particle particle ) {
        particleModel.removeParticle( particle );
        particlePanel.removeParticle( particle );
    }

    public ParticlePanel getParticlePanel() {
        return particlePanel;
    }

    public ParticleModel getParticleModel() {
        return particleModel;
    }
}
