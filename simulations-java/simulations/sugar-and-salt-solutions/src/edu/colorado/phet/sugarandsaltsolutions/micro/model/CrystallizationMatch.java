// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model;

/**
 * Data structure used when searching for a place for an ion to join a crystal
 *
 * @author Sam Reid
 */
public class CrystallizationMatch {
    //The particle used to test for a match
    public final Particle particle;

    //The site where the particle might bond to the crystal
    public final CrystalSite crystalSite;

    //The distance between the particle and the potential bonding site
    public final double distance;

    public CrystallizationMatch( Particle particle, CrystalSite crystalSite ) {
        this.particle = particle;
        this.crystalSite = crystalSite;
        this.distance = particle.position.get().minus( crystalSite.position ).getMagnitude();
    }

    @Override public String toString() {
        return "Match{" +
               "particle=" + particle +
               ", crystalSite=" + crystalSite +
               ", distance=" + distance +
               '}';
    }
}
