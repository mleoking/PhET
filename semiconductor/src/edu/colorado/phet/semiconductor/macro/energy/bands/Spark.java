package edu.colorado.phet.semiconductor.macro.energy.bands;

import java.awt.*;
import java.util.Random;
import java.util.Vector;

public class Spark {
    Point source;
    Point sink;
    double maxDTheta;
    Random randy;
    double threshold;
    double segLength;

    public Spark( Point source, Point sink, double maxDTheta, double threshold, double segLength ) {
        this.segLength = segLength;
        this.source = source;
        this.sink = sink;
        this.maxDTheta = maxDTheta;
        this.threshold = threshold;
        randy = new Random();
    }

    public void setSink( Point p ) {
        this.sink = p;
    }

    public class SparkPath {
        Point[] pts;

        public SparkPath( Point[] pts ) {
            this.pts = pts;
        }

    }

    public SparkPath toSparkPath( int maxPts ) {
        return new SparkPath( newPath( maxPts ) );
    }

    public Point[] newPath( int maxPts ) {
        /**Aim vaguely the direction of the sink, with some leeway.*/
        Vector pts = new Vector();
        Point prev = source;
        pts.add( source );
        for( int i = 0; i < maxPts; i++ ) {
            if( prev.distance( sink ) < threshold ) {
                //util.Debug.traceln("Distance="+prev.distance(sink)+", less than thershold: "+thresh);
                pts.add( sink );
                break;
            }
            Point p = nextPoint( prev );
            if( p == null ) {
                break;
            }
            pts.add( p );
            prev = p;
        }
        //System.out.println("Generated path: "+pts);
        return (Point[])pts.toArray( new Point[0] );
    }

    public double getAngle( double x, double y ) {
        return Math.atan2( y, x );
    }

    public Point nextPoint( Point prev ) {
        Point diff = new Point( prev.x - sink.x, prev.y - sink.y );
        double theta = getAngle( diff.x, diff.y );
        double dTheta = ( randy.nextDouble() - .5 ) * 2 * maxDTheta;
        double thetaNew = theta + dTheta;
        //util.Debug.traceln("Theta="+theta+", NewTheta="+thetaNew);

        Point newPt = new Point( (int)( prev.x - segLength * Math.cos( thetaNew ) ), (int)( prev.y - segLength * Math.sin( thetaNew ) ) );
        //	util.Debug.traceln("New point="+newPt);
        return newPt;
        //angle up or down.
    }
}
