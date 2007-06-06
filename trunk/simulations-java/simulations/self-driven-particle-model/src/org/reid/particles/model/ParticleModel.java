/* Copyright 2004, Sam Reid */
package org.reid.particles.model;

import java.util.ArrayList;
import java.util.Random;

/**
 * User: Sam Reid
 * Date: Aug 10, 2005
 * Time: 12:05:22 AM
 * Copyright (c) Aug 10, 2005 by Sam Reid
 */

public class ParticleModel {
    private ArrayList particles = new ArrayList();
    private double radius = 20.0;
    private Random random = new Random();
    private double speed = 3.0;
//    private double angleRandomness = 0;//zero to 2pi
//    private double angleRandomness = Math.PI * 2 / 10;//zero to 2pi
    private double angleRandomness = Math.PI * 2;
    private double boxWidth;
    private double boxHeight;
    private boolean factorOutNetMovement = false;
    private ArrayList listeners = new ArrayList();

    public ParticleModel( double boxWidth, double boxHeight ) {
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
    }

    public void addParticle( Particle particle ) {
        particles.add( particle );
    }

    public void step( double dt ) {
        updateAngles();
        moveParticles();
        if( factorOutNetMovement ) {
            factorOutNetMovement();
        }
    }

    private void factorOutNetMovement() {
        double dx = 0;
        double dy = 0;
        for( int i = 0; i < particles.size(); i++ ) {
            Particle particle = (Particle)particles.get( i );
            dx += speed * Math.cos( particle.getAngle() );
            dy += speed * Math.sin( particle.getAngle() );
        }
        dx /= particles.size();
        dy /= particles.size();
        translateAll( -dx, -dy );
    }

    private void translateAll( double dx, double dy ) {
        for( int i = 0; i < particles.size(); i++ ) {
            Particle particle = (Particle)particles.get( i );
            particle.translate( dx, dy );

        }
    }

    private void moveParticles() {
        for( int i = 0; i < particles.size(); i++ ) {
            Particle particle = (Particle)particles.get( i );
            double dx = speed * Math.cos( particle.getAngle() );
            double dy = speed * Math.sin( particle.getAngle() );
            particle.translate( dx, dy );
        }
        wrapParticles();
    }

    private void wrapParticles() {
        for( int i = 0; i < particles.size(); i++ ) {
            Particle particle = (Particle)particles.get( i );
            double dx = 0;
            double dy = 0;
            if( particle.getX() < 0 ) {
                dx += getBoxWidth();
            }
            if( particle.getY() < 0 ) {
                dy += getBoxHeight();
            }
            if( particle.getX() > getBoxWidth() ) {
                dx -= getBoxWidth();
            }
            if( particle.getY() > getBoxHeight() ) {
                dy -= getBoxHeight();
            }
            particle.translate( dx, dy );
        }
    }

    public double getBoxWidth() {
        return boxWidth;
    }

    public double getBoxHeight() {
        return boxHeight;
    }

    private void updateAngles() {
        double[]newAngles = new double[particles.size()];
        for( int i = 0; i < particles.size(); i++ ) {
            Particle particle = (Particle)particles.get( i );
            Particle[]neighbors = getNeighborsInRadius( particle, radius );
            newAngles[i] = getNewAngle( particle, neighbors );
        }
        for( int i = 0; i < newAngles.length; i++ ) {
            double newAngle = newAngles[i];
            particleAt( i ).setAngle( newAngle );
        }
    }

    public Particle particleAt( int i ) {
        return (Particle)particles.get( i );
    }

    private double getNewAngle( Particle particle, Particle[] neighbors ) {
        double x = 0;
        double y = 0;
        for( int i = 0; i < neighbors.length; i++ ) {
            Particle neighbor = neighbors[i];
            x += Math.cos( neighbor.getAngle() );
            y += Math.sin( neighbor.getAngle() );
            //leave speed factored out.
        }
        double angle = Math.atan2( y, x );
//        System.out.println( "Considered " + neighbors.length + " neighbors:" );
//        System.out.println( "angle = " + angle );
        angle = addRandomness( angle );
        return angle;
    }

    private double addRandomness( double angle ) {
        double randomContribution = random.nextDouble() * angleRandomness - angleRandomness / 2.0;
        return randomContribution + angle;
    }

    private Particle[] getNeighborsInRadius( Particle particle, double radius ) {
        ArrayList all = new ArrayList();
        for( int i = 0; i < particles.size(); i++ ) {
            Particle particle1 = (Particle)particles.get( i );
            if( particle1.distance( particle ) <= radius ) {
                all.add( particle1 );
            }
        }
        return (Particle[])all.toArray( new Particle[0] );
    }

    public int numParticles() {
        return particles.size();
    }

    public double getAngleRandomness() {
        return angleRandomness;
    }

    public void setAngleRandomness( double angleRandomness ) {
        this.angleRandomness = angleRandomness;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius( double radius ) {
        if( this.radius != radius ) {
            this.radius = radius;
            for( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener)listeners.get( i );
                listener.radiusChanged();
            }
        }
    }

    public boolean isFactorOutNetMovement() {
        return factorOutNetMovement;
    }

    public void setFactorOutNetMovement( boolean factorOutNetMovement ) {
        this.factorOutNetMovement = factorOutNetMovement;
    }

    public static interface Listener {
        void radiusChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }
}
