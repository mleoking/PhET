/* Copyright 2003-2004, University of Colorado */

/*
 * CVS Info -
 * Filename : $Source$
 * Branch : $Name$
 * Modified by : $Author$
 * Revision : $Revision$
 * Date modified : $Date$
 */
package edu.colorado.phet.common.util.persistence;

import java.awt.geom.Point2D;

/**
 * PersistentPoint2D
 * <p/>
 * This class is provided as a wrapper for Point2D for use in applications that
 * do persistence using XMLEncoder & XMLDecoder
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PersistentPoint2D extends Point2D {
    private Point2D point2D = new Double();

    public PersistentPoint2D() {
    }

    public PersistentPoint2D( Point2D point2D ) {
        this.point2D = point2D;
    }

    ////////////////////////////////////////////
    // Persistence setters and getters
    //
    public StateDescriptor getStateDescriptor() {
        return new StateDescriptor( point2D );
    }

    public void setStateDescriptor( StateDescriptor stateDescriptor ) {
        point2D = stateDescriptor.generate();
    }

    ////////////////////////////////////////////
    // Wrapper methods
    //
    public double distance( double PX, double PY ) {
        return point2D.distance( PX, PY );
    }

    public double distanceSq( double PX, double PY ) {
        return point2D.distanceSq( PX, PY );
    }

    public double distance( Point2D pt ) {
        return point2D.distance( pt );
    }

    public double distanceSq( Point2D pt ) {
        return point2D.distanceSq( pt );
    }

    public void setLocation( Point2D p ) {
        point2D.setLocation( p );
    }

    public boolean equals( Object obj ) {
        return point2D.equals( obj );
    }

    public void setLocation( double x, double y ) {
        point2D.setLocation( x, y );
    }

    public double getX() {
        return point2D.getX();
    }

    public void setX( double x ) {
        setLocation( x, point2D.getY() );
    }

    public double getY() {
        return point2D.getY();
    }

    public void setY( double y ) {
        setLocation( point2D.getX(), y );
    }

    //////////////////////////////////////
    // Inner classes
    //
    public static class StateDescriptor {
        private double x;
        private double y;

        public StateDescriptor() {
        }

        StateDescriptor( Point2D point2D ) {
            x = point2D.getX();
            y = point2D.getY();
        }

        ////////////////////////////////////
        // Generator
        //
        Point2D generate() {
            return new Point2D.Double( x, y );
        }

        ////////////////////////////////////
        // Persistence setters and getters
        //
        public double getX() {
            return x;
        }

        public void setX( double x ) {
            this.x = x;
        }

        public double getY() {
            return y;
        }

        public void setY( double y ) {
            this.y = y;
        }
    }
}
