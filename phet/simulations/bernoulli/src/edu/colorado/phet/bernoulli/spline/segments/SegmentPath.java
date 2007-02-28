package edu.colorado.phet.bernoulli.spline.segments;

import java.awt.geom.Point2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Jun 17, 2003
 * Time: 1:34:41 PM
 * Copyright (c) Jun 17, 2003 by Sam Reid
 */
public class SegmentPath {
    ArrayList segs = new ArrayList();
    private double lastx;
    private double lasty;
    boolean init = false;

    public void addSegment( Segment wire ) {
        segs.add( wire );
    }

    public Point2D.Double getPosition( double dist ) {
        if( dist < 0 ) {
            return null;
        }
        double runningDist = 0;
        double previousWireDist = 0;
        for( int i = 0; i < segs.size(); i++ ) {
            Segment linearWire = (Segment)segs.get( i );
            runningDist += linearWire.getLength();
            if( dist < runningDist ) {
                return linearWire.getPosition( dist - previousWireDist );
            }
            previousWireDist += linearWire.getLength();
        }
        return null;
    }

    public Segment getSegment( double dist ) {
        if( dist < 0 ) {
            return null;
        }
        double runningDist = 0;
        for( int i = 0; i < segs.size(); i++ ) {
            Segment linearWire = (Segment)segs.get( i );
            runningDist += linearWire.getLength();
            if( dist < runningDist ) {
                return linearWire;
            }
        }
        return null;
    }

    public double getLength() {
        double length = 0;
        for( int i = 0; i < segs.size(); i++ ) {
            Segment linearWire = (Segment)segs.get( i );
            length += linearWire.getLength();
        }
        return length;
    }

    public int numSegments() {
        return segs.size();
    }

    public Segment segmentAt( int i ) {
        return (Segment)segs.get( i );
    }

    public void startAt( double x, double y ) {
        this.lastx = x;
        this.lasty = y;
        init = true;
    }

    public Point2D.Double getFirstIntersection( Segment seg ) {
        for( int i = 0; i < segs.size(); i++ ) {
            Segment segment = (Segment)segs.get( i );
            Point2D.Double intersect = segment.getIntersection( seg );
            if( intersect != null ) {
                return intersect;
            }
        }
        return null;
    }

    public void lineTo( double x, double y ) {
        if( !init ) {
            throw new RuntimeException( "Missing startAt() call." );
        }
        Segment seg = new Segment( lastx, lasty, x, y );
        addSegment( seg );
        lastx = x;
        lasty = y;
    }
}
