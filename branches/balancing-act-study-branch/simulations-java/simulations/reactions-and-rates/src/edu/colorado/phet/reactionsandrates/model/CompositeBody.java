// Copyright 2002-2012, University of Colorado

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.reactionsandrates.model;

import java.awt.geom.Point2D;
import java.util.ArrayList;

import edu.colorado.phet.common.mechanics.Body;
import edu.colorado.phet.common.phetcommon.math.vector.MutableVector2D;

/**
 * CompositeBody
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CompositeBody extends Body {

    private ArrayList bodies = new ArrayList();
    private Point2D.Double cm = new Point2D.Double();
    private MutableVector2D velocity = new MutableVector2D();

    public void addBody( Body body ) {
        bodies.add( body );
        setMass( getMass() + body.getMass() );
    }

    public void removeBody( Body body ) {
        if ( bodies.contains( body ) ) {
            bodies.remove( body );
            setMass( getMass() - body.getMass() );
        }
    }

    public Point2D getCM() {
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
        Point2D cm = this.getCM();
        for ( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body) bodies.get( i );
            double dist = cm.distance( body.getCM() );
            double mOfIComponent = body.getMomentOfInertia() + body.getMass()
                                                               * dist * dist;
            mOfI += mOfIComponent;
        }
        return mOfI;
    }

    public MutableVector2D getVelocity() {
        velocity.setComponents( 0, 0 );
        double vx = 0;
        double vy = 0;
        for ( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body) bodies.get( i );
            vx += body.getVelocity().getX() * body.getMass() / this.getMass();
            vy += body.getVelocity().getY() * body.getMass() / this.getMass();
        }
        velocity.setComponents( vx, vy );
        return velocity;
    }
}
