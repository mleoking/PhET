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

import edu.colorado.phet.mechanics.Body;

import java.util.ArrayList;
import java.awt.geom.Point2D;

/**
 * CompositeBody
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class CompositeBody extends Body {

    private ArrayList bodies = new ArrayList();
    private Point2D.Double cm = new Point2D.Double();

    public void addBody( Body body ) {
        bodies.add( body );
        setMass( getMass() + body.getMass() );
    }

    public Point2D getCM() {
        double xSum = 0;
        double ySum = 0;
        double massSum = 0;
        for( int i = 0; i < bodies.size(); i++ ) {
            Body body = (Body)bodies.get(i);
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

}
