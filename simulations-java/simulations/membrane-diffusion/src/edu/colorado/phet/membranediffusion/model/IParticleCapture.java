/* Copyright 2010, University of Colorado */

package edu.colorado.phet.membranediffusion.model;

import java.awt.geom.Rectangle2D;

/**
 * Interface used to command the capturing of particles.  This is intended to
 * be used by membrane channels to tell the model that a particle should be
 * captured for movement through the channel.
 * 
 * @author John Blanco
 */
public interface IParticleCapture {

    CaptureZoneScanResult scanCaptureZoneForFreeParticles(CaptureZone zone, ParticleType particleType);
    Rectangle2D getUpperParticleChamberRect();
    Rectangle2D getLowerParticleChamberRect();
}
