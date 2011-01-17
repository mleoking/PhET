// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import edu.colorado.phet.common.phetcommon.math.ImmutableVector2D;
import edu.colorado.phet.common.phetcommon.math.Vector2D;

public class ParticleModel {
    private ArrayList<Particle> particles = new ArrayList<Particle>();
    private double radius = 60.0;
    private Random random = new Random();
    private double speed = 5.0;
    private double randomness = Math.PI * 2;
    private double boxWidth;
    private double boxHeight;
    private boolean factorOutNetMovement = false;
    private ArrayList<Listener> listeners = new ArrayList<Listener>();
    private int maxClusterID;
    private long time;
    private boolean doCountClusters;

    public ParticleModel( double boxWidth, double boxHeight ) {
        this.boxWidth = boxWidth;
        this.boxHeight = boxHeight;
    }

    public void addParticle( Particle particle ) {
        particles.add( particle );
        notifyCountChanged();
    }

    public void step( double dt ) {
        updateAngles();
        moveParticles( dt );
        if ( factorOutNetMovement ) {
            factorOutNetMovement();
        }
        if ( doCountClusters ) {
            int[] clusters = new ClusterAssignment().assignClusters( this );

            Arrays.sort( clusters );
            maxClusterID = clusters.length > 0 ? clusters[clusters.length - 1] : -1;
        }
        time++;
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = listeners.get( i );
            listener.steppedInTime();
        }

    }

    private void factorOutNetMovement() {
        double dx = 0;
        double dy = 0;
        for ( int i = 0; i < particles.size(); i++ ) {
            Particle particle = (Particle) particles.get( i );
            dx += speed * Math.cos( particle.getAngle() );
            dy += speed * Math.sin( particle.getAngle() );
        }
        dx /= particles.size();
        dy /= particles.size();
        translateAll( -dx, -dy );
    }

    private void translateAll( double dx, double dy ) {
        for ( int i = 0; i < particles.size(); i++ ) {
            Particle particle = (Particle) particles.get( i );
            particle.translate( dx, dy );

        }
    }

    private void moveParticles( double dt ) {
        for ( int i = 0; i < particles.size(); i++ ) {
            Particle particle = (Particle) particles.get( i );
            double dx = speed * Math.cos( particle.getAngle() ) * dt;
            double dy = speed * Math.sin( particle.getAngle() ) * dt;
            particle.translate( dx, dy );
        }
        wrapParticles();
    }

    private void wrapParticles() {
        for ( int i = 0; i < particles.size(); i++ ) {
            Particle particle = (Particle) particles.get( i );
            double dx = 0;
            double dy = 0;
            if ( particle.getX() < 0 ) {
                dx += getBoxWidth();
            }
            if ( particle.getY() < 0 ) {
                dy += getBoxHeight();
            }
            if ( particle.getX() > getBoxWidth() ) {
                dx -= getBoxWidth();
            }
            if ( particle.getY() > getBoxHeight() ) {
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
        double[] newAngles = new double[particles.size()];
        for ( int i = 0; i < particles.size(); i++ ) {
            Particle particle = (Particle) particles.get( i );
            Particle[] neighbors = getNeighborsInRadius( particle );
            newAngles[i] = getNewAngle( particle, neighbors );
        }
        for ( int i = 0; i < newAngles.length; i++ ) {
            double newAngle = newAngles[i];
            particleAt( i ).setAngle( newAngle );
        }
    }

    public Particle particleAt( int i ) {
        return (Particle) particles.get( i );
    }

    public long getTime() {
        return time;
    }

    private double getNewAngle( Particle particle, Particle[] neighbors ) {
        double x = 0;
        double y = 0;
        for ( int i = 0; i < neighbors.length; i++ ) {
            Particle neighbor = neighbors[i];
            x += Math.cos( neighbor.getAngle() );
            y += Math.sin( neighbor.getAngle() );
            //leave speed factored out.
        }
        double angle = Math.atan2( y, x );
        angle = addRandomness( angle );
        return angle;
    }

    private double addRandomness( double angle ) {
        double randomContribution = random.nextDouble() * randomness - randomness / 2.0;
        return randomContribution + angle;
    }

    public Particle[] getNeighborsInRadius( Particle particle ) {
        return getNeighborsInRadius( particle, radius );
    }

    private Particle[] getNeighborsInRadius( Particle particle, double radius ) {
        ArrayList all = new ArrayList();
        for ( int i = 0; i < particles.size(); i++ ) {
            Particle particle1 = (Particle) particles.get( i );
            if ( particle1.distance( particle ) <= radius ) {
                all.add( particle1 );
            }
        }
        return (Particle[]) all.toArray( new Particle[0] );
    }

    public int numParticles() {
        return particles.size();
    }

    public double getRandomness() {
        return randomness;
    }

    public void setRandomness( double randomness ) {
        if ( this.randomness != randomness ) {
            this.randomness = randomness;
            for ( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener) listeners.get( i );
                listener.randomnessChanged();
            }
        }
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius( double radius ) {
        if ( this.radius != radius ) {
            this.radius = radius;
            for ( int i = 0; i < listeners.size(); i++ ) {
                Listener listener = (Listener) listeners.get( i );
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

    public double getSpeed() {
        return speed;
    }

    public void setSpeed( double speed ) {
        this.speed = speed;
    }

    public int indexOf( Particle particle ) {
        return particles.indexOf( particle );
    }

    public void removeParticle( Particle particle ) {
        particles.remove( particle );
        notifyCountChanged();
    }

    private void notifyCountChanged() {
        for ( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener) listeners.get( i );
            listener.particleCountChanged();
        }
    }

    public Particle lastParticle() {
        return particleAt( numParticles() - 1 );
    }

    public int getNumClusters() {
        return maxClusterID + 1;
    }

    public void randomize() {
        for ( int i = 0; i < particles.size(); i++ ) {
            Particle particle = (Particle) particles.get( i );
            particle.setAngle( random.nextDouble() * Math.PI * 2 );
            particle.setLocation( random.nextDouble() * boxWidth, random.nextDouble() * boxHeight );
        }
    }

    public void removeListener( Listener listener ) {
        while ( listeners.contains( listener ) ) {
            listeners.remove( listener );
        }
    }

    public void resetTime() {
        time = 0;
    }

    public void setComputeClusterCount( boolean doCountClusters ) {
        this.doCountClusters = doCountClusters;
    }

    public static interface Listener {
        void radiusChanged();

        void randomnessChanged();

        void steppedInTime();

        void particleCountChanged();
    }

    public static class Adapter implements Listener {

        public void radiusChanged() {
        }

        public void randomnessChanged() {
        }

        public void steppedInTime() {

        }

        public void particleCountChanged() {
        }
    }

    public void addListener( Listener listener ) {
        if ( !listeners.contains( listener ) ) {
            listeners.add( listener );
        }
    }

    public double getOrderParameter() {
        Vector2D sum = new Vector2D();
        for ( int i = 0; i < particles.size(); i++ ) {
            Particle particle = (Particle) particles.get( i );
            ImmutableVector2D velocityVector = getVelocity( particle );
            sum.add( velocityVector );
        }
        return sum.getMagnitude() / numParticles() / speed;
    }

    private ImmutableVector2D getVelocity( Particle particle ) {
        return Vector2D.parseAngleAndMagnitude( speed, particle.getAngle() );
    }
}
