package edu.colorado.phet.bernoulli.spline.segments;

//import edu.colorado.phet.circuitconstructionkit.wire.util.Point2DDoubleMath;

import edu.colorado.phet.coreadditions.math.PhetVector;

import java.awt.geom.Point2D;

public class Segment {
    PhetVector start;
    PhetVector finish;
    PhetVector unitVector;
    double length;
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

    /**
     * Returns null if there is no intersection point.
     */
    public Point2D.Double getIntersection( Segment seg ) {
        SlopeInterceptLine line = toSlopeInterceptLine();
        SlopeInterceptLine line2 = seg.toSlopeInterceptLine();
        Point2D.Double intersection = line2.getIntersectionPoint( line );
        PhetVector pv = new PhetVector( intersection.x, intersection.y );
        boolean inRange = inRange( pv );
        boolean otherInRange = seg.inRange( pv );
        if( inRange && otherInRange ) {
            return intersection;
        }
        else {
            return null;
        }
    }

    private double dot( PhetVector a, PhetVector b ) {
        return a.getX() * b.getX() + a.getY() * b.getY();
    }

    private boolean inRange( PhetVector pv ) {
        double dist = pv.getSubtractedInstance( start ).getMagnitude();
        double myDist = finish.getSubtractedInstance( start ).getMagnitude();
        double dot = pv.getSubtractedInstance( start ).dot( finish.getSubtractedInstance( start ) );
//                double epsilon=.1;
//        return dist<= myDist+epsilon && dot >= 0-epsilon;
        return dist <= myDist && dot >= 0;

//        return withinError(dist,myDist,epsilon)&&withinError(dot,0,epsilon);
    }

//    private boolean withinError(double a, double b, double epsilon) {
//        return Math.abs(a - b) <= epsilon;
//    }
//
//    private boolean inRange2(PhetVector pv) {
//        PhetVector dv = pv.getSubtractedInstance(start);
//        PhetVector vector = finish.getSubtractedInstance(start);
//        PhetVector normalVector = vector.getNormalizedInstance();
////        double dot=normalVector.dot(dv);
//        double dot = dot(dv, normalVector);
//        double dist = dot / vector.getMagnitude();
//        if (dot > 1 || dot < 0)
//            return false;
//        return true;
//    }

    private SlopeInterceptLine toSlopeInterceptLine() {
        double rise = finish.getY() - start.getY();
        double run = finish.getX() - start.getX();
        if( run == 0 ) {
            run = Math.pow( 10, -10 );//almost zero
        }
        double m = rise / run;
        double intercept = start.getY() - m * start.getX();
        return new SlopeInterceptLine( m, intercept );
    }
}

