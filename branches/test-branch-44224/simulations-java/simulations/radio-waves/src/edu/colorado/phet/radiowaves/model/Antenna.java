/**
 * Class: Antenna Package: edu.colorado.phet.emf.model Author: Another Guy Date:
 * Jul 8, 2003
 */

package edu.colorado.phet.radiowaves.model;

import java.awt.geom.Point2D;

public class Antenna implements PositionConstraint {

    private Point2D end1;
    private Point2D end2;
    private double maxX;
    private double minX;
    private double maxY;
    private double minY;
    private double m;
    private double b;
    private double r;
    private double theta;

    public Antenna( Point2D end1, Point2D end2 ) {
        this.end1 = end1;
        this.end2 = end2;
        this.maxX = Math.max( end1.getX(), end2.getX() );
        this.maxY = Math.max( end1.getY(), end2.getY() );
        this.minX = Math.min( end1.getX(), end2.getX() );
        this.minY = Math.min( end1.getY(), end2.getY() );

        r = Math.sqrt( ( Math.pow( end1.getX() - end2.getX(), 2 ) + Math.pow( end1.getY() - end2.getY(), 2 ) ) );
        theta = Math.atan( ( end1.getY() - end2.getY() ) / ( end1.getX() - end2.getX() ) );
        if ( end1.getX() == end2.getX() ) {
            theta = Math.PI / 2;
            m = Double.POSITIVE_INFINITY;
            b = Double.NaN;
        }
        else {
            m = ( end1.getY() - end2.getY() ) / ( end1.getX() - end2.getX() );
            b = end1.getY() - ( end1.getX() * m );
        }
    }

    public Point2D constrainPosition( Point2D pos ) {
        if ( pos.getX() > this.maxX ) {
            pos.setLocation( this.maxX, getYForX( this.maxX, pos.getY() ) );
        }
        if ( pos.getX() < this.minX ) {
            pos.setLocation( this.minX, getYForX( this.minX, pos.getY() ) );
        }
        if ( pos.getY() > this.maxY ) {
            pos.setLocation( getXForY( this.maxY, pos.getX() ), this.maxY );
        }
        if ( pos.getY() < this.minY ) {
            pos.setLocation( getXForY( this.minY, pos.getX() ), this.minY );
        }
        pos.setLocation( pos.getX(), getYForX( pos.getX(), pos.getY() ) );
        return pos;
    }

    private double getYForX( double x, double y ) {
        if ( m == Double.POSITIVE_INFINITY ) {
            return y;
        }
        else {
            return m * x + b;
        }
    }

    private double getXForY( double y, double x ) {
        if ( m == 0 || m == Double.POSITIVE_INFINITY ) {
            return x;
        }
        else {
            return ( y - b ) / m;
        }
    }

    public Point2D getEnd1() {
        return end1;
    }

    public Point2D getEnd2() {
        return end2;
    }

    public double getMaxX() {
        return maxX;
    }

    public double getMinX() {
        return minX;
    }

    public double getMaxY() {
        return maxY;
    }

    public double getMinY() {
        return minY;
    }

    public double getM() {
        return m;
    }

    /**
     * Test driver
     *
     * @param args
     */
    public static void main( String[] args ) {
        Antenna a = new Antenna( new Point2D.Double( 2, 1 ), new Point2D.Double( 2, 6 ) );
        Point2D.Double p = new Point2D.Double( 2, 7 );
        p = (Point2D.Double) a.constrainPosition( p );
        System.out.println( "-->" + p.getX() + "," + p.getY() );
    }
}
