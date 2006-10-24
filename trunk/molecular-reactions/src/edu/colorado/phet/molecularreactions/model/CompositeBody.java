/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.molecularreactions.model;

import edu.colorado.phet.common.math.Vector2D;
import edu.colorado.phet.mechanics.Body;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * CompositeBody
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CompositeBody extends Body {

    private ArrayList bodies = new ArrayList();
    private Point2D.Double cm = new Point2D.Double();
    private Vector2D velocity = new Vector2D.Double();

    public void addBody( Body body ) {
        bodies.add( body );
        setMass( getMass() + body.getMass() );
    }

    public void removeBody( Body body ) {
        if( bodies.contains( body ) ) {
            bodies.remove( body );
            setMass( getMass() - body.getMass() );
        }
    }

    public Point2D getCM() {
        double xSum = 0;
        double ySum = 0;
        double massSum = 0;
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
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
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            double dist = cm.distance( body.getCM() );
            double mOfIComponent = body.getMomentOfInertia() + body.getMass()
                                                               * dist * dist;
            mOfI += mOfIComponent;
        }
        return mOfI;
    }

    public Vector2D getVelocity() {
        velocity.setComponents( 0, 0 );
        double vx = 0;
        double vy = 0;
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get( i );
            vx += velocity.getX() + ( body.getVelocity().getX() * body.getMass() / this.getMass() );
            vy += velocity.getY() + ( body.getVelocity().getY() * body.getMass() / this.getMass() );
        }
        velocity.setComponents( vx, vy );
        return velocity;
    }
}
