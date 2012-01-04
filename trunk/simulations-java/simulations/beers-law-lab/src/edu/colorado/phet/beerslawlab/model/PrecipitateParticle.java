// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.beerslawlab.model;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * One particle that makes up the precipitate that forms on the bottom of the beaker.
 *
 * @author Chris Malley (cmalley@pixelzoom.com)
 */
public class PrecipitateParticle {

    public interface PrecipitateParticleListener {
        // Instructs listeners to remove the specified particle from the model.
        public void particleDeleted( PrecipitateParticle particle );
    }

    private final Point2D location; // location of the particle in the coordinate frame of the beaker
    private final Color color;
    private final double size;
    private final ArrayList<PrecipitateParticleListener> listeners;

    public PrecipitateParticle( Point2D location, Color color, double size ) {
        this.location = location;
        this.color = color;
        this.size = size;
        this.listeners = new ArrayList<PrecipitateParticleListener>();
    }

    public Point2D getLocation() {
        return new Point2D.Double( location.getX(), location.getY() );
    }

    public Color getColor() {
        return color;
    }

    public double getSize() {
        return size;
    }

    public void delete() {
        fireParticleDeleted();
    }

    public void addListener( PrecipitateParticleListener listener ) {
        listeners.add( listener );
    }

    private void removeListener( PrecipitateParticleListener listener ) {
        listeners.remove( listener );
    }

    private void fireParticleDeleted() {
        for ( PrecipitateParticleListener listener : new ArrayList<PrecipitateParticleListener>( listeners ) ) {
            listener.particleDeleted( this );
            removeListener( listener ); // automatically remove the listener, since the particle is being deleted
        }
    }
}
