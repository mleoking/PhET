/**
 * Class: CompositeBody
 * Package: edu.colorado.phet.coreadditions
 * Author: Another Guy
 * Date: Aug 26, 2003
 */
package edu.colorado.phet.microwaves.coreadditions;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class CompositeBody extends Body {

    private ArrayList bodies = new ArrayList();
    private Point2D.Double cm = new Point2D.Double();

    public void addBody( Body body ) {
        bodies.add( body );
        setMass( getMass() + body.getMass() );
    }

    public Point2D.Double getCM() {
        double xSum = 0;
        double ySum = 0;
        double massSum = 0;
        for ( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body) bodies.get( i );
            double mass = body.getMass();
            xSum += mass * body.getCM().getX();
            ySum += mass * body.getCM().getY();
            massSum += mass;
        }
        cm.setLocation( xSum / massSum, ySum / massSum );
        return cm;
    }

    public double getMomentOfInertia() {
        double mOfI = 0;
        Point2D.Double cm = this.getCM();
        for ( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body) bodies.get( i );
            double dist = cm.distance( body.getCM() );
            double mOfIComponent = body.getMomentOfInertia() + body.getMass()
                                                               * dist * dist;
            mOfI += mOfIComponent;
        }
        return mOfI;
    }

}
