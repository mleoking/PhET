// Copyright 2002-2011, University of Colorado
package edu.colorado.phet.sugarandsaltsolutions.micro.model.dynamics;

import java.awt.Shape;

import edu.colorado.phet.sugarandsaltsolutions.micro.model.OpenSite;
import edu.colorado.phet.sugarandsaltsolutions.micro.model.Particle;

/**
 * Data structure used when searching for a place for an ion to join a crystal
 *
 * @author Sam Reid
 */
public class CrystallizationMatch<T extends Particle> {

    //The particle used to test for a match
    public final T particle;

    //The site where the particle could join the crystal
    public final OpenSite<T> site;

    //The distance between the particle and the potential bonding site
    public final double distance;

    public CrystallizationMatch( T particle, OpenSite<T> site ) {
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