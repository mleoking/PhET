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

import sun.security.krb5.internal.x;

import java.awt.geom.*;
import java.awt.*;

/**
 * PersistentRectangle2D
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PersistentRectangle2D extends Rectangle2D {
    private Rectangle2D rect = new Double();

    public PersistentRectangle2D() {
    }

    public PersistentRectangle2D( Rectangle2D rect ) {
        this.rect = rect;
    }

    //////////////////////////////////////////
    // Persistence getters and setters
    //
    public StateDescriptor getStateDescriptor() {
        return new StateDescriptor( rect );
    }

    public void setStateDescriptor( StateDescriptor stateDescriptor ) {
        rect = stateDescriptor.generate();
    }

    //////////////////////////////////////////
    // Wrapper methods
    //
    public int outcode( double x, double y ) {
        return rect.outcode( x, y );
    }

    public void setRect( double x, double y, double w, double h ) {
        rect.setRect( x, y, w, h );
    }

    public Rectangle2D createIntersection( Rectangle2D r ) {
        return rect.createIntersection( r );
    }

    public Rectangle2D createUnion( Rectangle2D r ) {
        return rect.createUnion( r );
    }

    public double getHeight() {
        return rect.getHeight();
    }

    public double getWidth() {
        return rect.getWidth();
    }

    public double getX() {
        return rect.getX();
    }

    public double getY() {
        return rect.getY();
    }

    public boolean isEmpty() {
        return rect.isEmpty();
    }

    public int hashCode() {
        return rect.hashCode();
    }

    public void add( double newx, double newy ) {
        rect.add( newx, newy );
    }

    public boolean contains( double x, double y ) {
        return rect.contains( x, y );
    }

    public void setFrame( double x, double y, double w, double h ) {
        rect.setFrame( x, y, w, h );
    }

    public boolean contains( double x, double y, double w, double h ) {
        return rect.contains( x, y, w, h );
    }

    public boolean intersects( double x, double y, double w, double h ) {
        return rect.intersects( x, y, w, h );
    }

    public boolean intersectsLine( double x1, double y1, double x2, double y2 ) {
        return rect.intersectsLine( x1, y1, x2, y2 );
    }

    public boolean intersectsLine( Line2D l ) {
        return rect.intersectsLine( l );
    }

    public int outcode( Point2D p ) {
        return rect.outcode( p );
    }

    public void add( Point2D pt ) {
        rect.add( pt );
    }

    public Rectangle2D getBounds2D() {
        return rect.getBounds2D();
    }

    public void add( Rectangle2D r ) {
        rect.add( r );
    }

    public void setRect( Rectangle2D r ) {
        rect.setRect( r );
    }

    public boolean equals( Object obj ) {
        return rect.equals( obj );
    }

    public PathIterator getPathIterator( AffineTransform at ) {
        return rect.getPathIterator( at );
    }

    public PathIterator getPathIterator( AffineTransform at, double flatness ) {
        return rect.getPathIterator( at, flatness );
    }

    public double getCenterX() {
        return rect.getCenterX();
    }

    public double getCenterY() {
        return rect.getCenterY();
    }

    public double getMaxX() {
        return rect.getMaxX();
    }

    public double getMaxY() {
        return rect.getMaxY();
    }

    public double getMinX() {
        return rect.getMinX();
    }

    public double getMinY() {
        return rect.getMinY();
    }

    public void setFrameFromCenter( double centerX, double centerY, double cornerX, double cornerY ) {
        rect.setFrameFromCenter( centerX, centerY, cornerX, cornerY );
    }

    public void setFrameFromDiagonal( double x1, double y1, double x2, double y2 ) {
        rect.setFrameFromDiagonal( x1, y1, x2, y2 );
    }

    public Rectangle getBounds() {
        return rect.getBounds();
    }

    public boolean contains( Point2D p ) {
        return rect.contains( p );
    }

    public Rectangle2D getFrame() {
        return rect.getFrame();
    }

    public void setFrame( Rectangle2D r ) {
        rect.setFrame( r );
    }

    public boolean contains( Rectangle2D r ) {
        return rect.contains( r );
    }

    public boolean intersects( Rectangle2D r ) {
        return rect.intersects( r );
    }

    public Object clone() {
        return rect.clone();
    }

    public void setFrame( Point2D loc, Dimension2D size ) {
        rect.setFrame( loc, size );
    }

    public void setFrameFromCenter( Point2D center, Point2D corner ) {
        rect.setFrameFromCenter( center, corner );
    }

    public void setFrameFromDiagonal( Point2D p1, Point2D p2 ) {
        rect.setFrameFromDiagonal( p1, p2 );
    }

    //////////////////////////////////////////
    // Inner classes
    //
    public static class StateDescriptor {
        private double x;
        private double y;
        private double w;
        private double h;

        public StateDescriptor() {
        }

        StateDescriptor( Rectangle2D rect ) {
            x = rect.getX();
            y = rect.getY();
            w = rect.getWidth();
            h = rect.getHeight();
        }

        //////////////////////////////////////
        // Generator
        //
        Rectangle2D generate() {
            return new Double( x, y, w, h );
        }

        //////////////////////////////////////
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

        public double getW() {
            return w;
        }

        public void setW( double w ) {
            this.w = w;
        }

        public double getH() {
            return h;
        }

        public void setH( double h ) {
            this.h = h;
        }
    }
}
