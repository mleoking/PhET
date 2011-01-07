// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.selfdrivenparticlemodel.model;

import java.util.ArrayList;

public class Particle {
    private double x;
    private double y;
    private double angle;
    private ArrayList listeners = new ArrayList();

    public Particle( double x, double y, double angle ) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public void translate( double dx, double dy ) {
        setLocation( x + dx, y + dy );
    }

    public static interface Listener {
        void locationChanged();
    }

    public void addListener( Listener listener ) {
        listeners.add( listener );
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public void setLocation( double x, double y ) {
        if( this.x != x || this.y != y ) {
            this.x = x;
            this.y = y;
            notifyLocationChanged();
        }
    }

    private void notifyLocationChanged() {
        for( int i = 0; i < listeners.size(); i++ ) {
            Listener listener = (Listener)listeners.get( i );
            listener.locationChanged();
        }
    }

    public double distance( Particle particle ) {
        double dx = this.x - particle.x;
        double dy = this.y - particle.y;
        return Math.sqrt( dx * dx + dy * dy );
    }

    public void setAngle( double angle ) {
        this.angle = angle;
    }

    public double getAngle() {
        return angle;
    }
}
