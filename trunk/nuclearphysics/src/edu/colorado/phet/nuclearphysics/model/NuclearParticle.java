/**
 * Class: NuclearParticle
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

public class NuclearParticle extends NuclearModelElement {
    private double radius;

    public NuclearParticle( Point2D location ) {
        super( location, new Vector2D.Double(), new Vector2D.Double(), 0f, 0f );
        this.radius = RADIUS;
    }

    public double getRadius() {
        return this.radius;
    }

    public Point2D getCM() {
        return getPosition();
    }

    public double getMomentOfInertia() {
        return 0;
    }

    //
    // Statics
    //
    public final static double RADIUS = 5;
}
