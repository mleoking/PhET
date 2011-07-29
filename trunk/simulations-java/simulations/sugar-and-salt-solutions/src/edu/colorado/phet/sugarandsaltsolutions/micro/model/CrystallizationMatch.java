// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

import java.awt.Shape;

/**
 * Data structure used when searching for a place for an ion to join a crystal
 *
 * @author Sam Reid
 */
public class CrystallizationMatch {

    //The particle used to test for a match
    public final Particle particle;

    //The site where the particle could join the crystal
    public final OpenSite<SphericalParticle> site;

    //The distance between the particle and the potential bonding site
    public final double distance;

    public CrystallizationMatch( Particle particle, OpenSite<SphericalParticle> site ) {
        this.particle = particle;
        this.site = site;
        this.distance = particle.getPosition().minus( site.absolutePosition ).getMagnitude();
    }

    @Override public String toString() {
        return "CrystallizationMatch{" +
               "particle=" + particle +
               ", constituent=" + site +
               ", distance=" + distance +
               '}';
    }

    //The absolute model shape (in meters) of where the binding site is, for purposes of debugging and making sure it is within the water shape
    public Shape getTargetShape() {
        return site.shape;
    }
}