// edu.colorado.phet.idealgas.physics.body.HollowSphere
/*
 * User: Administrator
 * Date: Jan 5, 2003
 * Time: 8:11:48 AM
 */
package edu.colorado.phet.idealgas.model;

import edu.colorado.phet.common.math.Vector2D;

import java.awt.geom.Point2D;

public class HollowSphere extends SphericalBody {

    public HollowSphere( Point2D center,
                         Vector2D velocity,
                         Vector2D acceleration,
                         double mass,
                         double radius ) {
        super( center, velocity, acceleration, mass, radius );
    }

    // The following are used for debug purposes only. It allows the contact point in a
    // collison to be displayed on the screen.
    public Point2D contactPt;

    public void setContactPt( Point2D.Double contactPt ) {
        this.contactPt = contactPt;
        notifyObservers();
    }
}
