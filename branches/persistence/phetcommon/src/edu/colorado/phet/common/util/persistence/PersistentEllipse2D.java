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

import java.awt.geom.*;
import java.awt.*;

/**
 * PersistentPoint2D
 * <p/>
 * This class is provided as a wrapper for Point2D for use in applications that
 * do persistence using XMLEncoder & XMLDecoder
 *
 * @author Ron LeMaster
 * @version $Revision$
 */
public class PersistentEllipse2D extends Ellipse2D implements Persistent {
    private Ellipse2D ellipse = new Double();

    public PersistentEllipse2D() {
    }

    public PersistentEllipse2D( Ellipse2D ellipse ) {
        this.ellipse = ellipse;
    }

    ////////////////////////////////////////////
    // Persistence setters and getters
    //
    public StateDescriptor getState() {
        return new Ellipse2DDescriptor( ellipse );
    }

    public void setState( StateDescriptor stateDescriptor ) {
        ellipse = new Ellipse2D.Double( );
        stateDescriptor.setState( this );
    }

    ////////////////////////////////////////////
    // Wrapper methods
    //
    public boolean contains( double x, double y ) {
        return ellipse.contains( x, y );
    }

    public boolean contains( double x, double y, double w, double h ) {
        return ellipse.contains( x, y, w, h );
    }

    public boolean intersects( double x, double y, double w, double h ) {
        return ellipse.intersects( x, y, w, h );
    }

    public PathIterator getPathIterator( AffineTransform at ) {
        return ellipse.getPathIterator( at );
    }

    public double getCenterX() {
        return ellipse.getCenterX();
    }

    public double getCenterY() {
        return ellipse.getCenterY();
    }

    public double getMaxX() {
        return ellipse.getMaxX();
    }

    public double getMaxY() {
        return ellipse.getMaxY();
    }

    public double getMinX() {
        return ellipse.getMinX();
    }

    public double getMinY() {
        return ellipse.getMinY();
    }

    public void setFrameFromCenter( double centerX, double centerY, double cornerX, double cornerY ) {
        ellipse.setFrameFromCenter( centerX, centerY, cornerX, cornerY );
    }

    public void setFrameFromDiagonal( double x1, double y1, double x2, double y2 ) {
        ellipse.setFrameFromDiagonal( x1, y1, x2, y2 );
    }

    public Rectangle getBounds() {
        return ellipse.getBounds();
    }

    public boolean contains( Point2D p ) {
        return ellipse.contains( p );
    }

    public Rectangle2D getFrame() {
        return ellipse.getFrame();
    }

    public void setFrame( Rectangle2D r ) {
        ellipse.setFrame( r );
    }

    public boolean contains( Rectangle2D r ) {
        return ellipse.contains( r );
    }

    public boolean intersects( Rectangle2D r ) {
        return ellipse.intersects( r );
    }

    public Object clone() {
        return ellipse.clone();
    }

    public void setFrame( Point2D loc, Dimension2D size ) {
        ellipse.setFrame( loc, size );
    }

    public PathIterator getPathIterator( AffineTransform at, double flatness ) {
        return ellipse.getPathIterator( at, flatness );
    }

    public void setFrameFromCenter( Point2D center, Point2D corner ) {
        ellipse.setFrameFromCenter( center, corner );
    }

    public void setFrameFromDiagonal( Point2D p1, Point2D p2 ) {
        ellipse.setFrameFromDiagonal( p1, p2 );
    }

    public double getHeight() {
        return ellipse.getHeight();
    }

    public double getWidth() {
        return ellipse.getWidth();
    }

    public double getX() {
        return ellipse.getX();
    }

    public double getY() {
        return ellipse.getY();
    }

    public boolean isEmpty() {
        return ellipse.isEmpty();
    }

    public void setFrame( double x, double y, double w, double h ) {
        ellipse.setFrame( x, y, w, h );
    }

    public Rectangle2D getBounds2D() {
        return ellipse.getBounds2D();
    }

    //////////////////////////////////////
    // Inner classes
    //
    public static class Ellipse2DDescriptor implements StateDescriptor {
        private double x;
        private double y;
        private double width;
        private double height;

        public Ellipse2DDescriptor() {
        }

        Ellipse2DDescriptor( Ellipse2D e ) {
            x = e.getX();
            y = e.getY();
            width = e.getWidth();
            height = e.getHeight();
        }

        ////////////////////////////////////
        // Generator
        //
        public void setState( Persistent persistentObject ) {
            PersistentEllipse2D ellipse = (PersistentEllipse2D)persistentObject;
            ellipse.setFrame( x, y, width, height );
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

        public double getWidth() {
            return width;
        }

        public void setWidth( double width ) {
            this.width = width;
        }

        public double getHeight() {
            return height;
        }

        public void setHeight( double height ) {
            this.height = height;
        }
    }
}
