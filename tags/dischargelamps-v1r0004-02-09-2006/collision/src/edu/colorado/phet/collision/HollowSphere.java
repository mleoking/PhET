// edu.colorado.phet.idealgas.model.body.HollowSphere
/*
 * User: Administrator
 * Date: Jan 5, 2003
 * Time: 8:11:48 AM
 */
package edu.colorado.phet.collision;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class HollowSphere extends SphericalBody {
    ArrayList containedBodies = new ArrayList();

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


    public List getContainedBodies() {
        return containedBodies;
    }

    public void addContainedBody( Body body ) {
        containedBodies.add( body );
    }

    public void removeContainedBody( Body body ) {
        containedBodies.remove( body );
    }

    public boolean containsBody( Body body ) {
        return containedBodies.contains( body );
    }

    public int numContainedBodies() {
        return containedBodies.size();
    }
}
