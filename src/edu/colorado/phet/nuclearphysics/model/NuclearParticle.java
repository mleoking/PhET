/**
 * Class: NuclearParticle
 * Package: edu.colorado.phet.nuclearphysics.model
 * Author: Another Guy
 * Date: Feb 26, 2004
 */
package edu.colorado.phet.nuclearphysics.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.coreadditions.Body;

import java.awt.geom.Point2D;

public class NuclearParticle extends Body {
//    private Point2D.Double position = new Point2D.Double();
    private double radius;

    public NuclearParticle( Point2D.Double location ) {
        super( location, new Vector2D(), new Vector2D(), 0f, 0f );
        this.radius = RADIUS;
    }

    public double getRadius() {
        return this.radius;
    }

    public Point2D.Double getCM() {
        return getLocation();
    }

    public double getMomentOfInertia() {
        return 0;
    }

    //
    // Statics
    //
    public final static double RADIUS = 5;
}
