/* Copyright 2004, Sam Reid */
package edu.colorado.phet.ec3.model.spline;

import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * User: Sam Reid
 * Date: Sep 26, 2005
 * Time: 9:24:52 PM
 * Copyright (c) Sep 26, 2005 by Sam Reid
 */

public class SegmentPath {
    private ArrayList segments = new ArrayList();
    //    private boolean dirty = true;
    private HashMap table = new HashMap();

    public SegmentPath() {
    }

    public void addSegment( Segment segment ) {
        segments.add( segment );
//        dirty = true;
    }

    public Point2D evaluate( double distAlongSpline ) {
        double offset = 0.0;
        for( int i = 0; i < segments.size(); i++ ) {
            Segment segment = (Segment)segments.get( i );
            if( distAlongSpline < segment.getLength() + offset ) {
//                System.out.println( "@" + System.currentTimeMillis() + ", Matched on segment: " + i );
                return segment.evaluate( distAlongSpline - offset );
            }
            else {
                offset += segment.getLength();
            }
        }
        System.out.println( "No segment found: dist=" + distAlongSpline + ", length=" + getLength() );
        return new Point2D.Double( 0, 0 );//todo error
    }

    public double getLength() {
        double length = 0;
        for( int i = 0; i < segments.size(); i++ ) {
            Segment segment = (Segment)segments.get( i );
            length += segment.getLength();
        }
        return length;
    }

    public int numSegments() {
        return segments.size();
    }

    public Segment segmentAt( int i ) {
        return (Segment)segments.get( i );
    }

    public double getScalarPosition( Segment seg ) {
        if( table.containsKey( seg ) ) {
            Double value = (Double)table.get( seg );
            return value.doubleValue();
        }
        double length = 0;
        for( int i = 0; i < segments.size(); i++ ) {
            Segment segment = (Segment)segments.get( i );
            if( segment == seg ) {
                table.put( seg, new Double( length ) );
                return length;
            }
            length += segment.getLength();
        }
        throw new RuntimeException( "No such segment: " + seg );
    }

    public Segment getSegmentAtPosition( double position ) {
        double offset = 0.0;
        for( int i = 0; i < segments.size(); i++ ) {
            Segment segment = (Segment)segments.get( i );
            if( position < segment.getLength() + offset ) {
                return segment;
            }
            else {
                offset += segment.getLength();
            }
        }
        System.out.println( "No segment found: dist=" + position + ", length=" + getLength() );
        return null;
    }

    public boolean intersects( Rectangle2D r ) {
        for( int i = 0; i < segments.size(); i++ ) {
            Segment segment = (Segment)segments.get( i );
            if( segment.getShape().getBounds2D().intersects( r ) ) {
                Area a = new Area( segment.getShape() );
                Area b = new Area( r );
                a.intersect( b );
                if( !a.isEmpty() ) {
                    return true;
                }
            }
        }
        return false;
    }
}
