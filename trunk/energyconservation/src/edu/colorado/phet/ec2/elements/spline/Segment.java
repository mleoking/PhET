package edu.colorado.phet.ec2.elements.spline;

//import edu.colorado.phet.circuitconstructionkit.wire.util.Point2DDoubleMath;

import edu.colorado.phet.coreadditions.math.PhetVector;

import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Segment {
    PhetVector start;
    PhetVector finish;
    PhetVector unitVector;
    double length;
    ArrayList p = new ArrayList();
    double angle;

    public Segment( double x, double y, double x2, double y2 ) {
        this( new Point2D.Double( x, y ), new Point2D.Double( x2, y2 ) );
    }

    public Segment( Point2D.Double start, Point2D.Double finish ) {
        this.start = new PhetVector( start.x, start.y );
        this.finish = new PhetVector( finish.x, finish.y );
        this.length = finish.distance( start );
        this.unitVector = this.finish.getSubtractedInstance( this.start );
        this.unitVector.normalize();
        this.angle = unitVector.getAngle();
    }

    public double getLength() {
        return length;
    }

    public String toString() {
        return "Start=" + start + ", finish=" + finish + ", length=" + length;
    }

    public Point2D.Double getPosition( double dist ) {
        //Get a unit vector in the right direction.
        PhetVector copy = new PhetVector( unitVector );
        copy.setMagnitude( dist );
        copy.add( start );
        return new Point2D.Double( copy.getX(), copy.getY() );
    }

    public Point2D getStartPoint() {
        return new Point2D.Double( start.getX(), start.getY() );
    }

    public Point2D getFinishPoint() {
        return new Point2D.Double( finish.getX(), finish.getY() );
    }

    public double getAngle() {
        return angle;
    }

    public PhetVector getDirectionVector() {
        return unitVector;
    }
//    public PhetVector getDirectionVector() {
//        return this.
//        PhetVector vec = new PhetVector(finish.getX() - start.getX(), finish.getY() - start.getY());
//        return vec.getNormalizedInstance();
//    }
}
