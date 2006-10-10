/* Copyright 2004, Sam Reid */
package edu.colorado.phet.energyskatepark.model;

import edu.colorado.phet.energyskatepark.model.spline.AbstractSpline;
import edu.colorado.phet.energyskatepark.model.spline.Segment;
import edu.colorado.phet.energyskatepark.model.spline.SegmentPath;
import edu.umd.cs.piccolo.util.PBounds;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

/**
 * User: Sam Reid
 * Date: Sep 29, 2005
 * Time: 10:23:11 AM
 * Copyright (c) Sep 29, 2005 by Sam Reid
 */

public class SplineLogic {
    private Body body;

    public SplineLogic( Body body ) {
        this.body = body;
    }

    public Segment guessSegment( AbstractSpline spline ) throws NullIntersectionException {
        double x = guessPositionAlongSpline( spline );
        return spline.getSegmentPath().getSegmentAtPosition( x );
    }

    public double guessPositionAlongSpline( AbstractSpline spline ) throws NullIntersectionException {
        ArrayList overlap = getOverlappingSegments( spline );

        //return the centroid.
        Rectangle2D rect = null;
        for( int i = 0; i < overlap.size(); i++ ) {
            Segment segment = (Segment)overlap.get( i );
            if( rect == null ) {
                rect = segment.toLine2D().getBounds2D();
            }
            else {
                rect = rect.createUnion( segment.toLine2D().getBounds2D() );
            }
        }

        if( rect == null ) {
            throw new NullIntersectionException( "No contact with spline." );
//            return 0.0;
        }
        Point2D center = new PBounds( rect ).getCenter2D();
        return getClosestScalar( spline, center );
    }

    public ArrayList getOverlappingSegments( AbstractSpline spline ) {
        SegmentPath segmentPath = spline.getSegmentPath();
        Shape bodyShape = body.getLocatedShape();
        //find all segments that overlap.
        ArrayList overlap = new ArrayList();
        for( int i = 0; i < segmentPath.numSegments(); i++ ) {
            Segment segment = segmentPath.segmentAt( i );
            if( bodyShape.getBounds2D().intersects( segment.getShape().getBounds2D() ) ) {//make sure we need areas
                Area a = new Area( bodyShape );
                a.intersect( new Area( segment.getShape() ) );
                if( !a.isEmpty() ) {
                    overlap.add( segment );
                }
            }
        }
        return overlap;
    }

    public double getClosestScalar( AbstractSpline spline, Point2D center ) {
        SegmentPath segmentPath = spline.getSegmentPath();
        double closestPosition = Double.POSITIVE_INFINITY;
        Segment bestSegment = null;
        for( int i = 0; i < segmentPath.numSegments(); i++ ) {
            Segment seg = segmentPath.segmentAt( i );
            double distance = center.distance( seg.getCenter2D() );
            if( distance < closestPosition ) {
                bestSegment = seg;
                closestPosition = distance;
            }
        }
        return segmentPath.getScalarPosition( bestSegment );
    }

    public double guessPositionAlongSpline( AbstractSpline spline, double lastScalarPosition ) throws NullIntersectionException {
        SegmentPath segmentPath = spline.getSegmentPath();
        Shape bodyShape = body.getLocatedShape();
        //find all segments that overlap.
        ArrayList overlap = new ArrayList();
        for( int i = 0; i < segmentPath.numSegments(); i++ ) {
            Segment segment = segmentPath.segmentAt( i );
            double ds = Math.abs( segmentPath.getScalarPosition( segment ) - lastScalarPosition );
            if( ds < 50 ) {//only consider nearby segments.
                if( bodyShape.getBounds2D().intersects( segment.getShape().getBounds2D() ) ) {//make sure we need areas
                    Area a = new Area( bodyShape );
                    a.intersect( new Area( segment.getShape() ) );
                    if( !a.isEmpty() ) {
                        overlap.add( segment );
                    }
                }
            }
        }

//        System.out.println( "overlap.size() = " + overlap.size() );
        //return the centroid.
        Rectangle2D rect = null;
        for( int i = 0; i < overlap.size(); i++ ) {
            Segment segment = (Segment)overlap.get( i );
            if( rect == null ) {
                rect = segment.toLine2D().getBounds2D();
            }
            else {
                rect = rect.createUnion( segment.toLine2D().getBounds2D() );
            }
        }

        if( rect == null ) {
            throw new NullIntersectionException( "No contact with spline." );
//            return 0.0;
        }
        Point2D center = new PBounds( rect ).getCenter2D();
        return getClosestScalar( spline, center, lastScalarPosition );
    }

    public double getClosestScalar( AbstractSpline spline, Point2D center, double lastScalarPosition ) {
        SegmentPath segmentPath = spline.getSegmentPath();
        double closestPosition = Double.POSITIVE_INFINITY;
        Segment bestSegment = null;
//        double thresholdScalar = 50;
        double thresholdScalar = 50;
        for( int i = 0; i < segmentPath.numSegments(); i++ ) {
            Segment seg = segmentPath.segmentAt( i );
            double segmentScalar = segmentPath.getScalarPosition( seg );
            double ds = Math.abs( segmentScalar - lastScalarPosition );


            double distance = center.distance( seg.getCenter2D() );

            if( distance < closestPosition && ds < thresholdScalar ) {
                bestSegment = seg;
                closestPosition = distance;
            }
        }
        return segmentPath.getScalarPosition( bestSegment );
    }
}
