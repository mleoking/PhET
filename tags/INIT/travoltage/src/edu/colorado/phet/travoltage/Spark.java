package edu.colorado.phet.travoltage;

import edu.colorado.phet.common.gui.Painter;

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

    public class SparkPath implements Painter {
        Point[] pts;

        public SparkPath( Point[] pts ) {
            this.pts = pts;
        }

        public void paint( Graphics2D g ) {
            g.setStroke( new BasicStroke( 4 ) );
            g.setColor( Color.white );
            //edu.colorado.phet.common.util.Debug.traceln("Drawing: "+pts.length+" vertices.");
            for( int i = 1; i < pts.length; i++ ) {
                //edu.colorado.phet.common.util.Debug.traceln(i+ "At point: "+pts[i]);
                g.drawLine( pts[i - 1].x, pts[i - 1].y, pts[i].x, pts[i].y );
            }
            g.setStroke( new BasicStroke( 1 ) );
            g.setColor( Color.blue );
            //edu.colorado.phet.common.util.Debug.traceln("Drawing: "+pts.length+" vertices.");
            for( int i = 1; i < pts.length; i++ ) {
                //edu.colorado.phet.common.util.Debug.traceln(i+ "At point: "+pts[i]);
                g.drawLine( pts[i - 1].x, pts[i - 1].y, pts[i].x, pts[i].y );
            }
        }
    }

    public SparkRunner newSparkRunner( Component c, int time, int napTime ) {
        return new SparkRunner( c, time, napTime );
    }

    public class SparkRunner implements Painter, Runnable {
        SparkPath path;
        Component paintMe;
        int time;
        int nap; //20 looks ok.

        public SparkRunner( Component paintMe, int time, int nap ) {
            this.nap = nap;
            this.time = time;
            this.paintMe = paintMe;
        }

        public void run() {
            int t = 0;
            int NAP = 20;
            while( true ) {
                edu.colorado.phet.common.util.ThreadHelper.quietNap( NAP );
                t += NAP;
                this.path = toSparkPath( 100 );
                paintMe.repaint();
                if( t > time ) {
                    break;
                }
            }
        }

        public void paint( Graphics2D g ) {
            if( path != null ) {
                path.paint( g );
            }
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
                //edu.colorado.phet.common.util.Debug.traceln("Distance="+prev.distance(sink)+", less than thershold: "+thresh);
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
        //edu.colorado.phet.common.util.Debug.traceln("Theta="+theta+", NewTheta="+thetaNew);

        Point newPt = new Point( (int)( prev.x - segLength * Math.cos( thetaNew ) ), (int)( prev.y - segLength * Math.sin( thetaNew ) ) );
        //	edu.colorado.phet.common.util.Debug.traceln("New point="+newPt);
        return newPt;
        //angle up or down.
    }
}
