
package edu.colorado.phet.neuron.model;

import java.awt.Color;
import java.awt.geom.Point2D;

/**
 * Interface for a particle that can be viewed, i.e. displayed to the user.
 * 
 * @author John Blanco
 */
public interface IViewableParticle {

    public abstract ParticleType getType();

    public abstract Point2D getPosition();

    public abstract Point2D getPositionReference();

    public abstract double getOpaqueness();

    /**
     * This is called to remove this particle from the model.  It simply sends
     * out a notification of removal, and all listeners (including the view)
     * are expected to act appropriately and to remove all references.
     */
    public abstract void removeFromModel();

    /**
     * Get the radius of this particle in nano meters.  This is approximate in
     * the case of non-round particles.
     */
    public abstract double getRadius();

    /**
     * Get the base color to be used when representing this particle.
     */
    abstract public Color getRepresentationColor();

    public abstract void addListener( IParticleListener listener );

    public abstract void removeListener( IParticleListener listener );

}