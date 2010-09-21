/* Copyright 2010, University of Colorado */

package edu.colorado.phet.neuron.model;

import java.awt.Color;
import java.awt.geom.Point2D;


/**
 * This class contains enough state information to support particle motion and
 * appearance for the playback feature.  It does NOT contain enough state
 * information to store everything about the particle such that it could
 * resume the simulation.  For instance, the particles motion strategy is
 * not stored.
 * 
 * @author John Blanco
 */
public class ParticlePlaybackMemento {

    private final Point2D position = new Point2D.Double();
    private final double opaqueness;
    private final ParticleType particleType;
    private final double radius;
    private final Color representationColor;
    
    /**
     * Constructor.
     * 
     * @param particle
     */
    public ParticlePlaybackMemento( Particle particle ) {
        super();
        position.setLocation( particle.getPositionReference() );
        opaqueness = particle.getOpaqueness();
        particleType = particle.getType();
        radius = particle.getRadius();
        representationColor = particle.getRepresentationColor();
    }
    
    protected Point2D getPositionRef() {
        return position;
    }
    
    protected double getOpaqueness() {
        return opaqueness;
    }
    
    protected ParticleType getParticleType() {
        return particleType;
    }

    protected double getRadius() {
        return radius;
    }
    
    protected Color getRepresentationColor() {
        return representationColor;
    }
}
